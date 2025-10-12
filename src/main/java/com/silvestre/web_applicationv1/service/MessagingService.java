package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.MailSenderException;
import com.silvestre.web_applicationv1.util.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class MessagingService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendOtp(String otp, String name){
        Context context= new Context();
        context.setVariable("otp", otp);
        context.setVariable("name", name);
        try{
            String htmlContent=templateEngine.process("Otp",context);
            MimeMessage message= javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setTo("velayoshervin69@gmail.com");
            helper.setSubject("One Time Password");
            helper.setText(htmlContent,true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailSenderException("failed to send OTP message. something went wrong");
        }
    }

    public void sendToken(String token, String name, String email){
        //add the ff : email

        String link = "http://localhost:5173/verify?token=" + token;

        Context context= new Context();
        context.setVariable("name", name);
        context.setVariable("link",link);

        try{
        String htmlContent=templateEngine.process("EnableAccount",context);
            MimeMessage message= javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setTo(email);
            helper.setSubject("Activate Account");
            helper.setText(htmlContent,true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailSenderException("failed to send message. something went wrong");
        }
    }
}
