package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.CustomPMPaymentMethods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomPMPaymentMethodsRepository extends JpaRepository<CustomPMPaymentMethods,Long> {

    Optional<CustomPMPaymentMethods> findByCode(String code);

    List<CustomPMPaymentMethods> findByIsActiveTrue();

    Optional<CustomPMPaymentMethods> findByCodeAndIsActiveTrue(String code);
}
