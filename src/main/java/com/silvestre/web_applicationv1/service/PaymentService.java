package com.silvestre.web_applicationv1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.config.AdminConfig;
import com.silvestre.web_applicationv1.entity.*;
import com.silvestre.web_applicationv1.enums.*;
import com.silvestre.web_applicationv1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ObjectMapper objectMapper;
    private final QuotationRepository quotationRepository;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;

    @Autowired
    private CalendarEventRepository calendarEventRepository;

    @Autowired
    private CalendarAvailabilityService calService;
    @Autowired
    private CalendarAvailabilityRepository calRepo;
    @Autowired
    private CalendarCalendarsRepository calendarCalendarsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private QuotationRedisService quotationRedisService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private AdminConfig adminConfig;

    @Autowired
    private UserService userService;


    @Autowired
    private ScheduledNotificationService scheduledNotificationService;

    @Transactional(rollbackFor = Exception.class)
    public void processWebhook(String payload, String signature) throws Exception {
        // TODO: verify signature using webhook secret (important for security)




        JsonNode root = objectMapper.readTree(payload);

        JsonNode dataNode = root.path("data").path("attributes").path("data");
        JsonNode attributesNode = dataNode.path("attributes");

        String status = attributesNode.path("status").asText();
        String quotationIdStr = attributesNode.path("metadata").path("quotationId").asText();
        String eventDateStr = attributesNode.path("metadata").path("eventDate").asText();
        System.out.println(eventDateStr);

        LocalDateTime eventDateTime = null;

        if (eventDateStr.startsWith("[")) {
            // If eventDate is in array format like "[2025, 9, 28]"
            ObjectMapper mapper = new ObjectMapper();
            JsonNode arrNode = mapper.readTree(eventDateStr);

            // Extract the year, month, and day
            int year = arrNode.get(0).asInt();
            int month = arrNode.get(1).asInt();
            int day = arrNode.get(2).asInt();

            // Create LocalDate and convert it to LocalDateTime (at start of day)
            LocalDate date = LocalDate.of(year, month, day);
            eventDateTime = date.atStartOfDay();
        } else {
            // Otherwise, treat eventDate as a standard ISO string (like "2025-09-28T00:00:00.000Z")
            eventDateTime =  LocalDate.parse(eventDateStr).atStartOfDay();
        }

        // Now, process the eventDateTime as needed in your application
        System.out.println("Parsed Event DateTime: " + eventDateTime);

        String paymentType = attributesNode.path("metadata").path("paymentType").asText();
        System.out.println(paymentType);


        // Find related quotation & user
        Long quotationId = Long.valueOf(quotationIdStr);
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));


        quotationRedisService.removeQuotation(quotationId);

        if(!quotationRedisService.isQuotationActive(quotationId)){
            System.out.println("quotation ttl removed from redis");
        }

        User user = quotation.getUser();

        // Map payment from webhook
        Payments payment = mapFlattenedWebhook(root, user, quotation);

        LocalDateTime finalEventDateTime = eventDateTime;
        Booking booking = bookingService.findByQuotation(quotation)
                .orElseGet(() -> {
                    Booking newBooking = new Booking();
                    newBooking.setQuotation(quotation);
                    newBooking.setUser(user);
                    newBooking.setBookingStatus(BookingStatus.PENDING);
                    newBooking.setRequestedDate(finalEventDateTime);
                    return bookingService.save(newBooking);
                });


        booking.setTotalAmount(quotation.getTotalAmount());

        quotation.setRequestedEventDate(eventDateTime.toLocalDate());
        booking.setRequestedDate(eventDateTime);


        BigDecimal quotationInCents = quotation.getTotal().multiply(BigDecimal.valueOf(100));
        BigDecimal totalPaid = paymentRepository.sumOfAllPaymentsForQuotation(quotation.getId(), PaymentStatus.PAID);
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;

        BigDecimal totalWithThisPayment = totalPaid.add(payment.getAmount());
        BigDecimal remainingBalance = quotationInCents.subtract(totalWithThisPayment);

        if ("paid".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.PAID);

            booking.setBalance(remainingBalance);
            booking.setAmountPaid(totalWithThisPayment);

            String fullName= user.getFirstname() + " " + user.getLastname();


            if(paymentType.equals("reservation"))
            {

                CalendarAvailability calendarAvailability = new CalendarAvailability();
                calendarAvailability.setDate(eventDateTime.toLocalDate());
                calendarAvailability.setStatus("BOOKED");
                calendarAvailability.setReason("reserved for " + fullName +"'s event" );
                calService.save(calendarAvailability);

                booking.setBookingStatus(BookingStatus.RESERVED);
                quotation.setStatus(QuotationStatus.RESERVED);
                quotationRepository.save(quotation);
            }
            if(paymentType.equals("book")){
            quotation.setStatus(QuotationStatus.BOOKED);

                CalendarAvailability existing = calRepo.findById(eventDateTime.toLocalDate()).orElse(new CalendarAvailability());

                existing.setDate(eventDateTime.toLocalDate());
                existing.setStatus("BOOKED");
                existing.setReason("booked for "+ fullName +"'s event");
                calService.save(existing);
                booking.setBookingStatus(BookingStatus.BOOKED);
                quotationRepository.save(quotation);
            }
            if (remainingBalance.compareTo(BigDecimal.ZERO) == 0) {
                booking.setBookingStatus(BookingStatus.BOOKED_AND_FULLY_PAID);
                quotation.setStatus(QuotationStatus.PAID);
                quotationRepository.save(quotation);
            }
        } else {
            switch (status.toLowerCase()) {
                case "pending" -> payment.setStatus(PaymentStatus.PAYMENT_PENDING);
                case "failed" -> payment.setStatus(PaymentStatus.PAYMENT_FAILED);
                case "refunded" -> payment.setStatus(PaymentStatus.REFUNDED);
            }
        }
        payment.setRemainingBalance(remainingBalance);


        BigDecimal totalPaidInPesos = totalWithThisPayment.divide(BigDecimal.valueOf(100));
        boolean isFullPayment = totalPaidInPesos.compareTo(quotation.getTotal()) >= 0;

        payment.setPaymentType(isFullPayment ? "Full" : "Partial");


        String externalRef = attributesNode.path("external_reference_number").asText(null);
        payment.setExternalReference(externalRef);

        BigDecimal totalAmount = quotation.getTotal().multiply(BigDecimal.valueOf(100));
        payment.setTotalDue(totalAmount);


        Long calendarId = 4L;
        CalendarCalendars calendarCalendars= calendarCalendarsRepository.findById(calendarId).orElseThrow(()->
                 new ResourceNotFoundException("calendar Id not found"));

        AtomicBoolean isNewEvent= new AtomicBoolean(false);


        CalendarEvent calendarEvent = calendarEventRepository.findByQuotation(quotation)
                .orElseGet(() -> {
                    CalendarEvent newEvent = new CalendarEvent();
                    newEvent.setQuotation(quotation);
                    newEvent.setBooking(booking);
                    newEvent.setCalendar(calendarCalendars);
                    newEvent.setEventCreator(user);
                    newEvent.setTitle(user.getFirstname() + "'s " + quotation.getEventType());
                    Invitation invitation = new Invitation();
                    invitation.setStatus(RSVP.INVITED);
                    invitation.setUser(user);
                    invitation.setCalendarEvent(newEvent);


                    User admin = userRepository.findFirstByRole(Role.ADMIN)
                            .stream().findFirst()
                            .orElseThrow(() -> new RuntimeException("Admin not found"));


                    Invitation adminInvite = new Invitation();
                    adminInvite.setStatus(RSVP.INVITED);
                    adminInvite.setUser(admin);
                    adminInvite.setCalendarEvent(newEvent);

                    newEvent.setInvitations(List.of(invitation,adminInvite));

                    isNewEvent.set(true);

                    return newEvent;
                });

// Always update dynamic fields
        calendarEvent.setLocation(quotation.getVenue().getName());
        calendarEvent.setEventCategory(quotation.getStatus() == QuotationStatus.BOOKED
                ? EventCategory.BOOKING
                : EventCategory.RESERVATION);

        calendarEvent.setTitle(user.getFirstname() + " " + user.getLastname() + " " + quotation.getEventType());

        LocalDate eventDate = quotation.getRequestedEventDate();
        LocalDateTime startOfDay = eventDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        ZoneOffset offset = ZoneOffset.ofHours(8);

        calendarEvent.setStartTime(startOfDay.atOffset(offset));
        calendarEvent.setEndTime(endOfDay.atOffset(offset));
        calendarEvent.setAllDay(true);

         CalendarEvent calSaved=calendarEventRepository.save(calendarEvent);

        if (isNewEvent.get()) {
            scheduledNotificationService.scheduleEventNotifications(calSaved);
        }

        booking.addPayment(payment);
        bookingService.save(booking);

        Notification customerNotification = new Notification();
        customerNotification.setType("PAYMENT NOTIFICATION");
        customerNotification.setTitle("Payment has been received");
        customerNotification.setMessage("Your payment for booking #" + quotation.getId() +" has been received");
        customerNotification.setUser(quotation.getUser());



        Notification adminNotification = new Notification();

        User admin = userService.findUserById(adminConfig.getAdminId());
        adminNotification.setTitle("Payment Received");

        String customerName = quotation.getUser().getFirstname() + " " + quotation.getUser().getLastname();
        adminNotification.setMessage("Payment received from " + customerName +" for booking "+ quotation.getId());
        adminNotification.setUser(admin);
        adminNotification.setType("PAYMENT NOTIFICATION");

        notificationRepository.save(customerNotification);
        notificationRepository.save(adminNotification);


        simpMessagingTemplate.convertAndSendToUser(
                quotation.getUser().getEmail(),               // username used in WebSocket session
                "/queue/notifications",        // matches your @SendToUser
                customerNotification
        );

        simpMessagingTemplate.convertAndSendToUser(
                admin.getEmail(),               // username used in WebSocket session
                "/queue/notifications",        // matches your @SendToUser
                adminNotification
        );
    }

    public Payments mapFlattenedWebhook(JsonNode json, User user, Quotation quotation) {
        Payments payment = new Payments();

        JsonNode dataNode = json.path("data").path("attributes").path("data");
        JsonNode attributesNode = dataNode.path("attributes");

        // relationships
        payment.setUser(user);
        payment.setQuotation(quotation);

        // paymongo info
        payment.setPaymongoPaymentId(dataNode.path("id").asText());
        payment.setBalanceTransactionId(attributesNode.path("balance_transaction_id").asText(null));
        payment.setStatus(PaymentStatus.valueOf(attributesNode.path("status").asText().toUpperCase()));
        payment.setPaymentType("full"); // default

        // amounts
        payment.setAmount(BigDecimal.valueOf(attributesNode.path("amount").asLong()));
        payment.setFee(BigDecimal.valueOf(attributesNode.path("fee").asLong()));
        payment.setNetAmount(BigDecimal.valueOf(attributesNode.path("net_amount").asLong()));
        payment.setCurrency(attributesNode.path("currency").asText("PHP"));

        // description
        payment.setDescription(attributesNode.path("description").asText(null));
        payment.setStatementDescriptor(attributesNode.path("statement_descriptor").asText(null));

        // customer info
        JsonNode billing = attributesNode.path("billing");
        payment.setCustomerName(billing.path("name").asText(null));
        payment.setCustomerEmail(billing.path("email").asText(null));
        payment.setCustomerPhone(billing.path("phone").asText(null));

        // source info
        JsonNode source = attributesNode.path("source");
        payment.setSourceId(source.path("id").asText(null));
        payment.setOrigin(attributesNode.path("origin").asText(null));
        payment.setSourceType(source.path("type").asText(null));

        // timestamps
        payment.setPaidAt(Instant.ofEpochSecond(attributesNode.path("paid_at").asLong()));
        payment.setCreatedAt(Instant.ofEpochSecond(attributesNode.path("created_at").asLong()));
        payment.setUpdatedAt(Instant.ofEpochSecond(attributesNode.path("updated_at").asLong()));

        // raw payload
        payment.setRawPayload(json);

        return payment;
    }







}
