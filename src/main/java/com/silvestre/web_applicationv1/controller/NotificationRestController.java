package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.Notification;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.NotificationRepository;
import com.silvestre.web_applicationv1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NotificationRestController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/api/test-notify")
    public String testNotify() {
        Long userId = 30L; // Your predetermined user ID
        String message = "Test notification from server!";

        // Create notification
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "System Notification");
        notification.put("content", message);
        notification.put("timestamp", Instant.now().toString());
        notification.put("type", "TEST");

        // Send to user 30 via WebSocket
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );

        return "✅ Notification sent to user " + userId;
    }

//    @GetMapping("/api/{userId}/notifications")
//    public List<Notification> getNotificationsForUserId(@PathVariable Long userId){
//
//        User existing = userService.findUserById(userId);
//
//        Pageable pageable = PageRequest.of(0,20);
//        return notificationRepository.findByUserOrderByCreatedAtDesc(existing,pageable);
//    }


    @GetMapping("/api/{userId}/notifications")
    public List<Notification> getNotificationsForUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User existing = userService.findUserById(userId);

        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUserOrderByCreatedAtDesc(existing, pageable);
    }


    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Notification notif = notificationRepository.findById(id).orElseThrow();
        notif.setRead(true);
        notificationRepository.save(notif);
        return ResponseEntity.ok().build();
    }

}

