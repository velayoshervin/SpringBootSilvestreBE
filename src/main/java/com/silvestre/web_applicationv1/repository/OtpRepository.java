package com.silvestre.web_applicationv1.repository;


import com.silvestre.web_applicationv1.entity.Otp;
import com.silvestre.web_applicationv1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByUserAndOtpCodeAndUsedFalse(User user, String inputOtp);

    List<Otp> findByUserAndUsedFalseAndExpiryTimeAfter(User user, LocalDateTime now);
}
