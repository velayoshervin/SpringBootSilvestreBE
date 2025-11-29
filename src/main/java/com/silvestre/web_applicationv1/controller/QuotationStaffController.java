package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.repository.QuotationRepository;
import com.silvestre.web_applicationv1.service.StaffRoleAssignmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/public/booking-staff-assignment")
public class QuotationStaffController {


    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private StaffRoleAssignmentService staffRoleAssignmentService;


    @GetMapping("/quotationId/{quotationId}")
    public ResponseEntity<?> getQuotationBasicInfo (@PathVariable Long quotationId){

        Optional<Quotation> quotation = quotationRepository.findById(quotationId);

        if(quotation.isEmpty())
            throw new ResourceNotFoundException("invalid id");

        return ResponseEntity.ok(mapToStaffQuotationInfo(quotation.get()));
    }

    private StaffQuotationInfo mapToStaffQuotationInfo(Quotation quotation) {
        return new StaffQuotationInfo(
                quotation.getId(),
                quotation.getRequestedEventDate(),
                quotation.getEventType(),
                quotation.getPax(),
                quotation.getCustomerName(),
                quotation.getVenue() != null ? quotation.getVenue().getName() : quotation.getClientVenue(),
                quotation.getContactNumber()
        );
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffQuotationInfo {
        private Long id;
        private LocalDate eventDate;
        private String eventType;
        private Integer pax;
        private String customerName;
        private String venue;
        private String contactNumber;
    }

    // DTO for StaffRoleAssignment
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffRoleAssignmentDto {
        private Long id;
        private Long quotationId;
        private UserStaffRoleController.UserStaffRoleDto userStaffRole;
        private LocalDate assignmentDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkAssignRequest {
        private Long quotationId;
        private List<Long> userStaffRoleIds; // List of UserStaffRole IDs to assign
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StaffAssignmentWithQuotationDto {
        private Long id;
        private Long quotationId;
        private UserStaffRoleController.UserStaffRoleDto userStaffRole;
        private LocalDate assignmentDate;
        private StaffQuotationInfo quotationInfo;
    }


    // Get all assignments for a specific quotation
    @GetMapping("/quotation/{quotationId}")
    public ResponseEntity<List<StaffRoleAssignmentDto>> getAssignmentsForQuotation(@PathVariable Long quotationId) {
        List<StaffRoleAssignmentDto> assignments = staffRoleAssignmentService.getAssignmentsForQuotation(quotationId);
        return ResponseEntity.ok(assignments);
    }

    // Bulk assign staff to quotation (for checkbox system)
    @PostMapping("/bulk")
    public ResponseEntity<List<StaffRoleAssignmentDto>> bulkAssignStaffToQuotation(@Valid @RequestBody BulkAssignRequest request) {
        List<StaffRoleAssignmentDto> assignments = staffRoleAssignmentService.bulkAssignStaffToQuotation(request);
        return ResponseEntity.ok(assignments);
    }

    // Remove assignment
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<?> removeAssignment(@PathVariable Long assignmentId) {
        staffRoleAssignmentService.removeAssignment(assignmentId);
        return ResponseEntity.ok().build();
    }

    // Clear all assignments for a quotation
    @DeleteMapping("/quotation/{quotationId}")
    public ResponseEntity<?> clearAllAssignmentsForQuotation(@PathVariable Long quotationId) {
        staffRoleAssignmentService.clearAllAssignmentsForQuotation(quotationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StaffAssignmentWithQuotationDto>> getAssignmentsByUser(@PathVariable Long userId) {
        List<StaffAssignmentWithQuotationDto> assignments = staffRoleAssignmentService.getAssignmentsByUserWithQuotationInfo(userId);
        return ResponseEntity.ok(assignments);
    }





}
