package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.UserStaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserStaffRoleRepo extends JpaRepository<UserStaffRole,Long> {

    // These should work with your entity structure
    List<UserStaffRole> findByStaffRoleStaffRoleId(Long staffRoleId);

    List<UserStaffRole> findByUserId(Long userId);

    // FIXED: Use the correct property path
    Optional<UserStaffRole> findByUserIdAndStaffRoleStaffRoleId(Long userId, Long staffRoleId);

    @Modifying
    @Query("DELETE FROM StaffRoleAssignment s WHERE s.quotationId = :quotationId")
    void deleteByQuotationId(@Param("quotationId") Long quotationId);
}
