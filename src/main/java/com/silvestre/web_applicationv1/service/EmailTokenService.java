package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.Dto.UserDto;
import com.silvestre.web_applicationv1.ExceptionHandler.EmailTokenExpiredException;
import com.silvestre.web_applicationv1.ExceptionHandler.InvalidTokenException;
import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.EmailToken;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.EmailTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailTokenService {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailTokenRepository emailTokenRepository;

    @Autowired
    private  MessagingService messagingService;

    public EmailToken resend(Long userId){

        List<EmailToken> existingTokens = emailTokenRepository.findByUserId(userId);
        User existing= userService.findUserById(userId);
        existingTokens.forEach(
                token-> {
                    token.setUsed(true);
                }
        );
        emailTokenRepository.saveAll(existingTokens);

        return generateEmailToken(existing);
    }

    public Map<String,Object> validate(String token){

        EmailToken existingToken = emailTokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(()-> new InvalidTokenException("token is invalid")
        );
        if (existingToken.getExpiry().isBefore(LocalDateTime.now()))
            throw new EmailTokenExpiredException("Token expired");

        existingToken.setUsed(true);
        emailTokenRepository.save(existingToken);
        User user= existingToken.getUser();
        System.out.println(user.getId());
        user.setVerifiedEmail(true);
        userService.save(user);
        UserDto userDto= UserDto.builder().
                email(user.getEmail()).userId(user.getId())
                .firstname(user.getFirstname()).
                lastname(user.getLastname()).
                build();

        return Map.of("userDto", userDto, "user", user );
    }

    public EmailToken generateEmailToken(User user){
        String token =  UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        EmailToken emailToken= new EmailToken();
        emailToken.setToken(token);
        emailToken.setExpiry(LocalDateTime.now().plusMinutes(10));
        emailToken.setUser(user);
        emailTokenRepository.save(emailToken);
        return emailToken;
    }

    public void sendVerificationToken(User user){
        EmailToken emailToken= generateEmailToken(user);
        String token = emailToken.getToken();
        String name = user.getFirstname();
        String email= user.getEmail();
        messagingService.sendToken(token,name,email);
    }

}
