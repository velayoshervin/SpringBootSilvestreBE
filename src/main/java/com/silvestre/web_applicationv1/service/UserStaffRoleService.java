package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.Dto.UserShowingRoleDto;
import com.silvestre.web_applicationv1.controller.UserStaffRoleController;
import com.silvestre.web_applicationv1.entity.StaffRole;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.entity.UserStaffRole;
import com.silvestre.web_applicationv1.repository.StaffRoleRepo;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.repository.UserStaffRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserStaffRoleService {

    @Autowired
    private UserStaffRoleRepo userStaffRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRoleRepo staffRoleRepository;

    public List<UserStaffRoleController.UserStaffRoleDto> getAllUserRoleAssignments() {
        return userStaffRoleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserStaffRoleController.UserStaffRoleDto assignRoleToUser(UserStaffRoleController.AssignRoleRequest request) {
        // Check if assignment already exists
        Optional<UserStaffRole> existingAssignment = userStaffRoleRepository
                .findByUserIdAndStaffRoleStaffRoleId(request.getUserId(), request.getStaffRoleId());

        if (existingAssignment.isPresent()) {
            throw new RuntimeException("User already has this role assigned");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        StaffRole staffRole = staffRoleRepository.findById(request.getStaffRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserStaffRole assignment = new UserStaffRole();
        assignment.setUser(user);
        assignment.setStaffRole(staffRole);

        UserStaffRole saved = userStaffRoleRepository.save(assignment);
        return convertToDto(saved);
    }

    public void removeRoleAssignment(Long assignmentId) {
        UserStaffRole assignment = userStaffRoleRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        userStaffRoleRepository.delete(assignment);
    }

    public List<UserStaffRoleController.UserStaffRoleDto> getUsersByRole(Long roleId) {
        return userStaffRoleRepository.findByStaffRoleStaffRoleId(roleId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserStaffRoleController.UserStaffRoleDto> getRolesByUser(Long userId) {
        return userStaffRoleRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserStaffRoleController.UserStaffRoleDto convertToDto(UserStaffRole assignment) {
        UserShowingRoleDto userDto = new UserShowingRoleDto(assignment.getUser());
        UserStaffRoleController.StaffRoleDto roleDto = new UserStaffRoleController.StaffRoleDto(
                assignment.getStaffRole().getStaffRoleId(),
                assignment.getStaffRole().getRoleName(),
                assignment.getStaffRole().getRoleDescription()
        );

        return new UserStaffRoleController.UserStaffRoleDto(assignment.getId(), userDto, roleDto);
    }

}
