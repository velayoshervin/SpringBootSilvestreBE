package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.config.AdminConfig;
import com.silvestre.web_applicationv1.entity.*;
import com.silvestre.web_applicationv1.enums.QuotationStatus;
import com.silvestre.web_applicationv1.repository.QuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class  QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private AdminConfig adminConfig;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CalendarAvailabilityService calendarAvailabilityService;

    public Quotation save(Quotation quotation){
        return quotationRepository.save(quotation);
    }

    public List<Quotation> loadAllQuotations(Long userId){
        return quotationRepository.findAllByUserId(userId);
    }

    public Quotation findByIdAndUSerId(Long quotationId, Long userId){
        return quotationRepository.findByIdAndUserId(quotationId, userId).
                orElseThrow(()-> new ResourceNotFoundException("quotation not found"));
    }

    public Page<Quotation> findAll(Pageable pageable) {
        return quotationRepository.findAll(pageable);
    }

    public Page<Quotation> findByUserId(Long userId, Pageable pageable) {

        User existing = userService.findUserById(userId);
        return quotationRepository.findByUserId(userId, pageable);


    }

    public Quotation findById(Long quotationId){
        return quotationRepository.findById(quotationId).orElseThrow(()-> new ResourceNotFoundException("quotation not found"));
    }



    @Transactional
    public void setExpired(Long quotationId){

        Quotation existing = quotationRepository.findById(quotationId).orElseThrow(()-> new ResourceNotFoundException(
                "quotation Id does not exist"
        ));

        User customer= existing.getUser();


        System.out.println("Now: " + LocalDateTime.now());
        System.out.println("ApprovalTime: " + existing.getApprovalTime());
        System.out.println("Is after? " + LocalDateTime.now().isAfter(existing.getApprovalTime()));


        if(LocalDateTime.now().isAfter(existing.getApprovalTime())){
            existing.setStatus(QuotationStatus.EXPIRED);
        }

        System.out.println("Set expired called");

        Notification notificationForClient = new Notification();
        notificationForClient.setType("PAYMENT TIME EXPIRED");
        notificationForClient.setTitle("Payment due has expired");
        notificationForClient.setMessage("Your time to make payment has expired.");
        notificationForClient.setUser(customer);

        LocalDate reservedDate = existing.getRequestedEventDate();

        User admin = userService.findUserById(adminConfig.getAdminId());

        Notification cus= notificationService.save(notificationForClient);

        BookingHistory bookingHistory= new BookingHistory();
        bookingHistory.setQuotation(existing);
        bookingHistory.setDescription("Time for payment expired");
        bookingHistory.setAction("Expire Booking");

        existing.getHistory().add(bookingHistory);

        quotationRepository.save(existing);

        //sendToClient
        messagingTemplate.convertAndSendToUser(
                existing.getUser().getEmail(), // assuming userId is used as destination
                "/queue/notifications",
                cus
        );

    }


    public List<LocalDate> getQuotationDatesByUserId(Long userId) {

        return quotationRepository.findAllEventDatesByUserId(userId);
    }

    public Quotation completeQuotation(Long quotationId) {


            Quotation quotation = quotationRepository.findById(quotationId)
                    .orElseThrow(() -> new RuntimeException("Quotation not found"));

            // Update status to COMPLETED
            quotation.setStatus(QuotationStatus.COMPLETED);


            return quotationRepository.save(quotation);

    }
}
