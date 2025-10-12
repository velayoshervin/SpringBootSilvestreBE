package com.silvestre.web_applicationv1.controller;


import com.silvestre.web_applicationv1.entity.Otp;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.service.MessagingService;
import com.silvestre.web_applicationv1.service.OtpService;
import com.silvestre.web_applicationv1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public/otp")
public class OtpController {

    @Autowired
    private UserService userService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private MessagingService messagingService;

    @PostMapping
    ResponseEntity<?> requestOtp(@RequestParam Long userId){
        User user = userService.findUserById(userId);
        Otp generated = otpService.createOtpForUser(user);
        messagingService.sendOtp(generated.getOtpCode(), user.getFirstname());
        return ResponseEntity.ok(null);
    }
    @PostMapping("/resend")
    ResponseEntity<?> resendOtp(@RequestParam Long userId){
        User user = userService.findUserById(userId);
        Otp resend= otpService.resendOtp(user);
        messagingService.sendOtp(resend.getOtpCode(), user.getFirstname());
        return ResponseEntity.ok(null);
    }
    @PostMapping("/validate")
    ResponseEntity<?> validateOtp(@RequestBody Map<String,Object> requestBody){
        Number idNumber = (Number) requestBody.get("userId");
        Long userId = idNumber.longValue();
        String otp = String.valueOf(requestBody.get("otp"));
        User user = userService.findUserById(userId);
        Boolean result =otpService.validateOtp(user,otp);
        return ResponseEntity.ok(Map.of("valid", result));
    }
}
