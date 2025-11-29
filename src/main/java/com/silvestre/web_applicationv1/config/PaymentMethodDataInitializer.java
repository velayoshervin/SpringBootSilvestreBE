package com.silvestre.web_applicationv1.config;

import com.silvestre.web_applicationv1.entity.CustomPMPaymentMethods;
import com.silvestre.web_applicationv1.enums.FeeType;
import com.silvestre.web_applicationv1.repository.CustomPMPaymentMethodsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentMethodDataInitializer {

    @Autowired
    private CustomPMPaymentMethodsRepository paymentMethodRepository;


    @PostConstruct
    public void init() {
        if (paymentMethodRepository.count() == 0) {
            // GCash - Percentage only

            CustomPMPaymentMethods gCash = new CustomPMPaymentMethods();
            gCash.setCode("gcash");
            gCash.setName("GCash");
            gCash.setFeeRate(new BigDecimal("0.025"));
            gCash.setFeeType(FeeType.PERCENTAGE);

            paymentMethodRepository.save(gCash);


            CustomPMPaymentMethods grabPay = new CustomPMPaymentMethods();
            grabPay.setCode("grabpay");
            grabPay.setName("GrabPay");
            grabPay.setFeeRate(new BigDecimal("0.022"));
            grabPay.setFeeType(FeeType.PERCENTAGE);

            paymentMethodRepository.save(grabPay);

            CustomPMPaymentMethods payMaya = new CustomPMPaymentMethods();
            payMaya.setCode("paymaya");
            payMaya.setName("PayMaya");
            payMaya.setFeeRate(new BigDecimal("0.02"));
            payMaya.setFeeType(FeeType.PERCENTAGE);

            paymentMethodRepository.save(payMaya);


            CustomPMPaymentMethods card = new CustomPMPaymentMethods();
            card.setCode("card");
            card.setName("Credit/Debit Card");
            card.setFeeRate(new BigDecimal("0.035"));
            card.setMinimumFee(new BigDecimal("15.00"));
            card.setFeeType(FeeType.HYBRID);

            paymentMethodRepository.save(card);

        }
    }
}
