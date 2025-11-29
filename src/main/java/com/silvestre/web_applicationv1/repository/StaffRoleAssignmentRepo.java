package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.StaffRoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRoleAssignmentRepo extends JpaRepository<StaffRoleAssignment,Long> {

    List<StaffRoleAssignment> findByQuotationId(Long quotationId);

    void deleteByQuotationId(Long quotationId);

    boolean existsByQuotationIdAndUserStaffRoleId(Long quotationId, Long userStaffRoleId);

    List<StaffRoleAssignment> findByUserStaffRoleIdIn(List<Long> userStaffRoleIds);
}
