package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.Dto.NotificationDTO;
import com.silvestre.web_applicationv1.Dto.NotificationMapper;
import com.silvestre.web_applicationv1.entity.Notification;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    public void sendToUser(String userId, NotificationDTO notification) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }

    public void sendToTopic(String topic, NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/" + topic, notification);
    }

    public List<NotificationDTO> getUserNotifications(Long userId, int page, int size) {

        User user= userService.findUserById(userId);

        List<Notification> notifications = notificationRepository
                .findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, size));

        return notifications.stream()
                .map(NotificationMapper::toDTO
                        )
                .collect(Collectors.toList());
    }

    public void createAndSendNotificationToAdmin(String messageContent, Long adminId, String title, String type) {
        // 1️⃣ Find admin user
        User admin = userService.findUserById(adminId);
        if (admin == null) return;

        // 2️⃣ Create notification entity and save to DB
        Notification notification = new Notification();
        notification.setUser(admin);
        notification.setTitle(title);
        notification.setMessage(messageContent);
        notification.setType(type);
        notification.setMessage(messageContent);
        notificationRepository.save(notification);

        NotificationDTO notificationDTO = NotificationMapper.toDTO(notification);

        // 3️⃣ Send notification in real-time via WebSocket
        messagingTemplate.convertAndSendToUser(
                admin.getEmail(),               // username used in WebSocket session
                "/queue/notifications",        // matches your @SendToUser
                notificationDTO
        );
        System.out.println("📢 Sent to user ID: " +  admin.getEmail());
    }

     public Notification save(Notification notification){
        return notificationRepository.save(notification);
     }

}
