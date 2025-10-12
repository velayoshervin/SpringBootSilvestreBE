package com.silvestre.web_applicationv1.service;


import com.silvestre.web_applicationv1.ExceptionHandler.ExpiredOtpException;
import com.silvestre.web_applicationv1.ExceptionHandler.InvalidOtpException;
import com.silvestre.web_applicationv1.entity.Otp;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    public Otp createOtpForUser(User user) {
        String otpCode = generateOtp();
        Otp otp = new Otp();
        otp.setUser(user);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        return otpRepository.save(otp);
    }

    public boolean validateOtp(User user, String inputOtp) {
        Optional<Otp> otpOpt = otpRepository.findByUserAndOtpCodeAndUsedFalse(user, inputOtp);
        if(otpOpt.isEmpty())
            throw new InvalidOtpException("Invalid otp");

        Otp otp = otpOpt.get();

        if (otp.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new ExpiredOtpException("otp has expired.");

        otp.setUsed(true);
        otpRepository.save(otp);
        return true;
    }

    public Otp resendOtp(User user) {
        // Invalidate all previous valid OTPs for this user
        List<Otp> activeOtps = otpRepository.findByUserAndUsedFalseAndExpiryTimeAfter(user, LocalDateTime.now());
        for (Otp otp : activeOtps) {
            otp.setUsed(true);
        }
        otpRepository.saveAll(activeOtps);

        // Generate and return a new one
        return createOtpForUser(user);
    }

    private String generateOtp() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
