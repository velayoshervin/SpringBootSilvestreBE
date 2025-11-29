package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private  SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/hello") //clients sends to app/hello
    @SendTo("/topic/greetings") // then broadcast to all subscribers of topic/greetings
    public String sendGreeting(String message){
        System.out.println("Received message + message");
        return "Hello to all users! Original message" + message;
    }


    @MessageMapping("/notify")
    public void sendPrivateMessage(@Payload NotificationMessage message) {

        String userId = message.getUserId();
        String destination = "/queue/notifications/" + userId; //✅ user-specific destination

        messagingTemplate.convertAndSendToUser(
                message.getUserId(),
                destination,
                message
        );

        System.out.println("Sent to user " + userId + ": " + message.getContent());
    }





}
