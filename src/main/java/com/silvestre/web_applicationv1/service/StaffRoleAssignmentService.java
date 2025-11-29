package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.Dto.UserShowingRoleDto;
import com.silvestre.web_applicationv1.controller.QuotationStaffController;
import com.silvestre.web_applicationv1.controller.UserStaffRoleController;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.entity.StaffRoleAssignment;
import com.silvestre.web_applicationv1.entity.UserStaffRole;
import com.silvestre.web_applicationv1.repository.QuotationRepository;
import com.silvestre.web_applicationv1.repository.StaffRoleAssignmentRepo;
import com.silvestre.web_applicationv1.repository.UserStaffRoleRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StaffRoleAssignmentService {

    @Autowired
    private StaffRoleAssignmentRepo staffRoleAssignmentRepository;

    @Autowired
    private UserStaffRoleRepo userStaffRoleRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    public List<QuotationStaffController.StaffRoleAssignmentDto> getAssignmentsForQuotation(Long quotationId) {
        return staffRoleAssignmentRepository.findByQuotationId(quotationId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public List<QuotationStaffController.StaffRoleAssignmentDto> bulkAssignStaffToQuotation(QuotationStaffController.BulkAssignRequest request) {
        try {
            System.out.println("Bulk assignment request: quotationId=" + request.getQuotationId() + ", userStaffRoleIds=" + request.getUserStaffRoleIds());

            // First, remove existing assignments for this quotation
            List<StaffRoleAssignment> existingAssignments = staffRoleAssignmentRepository.findByQuotationId(request.getQuotationId());
            System.out.println("Found " + existingAssignments.size() + " existing assignments to delete");

            if (!existingAssignments.isEmpty()) {
                staffRoleAssignmentRepository.deleteAll(existingAssignments);
                staffRoleAssignmentRepository.flush(); // Force immediate delete
                System.out.println("Successfully deleted existing assignments");
            }

            // If no staff selected, return empty list
            if (request.getUserStaffRoleIds() == null || request.getUserStaffRoleIds().isEmpty()) {
                System.out.println("No staff selected - returning empty list");
                return new ArrayList<>();
            }

            // Create new assignments
            List<StaffRoleAssignment> newAssignments = new ArrayList<>();
            for (Long userStaffRoleId : request.getUserStaffRoleIds()) {
                UserStaffRole userStaffRole = userStaffRoleRepository.findById(userStaffRoleId)
                        .orElseThrow(() -> new RuntimeException("UserStaffRole not found: " + userStaffRoleId));

                StaffRoleAssignment assignment = new StaffRoleAssignment();
                assignment.setQuotationId(request.getQuotationId());
                assignment.setUserStaffRole(userStaffRole);
                assignment.setLocalDate(LocalDate.now());
                newAssignments.add(assignment);
            }

            List<StaffRoleAssignment> saved = staffRoleAssignmentRepository.saveAll(newAssignments);
            System.out.println("Successfully saved " + saved.size() + " new assignments");

            return saved.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error in bulkAssignStaffToQuotation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to assign staff: " + e.getMessage(), e);
        }
    }

    public void removeAssignment(Long assignmentId) {
        if (!staffRoleAssignmentRepository.existsById(assignmentId)) {
            throw new RuntimeException("Assignment not found: " + assignmentId);
        }
        staffRoleAssignmentRepository.deleteById(assignmentId);
    }

    public void clearAllAssignmentsForQuotation(Long quotationId) {
        staffRoleAssignmentRepository.deleteByQuotationId(quotationId);
    }

    private QuotationStaffController.StaffRoleAssignmentDto convertToDto(StaffRoleAssignment assignment) {
        UserStaffRoleController.UserStaffRoleDto userStaffRoleDto = new UserStaffRoleController.UserStaffRoleDto(
                assignment.getUserStaffRole().getId(),
                new UserShowingRoleDto(assignment.getUserStaffRole().getUser()),
                new UserStaffRoleController.StaffRoleDto(
                        assignment.getUserStaffRole().getStaffRole().getStaffRoleId(),
                        assignment.getUserStaffRole().getStaffRole().getRoleName(),
                        assignment.getUserStaffRole().getStaffRole().getRoleDescription()
                )
        );

        return new QuotationStaffController.StaffRoleAssignmentDto(
                assignment.getId(),
                assignment.getQuotationId(),
                userStaffRoleDto,
                assignment.getLocalDate()
        );
    }


    private QuotationStaffController.StaffAssignmentWithQuotationDto convertToDtoWithQuotation(StaffRoleAssignment assignment, Quotation quotation) {
        UserStaffRoleController.UserStaffRoleDto userStaffRoleDto = new UserStaffRoleController.UserStaffRoleDto(
                assignment.getUserStaffRole().getId(),
                new UserShowingRoleDto(assignment.getUserStaffRole().getUser()),
                new UserStaffRoleController.StaffRoleDto(
                        assignment.getUserStaffRole().getStaffRole().getStaffRoleId(),
                        assignment.getUserStaffRole().getStaffRole().getRoleName(),
                        assignment.getUserStaffRole().getStaffRole().getRoleDescription()
                )
        );

        // Convert quotation to StaffQuotationInfo - match the existing DTO constructor
        QuotationStaffController.StaffQuotationInfo quotationInfo = null;
        if (quotation != null) {
            quotationInfo = new QuotationStaffController.StaffQuotationInfo(
                    quotation.getId(),
                    quotation.getRequestedEventDate(),
                    quotation.getEventType(),
                    quotation.getPax(),
                    quotation.getCustomerName(),
                    quotation.getVenue() != null ? quotation.getVenue().getName() : quotation.getClientVenue(),
                    quotation.getUser().getPhone()
                    // Only 6 parameters to match your @AllArgsConstructor
            );
        }

        return new QuotationStaffController.StaffAssignmentWithQuotationDto(
                assignment.getId(),
                assignment.getQuotationId(),
                userStaffRoleDto,
                assignment.getLocalDate(),
                quotationInfo
        );
    }





//    @Transactional
//    public List<QuotationStaffController.StaffRoleAssignmentDto> getAssignmentsByUser(Long userId) {
//        try {
//            System.out.println("🔍 Getting assignments for user ID: " + userId);
//
//            // Get all UserStaffRole entries for this user
//            List<UserStaffRole> userStaffRoles = userStaffRoleRepository.findByUserId(userId);
//            System.out.println("📋 Found " + userStaffRoles.size() + " user staff roles for user " + userId);
//
//            if (userStaffRoles.isEmpty()) {
//                System.out.println("ℹ️ No user staff roles found for user " + userId);
//                return new ArrayList<>();
//            }
//
//            // Extract the UserStaffRole IDs
//            List<Long> userStaffRoleIds = userStaffRoles.stream()
//                    .map(UserStaffRole::getId)
//                    .collect(Collectors.toList());
//            System.out.println("🎯 UserStaffRole IDs: " + userStaffRoleIds);
//
//            // Get assignments for these UserStaffRole IDs
//            List<StaffRoleAssignment> assignments = staffRoleAssignmentRepository.findByUserStaffRoleIdIn(userStaffRoleIds);
//            System.out.println("📅 Found " + assignments.size() + " assignments for user " + userId);
//
//            // Convert to DTOs
//            List<QuotationStaffController.StaffRoleAssignmentDto> result = assignments.stream()
//                    .map(this::convertToDto)
//                    .collect(Collectors.toList());
//
//            System.out.println("✅ Successfully converted " + result.size() + " assignments to DTOs");
//            return result;
//
//        } catch (Exception e) {
//            System.err.println("❌ Error in getAssignmentsByUser: " + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("Failed to get user assignments: " + e.getMessage(), e);
//        }
//    }

    @Transactional
    public List<QuotationStaffController.StaffAssignmentWithQuotationDto> getAssignmentsByUserWithQuotationInfo(Long userId) {
        try {
            System.out.println("🔍 Getting assignments for user ID: " + userId);

            // Get all UserStaffRole entries for this user
            List<UserStaffRole> userStaffRoles = userStaffRoleRepository.findByUserId(userId);
            System.out.println("📋 Found " + userStaffRoles.size() + " user staff roles for user " + userId);

            if (userStaffRoles.isEmpty()) {
                System.out.println("ℹ️ No user staff roles found for user " + userId);
                return new ArrayList<>();
            }

            // Extract the UserStaffRole IDs
            List<Long> userStaffRoleIds = userStaffRoles.stream()
                    .map(UserStaffRole::getId)
                    .collect(Collectors.toList());
            System.out.println("🎯 UserStaffRole IDs: " + userStaffRoleIds);

            // Get assignments for these UserStaffRole IDs
            List<StaffRoleAssignment> assignments = staffRoleAssignmentRepository.findByUserStaffRoleIdIn(userStaffRoleIds);
            System.out.println("📅 Found " + assignments.size() + " assignments for user " + userId);

            // Get all quotation IDs
            List<Long> quotationIds = assignments.stream()
                    .map(StaffRoleAssignment::getQuotationId)
                    .distinct()
                    .collect(Collectors.toList());

            // Fetch all quotations at once (efficient)
            Map<Long, Quotation> quotationsMap = quotationRepository.findAllById(quotationIds)
                    .stream()
                    .collect(Collectors.toMap(Quotation::getId, Function.identity()));

            System.out.println("🗂️ Loaded " + quotationsMap.size() + " quotations");

            // Convert to DTOs with quotation info
            List<QuotationStaffController.StaffAssignmentWithQuotationDto> result = assignments.stream()
                    .map(assignment -> {
                        Quotation quotation = quotationsMap.get(assignment.getQuotationId());
                        return convertToDtoWithQuotation(assignment, quotation);
                    })
                    .collect(Collectors.toList());

            System.out.println("✅ Successfully converted " + result.size() + " assignments to DTOs");
            return result;

        } catch (Exception e) {
            System.err.println("❌ Error in getAssignmentsByUserWithQuotationInfo: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get user assignments: " + e.getMessage(), e);
        }
    }


}
