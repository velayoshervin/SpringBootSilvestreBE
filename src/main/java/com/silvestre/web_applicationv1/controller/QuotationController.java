package com.silvestre.web_applicationv1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvestre.web_applicationv1.Dto.UserDto;
import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.config.AdminConfig;
import com.silvestre.web_applicationv1.entity.*;
import com.silvestre.web_applicationv1.enums.BookingStatus;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import com.silvestre.web_applicationv1.enums.QuotationStatus;
import com.silvestre.web_applicationv1.repository.*;
import com.silvestre.web_applicationv1.requests.QuotationRequest;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.response.QuotationResponse;
import com.silvestre.web_applicationv1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;


@RestController
//@PreAuthorize("isAuthenticated()")
@RequestMapping("/quotations")
public class QuotationController {

    @Autowired
    private QuotationService quotationService;
    @Autowired
    private UserService userService;
    @Autowired
    private LineItemRepository lineItemRepository;
    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CalendarAvailabilityService calendarAvailabilityService;

    @Autowired
    private CalendarEventRepository calendarEventRepository;


    @Autowired
    private CalendarAvailabilityRepository calendarAvailabilityRepository;

    @Autowired
    private BookingHistoryRepository bookingHistoryRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AdminConfig adminConfig;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private QuotationRedisService quotationRedisService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @PatchMapping("/{quotationId}/complete")
    public ResponseEntity<?> completeQuotation(@PathVariable Long quotationId) {
        try {
            // Your service method to complete the quotation
            Quotation completedQuotation = quotationService.completeQuotation(quotationId);

            return ResponseEntity.ok(completedQuotation);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error completing quotation: " + e.getMessage());
        }
    }







    @Transactional
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createQuotation(@RequestBody QuotationRequest payload)
    {
        System.out.println(payload);

        User user = userService.findUserById(payload.getUserId());
        List<QuotationLineItem> items = Optional.ofNullable(payload.getLineItems())
                .orElse(Collections.emptyList());


        Venue venue = null;

        if (payload.getVenueId() != null) {
            venue = venueRepository.findById(payload.getVenueId())
                    .orElseThrow(() -> new RuntimeException("Venue not found"));
        }

        if (payload.getEventDate() == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

//        LocalDate date = payload.getEventDate();
//
//        CalendarAvailability calendarAvailability = calendarAvailabilityService.reserveDate(date);


        String customFoodJson = null;
        if (payload.getCustomFoodByCategory() != null) {
            try {
                customFoodJson = new ObjectMapper().writeValueAsString(payload.getCustomFoodByCategory());
            } catch (Exception e) {
                // Handle JSON conversion error
                customFoodJson = "{}";
            }
        }

        Quotation quotationData = Quotation.builder()
                .user(user)
                .requestedEventDate(payload.getEventDate())
                .pax(payload.getPax())
                .eventType(payload.getEventType())
                .venue(venue)
                .celebrants(payload.getCelebrants())
                .customerName(payload.getCustomerName())
                .contactNumber(payload.getContactNumber())
                .address(payload.getAddress())
                .customFoodSelection(customFoodJson).packageId(payload.getPackageId())
                .status(QuotationStatus.SUBMITTED)
                .menuBundleId(payload.getMenuBundleId())
                .build();


        for (QuotationLineItem lineItem : items) {
            lineItem.setQuotation(quotationData);

            if (lineItem.getItemId() != null) {
                Item itemEntity = itemRepo.findById(lineItem.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found: " + lineItem.getItemId()));
                lineItem.setItem(itemEntity);
            }
        }


        quotationData.setLineItems(items);

    BigDecimal computedAmount = quotationData.getTotalAmount();

    quotationData.setTotal(computedAmount);


        String description= "Booking created ";
        BookingHistory bookingHistory= new BookingHistory();
        bookingHistory.setQuotation(quotationData);
        bookingHistory.setDescription(description);
        bookingHistory.setAction("BOOKING CREATED");

        quotationData.getHistory().add(bookingHistory);

    Notification notification = new Notification();
    notification.setUser(user);
    notification.setTitle("Booking Created");
    notification.setMessage("Thank you for submitting a booking request. Well notify you as soon as possible");
    notification.setType("BOOKING CREATED");


    User admin = userService.findUserById(adminConfig.getAdminId());

    String customer = payload.getCustomerName();

    Notification adminNotification = new Notification();
    adminNotification.setType("BOOKING CREATED");
    adminNotification.setUser(admin);
    adminNotification.setTitle("Booking Submitted");
    adminNotification.setMessage("A booking has been submitted");
    adminNotification.setSender(customer);

    notificationService.save(notification);
    notificationService.save(adminNotification);


        messagingTemplate.convertAndSendToUser(
                user.getEmail(), // assuming userId is used as destination
                "/queue/notifications",
                notification
        );

        messagingTemplate.convertAndSendToUser(
               admin.getEmail(), // assuming userId is used as destination
                "/queue/notifications",
                adminNotification
        );
        Quotation saved = quotationService.save(quotationData);
//        calendarAvailabilityService.save(calendarAvailability);
        QuotationResponse response = mapToQuotationResponse(saved);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update/{quotationId}")
    public ResponseEntity<?> updateQuotation(
            @PathVariable Long quotationId,
            @RequestBody QuotationRequest payload) {
        Quotation existingQuotation = quotationService.findById(quotationId);

        // 2. Validate user
        User user = userService.findUserById(payload.getUserId());

        // 3. Handle venue
        Venue venue = null;
        if (payload.getVenueId() != null) {
            venue = venueRepository.findById(payload.getVenueId())
                    .orElseThrow(() -> new RuntimeException("Venue not found"));
        }

        // 4. Handle custom food JSON
        String customFoodJson = null;
        if (payload.getCustomFoodByCategory() != null) {
            try {
                customFoodJson = new ObjectMapper().writeValueAsString(payload.getCustomFoodByCategory());
            } catch (Exception e) {
                customFoodJson = "{}";
            }
        }

        // 5. Update the existing quotation entity
        existingQuotation.setUser(user);
        existingQuotation.setRequestedEventDate(payload.getEventDate());
        existingQuotation.setPax(payload.getPax());
        existingQuotation.setEventType(payload.getEventType());
        existingQuotation.setVenue(venue);
        existingQuotation.setCelebrants(payload.getCelebrants());
        existingQuotation.setCustomerName(payload.getCustomerName());
        existingQuotation.setContactNumber(payload.getContactNumber());
        existingQuotation.setAddress(payload.getAddress());
        existingQuotation.setCustomFoodSelection(customFoodJson);
        existingQuotation.setPackageId(payload.getPackageId());
        existingQuotation.setModificationTime(LocalDateTime.now());

        // 6. Update line items - SIMPLEST APPROACH
        // Clear existing and add all new ones (let orphanRemoval handle deletions)
        existingQuotation.getLineItems().clear();

        // Add all new line items from request
        for (QuotationLineItem newLineItem : payload.getLineItems()) {
            QuotationLineItem lineItem = new QuotationLineItem();
            lineItem.setQuotation(existingQuotation);
            lineItem.setQuantity(newLineItem.getQuantity());
            lineItem.setDescription(newLineItem.getDescription());
            lineItem.setPriceAtQuotation(newLineItem.getPriceAtQuotation());

            if (newLineItem.getItemId() != null) {
                Item itemEntity = itemRepo.findById(newLineItem.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found: " + newLineItem.getItemId()));
                lineItem.setItem(itemEntity);
            }

            existingQuotation.getLineItems().add(lineItem);
        }

        // 7. Recalculate total
        BigDecimal computedAmount = existingQuotation.getTotalAmount();
        existingQuotation.setTotal(computedAmount);

        // 8. Save the updated quotation
        Quotation updated = quotationService.save(existingQuotation);

        // 9. Return response
        QuotationResponse response = mapToQuotationResponse(updated);
        return ResponseEntity.ok(response);
        }

    @PutMapping("/{quotationId}")
    public ResponseEntity<?> handleQuotationEdit(
            @RequestBody QuotationRequest payload,
            @PathVariable Long quotationId) {

        // Fetch existing quotation
        Quotation existing = quotationService.findByIdAndUSerId(quotationId, payload.getUserId());

        // Get line items from payload
        List<QuotationLineItem> newItems = Optional.ofNullable(payload.getLineItems())
                .orElse(Collections.emptyList());

        // Clear the existing list in-place (important for Hibernate orphanRemoval)
        if (existing.getLineItems() == null) {
            existing.setLineItems(new ArrayList<>()); // ensure list is initialized
        } else {
            existing.getLineItems().clear();
        }

        // Add new items
        for (QuotationLineItem lineItem : newItems) {
            lineItem.setQuotation(existing);
            if (lineItem.getItemId() != null) {
                Item itemEntity = itemRepo.findById(lineItem.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found: " + lineItem.getItemId()));
                lineItem.setItem(itemEntity);
                lineItem.setItemId(itemEntity.getItemId());
            }
            existing.getLineItems().add(lineItem);
        }

        // Update event date and total
        existing.setRequestedEventDate(payload.getEventDate());
        existing.setTotal(existing.getTotalAmount());

        // Save
        Quotation saved = quotationService.save(existing);

        QuotationResponse response = mapToQuotationResponse(saved);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}/{quotationId}")
    public ResponseEntity<?> findByQuotationId(@PathVariable Long userId, @PathVariable Long quotationId){

       Quotation quotation = quotationService.findByIdAndUSerId(quotationId,userId);

        return ResponseEntity.ok(mapToQuotationResponse(quotation));
    }

    private QuotationResponse mapToQuotationResponse(Quotation quotation) {
        UserDto userDto = new UserDto(quotation.getUser());

        // Convert customFoodSelection JSON back to Map
        Map<String, List<QuotationResponse.CustomFoodItem>> customFoodMap = null;   
        if (quotation.getCustomFoodSelection() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                customFoodMap = mapper.readValue(
                        quotation.getCustomFoodSelection(),
                        new TypeReference<Map<String, List<QuotationResponse.CustomFoodItem>>>() {}
                );
            } catch (Exception e) {
                // Log the error and set empty map
                System.err.println("Error parsing custom food selection: " + e.getMessage());
                customFoodMap = Map.of();
            }
        }

        return new QuotationResponse(
                quotation.getId(),
                quotation.getLineItems(),
                quotation.getStatus(),
                quotation.getCreationTime(),
                quotation.getModificationTime(),
                quotation.getTotal(),
                quotation.getRequestedEventDate(),
                quotation.getEventType(),
                quotation.getPax(),
                quotation.getVenue(),
                userDto,
                quotation.getCelebrants(),
                quotation.getCustomerName(),
                quotation.getContactNumber(),
                quotation.getAddress(),
                customFoodMap,
                quotation.getPackageId(),
                quotation.getRescheduleTo(),
                quotation.getHistory(),
                quotation.getClientVenue(),
                quotation.getMenuBundleId(),
                quotation.getApprovalTime()
        );
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll(@RequestParam (defaultValue = "0") int  pageNumber
            ,@RequestParam (defaultValue = "20") int size){

        Pageable pageable = PageRequest.of(pageNumber,size,Sort.by(Sort.Direction.DESC, "modificationTime"));

        Page<Quotation> quotationPage = quotationService.findAll(pageable);
        Page<QuotationResponse> responsePage = quotationPage.map(this::mapToQuotationResponse);
        return ResponseEntity.ok(new PaginatedResponse<>(responsePage));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> findByUser(@RequestParam (defaultValue = "0" ) int pageNumber,
                                       @RequestParam (defaultValue = "20") int size ,
                                       @PathVariable Long userId){

        Pageable pageable=  PageRequest.of(pageNumber,size, Sort.by(Sort.Direction.DESC, "modificationTime") );
        Page<Quotation> quotationPage = quotationService.findByUserId(userId, pageable);

        Page<QuotationResponse> responsePage = quotationPage.map(this::mapToQuotationResponse);
        return ResponseEntity.ok(new PaginatedResponse<>(responsePage));
    }

    @GetMapping("/getGrossDue/{quotationId}")
    public ResponseEntity<?> getGrossDue(@PathVariable Long quotationId){

        //For simply getting total

        Quotation quotation = quotationService.findById(quotationId);

        return ResponseEntity.ok(quotation.getTotalAmount());
    }

    @GetMapping("/totalPayments")
    public ResponseEntity<?> getTotalPayments (@RequestParam Long quotationId){
        BigDecimal totalPaymentsInCentavo= paymentRepository.sumOfAllPaymentsForQuotation(quotationId, PaymentStatus.PAID);

        if (totalPaymentsInCentavo == null) {
            totalPaymentsInCentavo = BigDecimal.ZERO;
        }

        long totalPayments = totalPaymentsInCentavo.longValue() / 100;
        return ResponseEntity.ok(totalPayments);
    }

    @PatchMapping("/{quotationId}/cancel")
    public ResponseEntity<?> cancelQuotation(@PathVariable Long quotationId) {
        Quotation quotation = quotationService.findById(quotationId);



        quotation.setStatus(QuotationStatus.CANCELLED);
        quotation.setModificationTime(LocalDateTime.now());

        Quotation updated = quotationService.save(quotation);

        return ResponseEntity.ok(mapToQuotationResponse(updated));
    }

        //The customer hit this endpoint to reschedule
        @PreAuthorize("isAuthenticated()")
        @Transactional
        @PatchMapping("/{quotationId}/rescheduleTo")
        public ResponseEntity<?> requestReschedule(@PathVariable Long quotationId, @RequestParam LocalDate date,
        @RequestBody String reason
        ){

            if (date.isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body("Event date cannot be in the past");
            }

            Optional<CalendarAvailability> newDateAvailabilityOpt = calendarAvailabilityRepository.findById(date);

            if (newDateAvailabilityOpt.isPresent()) {
                CalendarAvailability existing = newDateAvailabilityOpt.get();
                // Use the same status logic as your query
                if (!"AVAILABLE".equals(existing.getStatus()) && !"RESCHEDULED".equals(existing.getStatus())) {
                    return ResponseEntity.badRequest().body("Selected date is not available");
                }
            }

            Quotation quotation= quotationService.findById(quotationId);
            BookingHistory bookingHistory= new BookingHistory();
            bookingHistory.setQuotation(quotation);



            User sender = quotation.getUser();

            String senderFullName = sender.getFirstname() + " "+ sender.getLastname();

            String description=  senderFullName +" requests to reschedule from " + quotation.getRequestedEventDate() +" to "+
                    date +" due to: " + reason;

            bookingHistory.setAction("RESCHEDULE REQUEST");
            bookingHistory.setDescription(description);
            quotation.getHistory().add(bookingHistory);

            quotation.setStatus(QuotationStatus.RESCHEDULE_REQUESTED);
            quotation.setRescheduleTo(date);

            Booking booking = bookingService.findByQuotation(quotation).orElseThrow(()-> new ResourceNotFoundException("quotation not found"));

            booking.setBookingStatus(BookingStatus.RESCHEDULE_REQUESTED
            );

            Quotation updated = quotationService.save(quotation);
            bookingService.save(booking);

            System.out.println("adminConfig admin id = " + adminConfig.getAdminId());

            String notificationType ="Reschedule request";
            String title="Reschedule request";

            notificationService.createAndSendNotificationToAdmin(description, adminConfig.getAdminId(),title,notificationType);


        return ResponseEntity.ok(updated);
        }



@PreAuthorize("hasRole('ADMIN')")
@Transactional
@PatchMapping("/{quotationId}/date")
public ResponseEntity<?> updateEventDate(
        @PathVariable Long quotationId,
        @RequestParam LocalDate date,
        @RequestBody String description
        ) {

    try {
        Quotation quotation = quotationService.findById(quotationId);
        LocalDate oldDate = quotation.getRequestedEventDate();

        // Business logic validations
        if (date.isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body("Event date cannot be in the past");
        }

        // Check if new date is available using your existing logic
        Optional<CalendarAvailability> newDateAvailabilityOpt = calendarAvailabilityRepository.findById(date);

        if (newDateAvailabilityOpt.isPresent()) {
            CalendarAvailability existing = newDateAvailabilityOpt.get();
            // Use the same status logic as your query
            if (!"AVAILABLE".equals(existing.getStatus()) && !"RESCHEDULED".equals(existing.getStatus())) {
                return ResponseEntity.badRequest().body("Selected date is not available");
            }
        }

        // Update Calendar Availability for OLD date - mark as available
        Optional<CalendarAvailability> oldDateAvailabilityOpt = calendarAvailabilityRepository.findById(oldDate);
        if (oldDateAvailabilityOpt.isPresent()) {
            CalendarAvailability oldDateAvailability = oldDateAvailabilityOpt.get();
            oldDateAvailability.setStatus("AVAILABLE");
            oldDateAvailability.setReason("Rescheduled to " + date);
            calendarAvailabilityRepository.save(oldDateAvailability);
        } else {
            // Create old date availability if it doesn't exist
            CalendarAvailability oldDateAvailability = new CalendarAvailability();
            oldDateAvailability.setDate(oldDate);
            oldDateAvailability.setStatus("AVAILABLE");
            oldDateAvailability.setReason("Rescheduled to " + date);
            calendarAvailabilityRepository.save(oldDateAvailability);
        }

        CalendarAvailability newDateAvailability;
        if (newDateAvailabilityOpt.isPresent()) {
            newDateAvailability = newDateAvailabilityOpt.get();
        } else {
            newDateAvailability = new CalendarAvailability();
            newDateAvailability.setDate(date); // ⭐️ CRITICAL: Set the date before saving
        }

        // Use "BOOKED" status since this is for your query to exclude
        newDateAvailability.setStatus("BOOKED");
        newDateAvailability.setReason("Rescheduled from " + oldDate);

        // Debug before save
        System.out.println("💾 Saving CalendarAvailability:");
        System.out.println("💾 Date: " + newDateAvailability.getDate());
        System.out.println("💾 Status: " + newDateAvailability.getStatus());
        System.out.println("💾 Reason: " + newDateAvailability.getReason());

        calendarAvailabilityRepository.save(newDateAvailability);

        CalendarEvent existing =calendarEventRepository.findByQuotation(quotation).orElseThrow(()->
                new ResourceNotFoundException("no event found")
        );

        OffsetDateTime start = existing.getStartTime();
        OffsetDateTime end = existing.getEndTime();

        OffsetDateTime newStart = start
                .withYear(date.getYear())
                .withMonth(date.getMonthValue())
                .withDayOfMonth(date.getDayOfMonth());

        OffsetDateTime newEnd = end
                .withYear(date.getYear())
                .withMonth(date.getMonthValue())
                .withDayOfMonth(date.getDayOfMonth());

        existing.setStartTime(newStart);
        existing.setEndTime(newEnd);

        calendarEventRepository.save(existing);

        quotation.setRequestedEventDate(date);
        quotation.setModificationTime(LocalDateTime.now());

        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setQuotation(quotation);
        bookingHistory.setAction("Rescheduled");
        bookingHistory.setDescription("Booking has been rescheduled from " +quotation.getRequestedEventDate() +" to " +
                date + "with NOTE: " + description);
        quotation.getHistory().add(bookingHistory);
        quotation.setStatus(QuotationStatus.BOOKED);

        Booking booking = bookingService.findByQuotation(quotation).orElseThrow(
                ()-> new ResourceNotFoundException("no booking found")
        );


        Quotation updated = quotationService.save(quotation);

        booking.setBookingStatus(BookingStatus.BOOKED);

        bookingService.save(booking);


        Notification notification =  new Notification();
        notification.setTitle("Rescheduled");
        notification.setUser(quotation.getUser());
        notification.setMessage("Booking has been rescheduled from " +quotation.getRequestedEventDate() +" to " +
                date + "with NOTE: " + description);
        notification.setType("Reschedule");

        notificationService.save(notification);

        System.out.println("Rescheduling notification"+ quotation.getUser().getEmail());

        messagingTemplate.convertAndSendToUser(
                quotation.getUser().getEmail(), // assuming userId is used as destination
                "/queue/notifications",
                notification
        );



        return ResponseEntity.ok(mapToQuotationResponse(updated));

    } catch (RuntimeException e) {
        System.out.println("❌ RuntimeException: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        System.out.println(" Exception: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating event date: " + e.getMessage());
    }
}

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @PatchMapping("/{quotationId}/pending-payment")
    public ResponseEntity<?> setPendingPayment(@PathVariable Long quotationId) {
        try {
            Quotation quotation = quotationService.findById(quotationId);

            // Validate: Only allow if current status is SUBMITTED
            if (quotation.getStatus() != QuotationStatus.SUBMITTED) {
                return ResponseEntity.badRequest().body(
                        "Cannot set to pending payment. Quotation must be in SUBMITTED status. Current status: " + quotation.getStatus()
                );
            }

            quotation.setStatus(QuotationStatus.PENDING_PAYMENT);
            quotation.setModificationTime(LocalDateTime.now());
            quotation.setApprovalTime(LocalDateTime.now().plusSeconds(60*30));

            Quotation updated = quotationService.save(quotation);

            quotationRedisService.trackQuotationExpiration(quotationId,60*31);

            Notification notification= new Notification();
            notification.setType("PENDING PAYMENT");
            notification.setUser(quotation.getUser());
            notification.setTitle("Payment Pending");

            notification.setLink("/payment-options/"+quotationId);
            notification.setMessage("Your quotation #" + quotationId + " is pending payment. Please pay within 30 minutes.");

            notificationService.save(notification);

            System.out.println("Notification for approving submitted:"+ quotation.getUser().getEmail());

            messagingTemplate.convertAndSendToUser(
                    quotation.getUser().getEmail(), // assuming userId is used as destination
                    "/queue/notifications",
                    notification
            );


            return ResponseEntity.ok(mapToQuotationResponse(updated));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating quotation: " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @PatchMapping("/{quotationId}/reject")
    public ResponseEntity<?> rejectQuotation(@PathVariable Long quotationId, @RequestParam String description) {
        try {
            Quotation quotation = quotationService.findById(quotationId);

            // Validate: Only allow if current status is SUBMITTED
            if (quotation.getStatus() != QuotationStatus.SUBMITTED) {
                return ResponseEntity.badRequest().body(
                        "Cannot reject quotation. Quotation must be in SUBMITTED status. Current status: " + quotation.getStatus()
                );
            }
            quotation.setStatus(QuotationStatus.REJECTED);
            quotation.setModificationTime(LocalDateTime.now());

            BookingHistory bookingHistory= new BookingHistory();
            bookingHistory.setQuotation(quotation);
            bookingHistory.setDescription(description);
            bookingHistory.setAction("REJECTED");

            quotation.getHistory().add(bookingHistory);

            Quotation updated = quotationService.save(quotation);



            Notification notification = new Notification();
            notification.setType("REJECTED BOOKING");
            notification.setMessage("Booking request was not approved with notes: " + description);
            notification.setTitle("REJECTED BOOKING");
            notification.setUser(quotation.getUser());

            notificationService.save(notification);

            System.out.println("Notification for rejecting submitted:"+ quotation.getUser().getEmail());


            messagingTemplate.convertAndSendToUser(
                    quotation.getUser().getEmail(), // assuming userId is used as destination
                    "/queue/notifications",
                    notification
            );



            return ResponseEntity.ok(mapToQuotationResponse(updated));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error rejecting quotation: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @PatchMapping("/{quotationId}/reschedule/reject")
    public ResponseEntity<?> rejectReschedule(
            @PathVariable Long quotationId,
            @RequestParam LocalDate selectedDate,
            @RequestBody String notes
    ) {
        Quotation quotation = quotationService.findById(quotationId);

        BookingHistory history = new BookingHistory();
        history.setQuotation(quotation);
        history.setAction("Reschedule Rejected");
        history.setDescription("Attempted date: " + selectedDate + ". notes: " + notes);
        quotation.getHistory().add(history);

        quotation.setStatus(QuotationStatus.BOOKED);

        Booking booking = bookingService.findByQuotation(quotation).orElseThrow(
                ()-> new ResourceNotFoundException("no booking found")
        );

        booking.setBookingStatus(BookingStatus.BOOKED);

        bookingService.save(booking);

        quotationService.save(quotation);


        Notification notification = new Notification();
        notification.setType("REJECT RESCHEDULE");
        notification.setMessage("Reschedule request was not approved with notes: " + notes);
        notification.setTitle("REJECTED RESCHEDULE");
        notification.setUser(quotation.getUser());

        notificationService.save(notification);

        System.out.println("Notification for rejecting submitted reschedule date:"+ quotation.getUser().getEmail());

        messagingTemplate.convertAndSendToUser(
                quotation.getUser().getEmail(), // assuming userId is used as destination
                "/queue/notifications",
                notification
        );


        return ResponseEntity.ok("Reschedule rejected successfully");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-quotation-dates")
    public List<LocalDate> getMyQuotationDates() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername());

        return quotationService.getQuotationDatesByUserId(currentUser.getId());
    }

}

