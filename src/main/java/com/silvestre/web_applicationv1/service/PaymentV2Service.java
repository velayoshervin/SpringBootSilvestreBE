package com.silvestre.web_applicationv1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.silvestre.web_applicationv1.entity.PaymentV2;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.PaymentV2Repository;
import com.silvestre.web_applicationv1.repository.QuotationRepository;
import com.silvestre.web_applicationv1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentV2Service {

    @Autowired
    private PaymentV2Repository paymentRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private UserRepository userRepository;

    public PaymentV2 processSuccessfulPayment(JsonNode paymongoResponse, Long quotationId, Long userId) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentV2 payment = PaymentV2.fromPaymongoResponse(paymongoResponse, quotation, user);
        return paymentRepository.save(payment);
    }

    public boolean isQuotationFullyPaid(Long quotationId) {
        BigDecimal totalPaid = paymentRepository.getTotalPaidForQuotation(quotationId);
        Quotation quotation = quotationRepository.findById(quotationId).orElseThrow();
        return totalPaid.compareTo(quotation.getTotal()) >= 0;
    }

    public BigDecimal getRemainingBalance(Long quotationId) {
        BigDecimal totalPaid = paymentRepository.getTotalPaidForQuotation(quotationId);
        Quotation quotation = quotationRepository.findById(quotationId).orElseThrow();
        return quotation.getTotal().subtract(totalPaid).max(BigDecimal.ZERO);
    }

    public List<PaymentV2> getPaymentHistory(Long quotationId) {
        return paymentRepository.findByQuotationId(quotationId);
    }
}
