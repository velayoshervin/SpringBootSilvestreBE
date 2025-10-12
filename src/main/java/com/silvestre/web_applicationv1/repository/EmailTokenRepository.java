package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken,Long> {

    List<EmailToken> findByUserId(Long userId);

    Optional<EmailToken> findByTokenAndUsedFalse(String token);
}
