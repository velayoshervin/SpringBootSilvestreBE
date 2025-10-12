package com.silvestre.web_applicationv1.repository;


import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {

    public Optional<User> findByEmail(String username);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    Optional<User> findFirstByRole(@Param("role") Role role);

}
