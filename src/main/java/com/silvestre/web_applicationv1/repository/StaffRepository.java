package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff,Long> {
    List<Staff> findByIsActiveTrue();
}
