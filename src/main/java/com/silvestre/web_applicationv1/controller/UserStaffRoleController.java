package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.UserShowingRoleDto;
import com.silvestre.web_applicationv1.repository.UserStaffRoleRepo;
import com.silvestre.web_applicationv1.service.UserStaffRoleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/user-staff-roles")
@Validated
public class UserStaffRoleController {

    @Autowired
    private UserStaffRoleService userStaffRoleService;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStaffRoleDto {
        private Long id;
        private UserShowingRoleDto user;
        private StaffRoleDto staffRole;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffRoleDto {
        private Long staffRoleId;
        private String roleName;
        private String roleDescription;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignRoleRequest {
        private Long userId;
        private Long staffRoleId;
    }


    @GetMapping
    public ResponseEntity<List<UserStaffRoleDto>> getAllUserRoleAssignments() {
        List<UserStaffRoleDto> assignments = userStaffRoleService.getAllUserRoleAssignments();
        return ResponseEntity.ok(assignments);
    }

    @PostMapping
    public ResponseEntity<UserStaffRoleDto> assignRoleToUser(@Valid @RequestBody AssignRoleRequest request) {
        UserStaffRoleDto assignment = userStaffRoleService.assignRoleToUser(request);
        return ResponseEntity.ok(assignment);
    }

    // Remove role assignment
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<?> removeRoleAssignment(@PathVariable Long assignmentId) {
        userStaffRoleService.removeRoleAssignment(assignmentId);
        return ResponseEntity.ok().build();
    }

    // Get users by role
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<UserStaffRoleDto>> getUsersByRole(@PathVariable Long roleId) {
        List<UserStaffRoleDto> users = userStaffRoleService.getUsersByRole(roleId);
        return ResponseEntity.ok(users);
    }

    // Get roles by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserStaffRoleDto>> getRolesByUser(@PathVariable Long userId) {
        List<UserStaffRoleDto> roles = userStaffRoleService.getRolesByUser(userId);
        return ResponseEntity.ok(roles);
    }


}
