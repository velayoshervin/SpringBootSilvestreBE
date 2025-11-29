package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.entity.Staff;
import com.silvestre.web_applicationv1.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;


    public List<Staff> getAllActiveStaff() {
        return staffRepository.findByIsActiveTrue();
    }

    public Staff getStaffById(Long staffId) {
        return staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));
    }
}
