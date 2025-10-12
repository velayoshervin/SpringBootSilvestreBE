package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.UserDto;
import com.silvestre.web_applicationv1.entity.EmailToken;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.filter.JwtUtil;
import com.silvestre.web_applicationv1.properties.JwtProperties;
import com.silvestre.web_applicationv1.requests.UserIdRequest;
import com.silvestre.web_applicationv1.service.EmailTokenService;
import com.silvestre.web_applicationv1.service.MessagingService;
import com.silvestre.web_applicationv1.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/public/email-token")
public class EmailTokenController {

    @Autowired
    private EmailTokenService emailTokenService;
    @Autowired
    private  MessagingService messagingService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserService userService;

    @PostMapping("/resend")
    public  ResponseEntity<?> handleResend(@RequestBody UserIdRequest userIdRequest){

        User existing = userService.findUserById(userIdRequest.getUserId());
        String email = existing.getEmail();

        EmailToken generated = emailTokenService.resend(userIdRequest.getUserId());
        messagingService.sendToken(generated.getToken(),generated.getUser().getFirstname(),email);
        return ResponseEntity.status(HttpStatus.CREATED).body("new otp has been sent to user's email");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> handleValidation(@RequestParam String token, HttpServletResponse response) {
        System.out.println(token);
         Map<String,Object> objectMap = emailTokenService.validate(token);

         User user =(User)objectMap.get("user");
         UserDto userDto = (UserDto) objectMap.get("userDto");

        String jwt= jwtUtil.generateToken(user);


        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false) // set to false if you're not using HTTPS locally /Set this to false only during local development if you're not using HTTPS.
                .path("/")
                .maxAge(Duration.ofMillis(jwtProperties.getExpirationMs()))
                .sameSite("Lax") // or "Lax" -- none . check later
                .build();

        ;
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(userDto);
    }
}
