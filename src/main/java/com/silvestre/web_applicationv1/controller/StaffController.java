package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.StaffDTO;
import com.silvestre.web_applicationv1.entity.Staff;
import com.silvestre.web_applicationv1.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<List<StaffDTO>> getAllStaff() {
        List<Staff> staff = staffService.getAllActiveStaff();
        List<StaffDTO> staffDTOs = staff.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(staffDTOs);
    }

    private StaffDTO convertToDTO(Staff staff) {
        StaffDTO dto = new StaffDTO();
        dto.setStaffId(staff.getStaffId());
        dto.setName(staff.getName());
        dto.setRole(staff.getRole());
        dto.setCategory(staff.getCategory());
        dto.setAvatarInitials(staff.getAvatarInitials());
        dto.setContactInfo(staff.getContactInfo());
        dto.setIsActive(staff.getIsActive());
        return dto;
    }
}
