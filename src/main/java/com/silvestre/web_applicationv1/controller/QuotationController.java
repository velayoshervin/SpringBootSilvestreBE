package com.silvestre.web_applicationv1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvestre.web_applicationv1.Dto.UserDto;
import com.silvestre.web_applicationv1.ExceptionHandler.QuotationAmountException;
import com.silvestre.web_applicationv1.entity.*;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import com.silvestre.web_applicationv1.enums.QuotationStatus;
import com.silvestre.web_applicationv1.repository.*;
import com.silvestre.web_applicationv1.requests.QuotationRequest;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.response.QuotationResponse;
import com.silvestre.web_applicationv1.service.CalendarAvailabilityService;
import com.silvestre.web_applicationv1.service.QuotationService;
import com.silvestre.web_applicationv1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("isAuthenticated()")
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
    private CalendarAvailabilityRepository calendarAvailabilityRepository;

    @PostMapping
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

    Quotation saved = quotationService.save(quotationData);

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

       Quotation quotation = quotationService.findByIdAndUSerId(userId,quotationId);

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
                quotation.getPackageId()
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
//    @Transactional
//    @PatchMapping("/{quotationId}/date/{date}")
//    public ResponseEntity<?> updateEventDate(
//            @PathVariable Long quotationId,
//            @PathVariable LocalDate date, HttpServletRequest request) { // eventDate is already a LocalDate from the path
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Authentication: " + auth);
//        System.out.println("Is Authenticated: " + auth.isAuthenticated());
//        System.out.println("Principal: " + auth.getPrincipal());
//        System.out.println("Authorities: " + auth.getAuthorities());
//
//        if (auth instanceof AnonymousAuthenticationToken) {
//            return ResponseEntity.status(403).body("Not authenticated");
//        }
//
//        try {
//            Quotation quotation = quotationService.findById(quotationId);
//            LocalDate oldDate = quotation.getRequestedEventDate();
//
//            // Business logic validations
//            if (date.isBefore(LocalDate.now())) {
//                return ResponseEntity.badRequest().body("Event date cannot be in the past");
//            }
//
//            // Check if new date is available
//            CalendarAvailability newDateAvailability = calendarAvailabilityRepository.findById(date).orElse(new CalendarAvailability());
//            if ("BOOKED".equals(newDateAvailability.getStatus())) {
//                return ResponseEntity.badRequest().body("Selected date is already booked");
//            }
//
//            // Update Calendar Availability for OLD date - mark as available
//            CalendarAvailability oldDateAvailability = calendarAvailabilityService.findByLocalDate(oldDate);
//            if (oldDateAvailability != null) {
//                oldDateAvailability.setStatus("AVAILABLE");
//                oldDateAvailability.setReason("Rescheduled to " + date);
//                calendarAvailabilityService.save(oldDateAvailability);
//            }
//
//            // Update/Create Calendar Availability for NEW date - mark as booked
//            if (newDateAvailability != null) {
//                newDateAvailability.setStatus("BOOKED");
//                newDateAvailability.setReason("Rescheduled from " + oldDate);
//            } else {
//                newDateAvailability = new CalendarAvailability();
//                newDateAvailability.setDate(date);
//                newDateAvailability.setStatus("BOOKED");
//                newDateAvailability.setReason("Rescheduled from " + oldDate);
//            }
//            calendarAvailabilityService.save(newDateAvailability);
//
//            // Update the quotation
//            quotation.setRequestedEventDate(date);
//            quotation.setModificationTime(LocalDateTime.now());
//
//            Quotation updated = quotationService.save(quotation);
//
//            return ResponseEntity.ok(mapToQuotationResponse(updated));
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error updating event date: " + e.getMessage());
//        }
//    }
@Transactional
@PatchMapping("/{quotationId}/date/{date}")
public ResponseEntity<?> updateEventDate(
        @PathVariable Long quotationId,
        @PathVariable LocalDate date, HttpServletRequest request) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    System.out.println("Authentication: " + auth);

    if (auth instanceof AnonymousAuthenticationToken) {
        return ResponseEntity.status(403).body("Not authenticated");
    }

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

        // Update/Create Calendar Availability for NEW date
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

        // Update the quotation
        quotation.setRequestedEventDate(date);
        quotation.setModificationTime(LocalDateTime.now());

        Quotation updated = quotationService.save(quotation);

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

            Quotation updated = quotationService.save(quotation);

            return ResponseEntity.ok(mapToQuotationResponse(updated));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating quotation: " + e.getMessage());
        }
    }

    @PatchMapping("/{quotationId}/reject")
    public ResponseEntity<?> rejectQuotation(@PathVariable Long quotationId) {
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

            Quotation updated = quotationService.save(quotation);

            return ResponseEntity.ok(mapToQuotationResponse(updated));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error rejecting quotation: " + e.getMessage());
        }
    }

}
