package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.entity.Notification;
import com.silvestre.web_applicationv1.entity.ScheduledNotification;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.NotificationStatus;
import com.silvestre.web_applicationv1.repository.NotificationRepository;
import com.silvestre.web_applicationv1.repository.ScheduledNotificationRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledNotificationProcessor {

    @Autowired
    private ScheduledNotificationRepository scheduledNotificationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processDueNotifications() {
        LocalDateTime now = LocalDateTime.now();

        // Find all notifications that are due and still pending
        List<ScheduledNotification> dueNotifications = scheduledNotificationRepository
                .findByScheduledTimeBeforeAndNotificationStatus(now, NotificationStatus.PENDING);

        if (dueNotifications.isEmpty()) {
            return; // Nothing to process
        }

        System.out.println("🔔 Processing " + dueNotifications.size() + " due notifications");

        for (ScheduledNotification scheduled : dueNotifications) {
            try {
                // Create the actual user-facing Notification
                Notification userNotification = createUserNotification(scheduled);

                // Send via WebSocket to the user
                sendRealTimeNotification(scheduled, userNotification);

                // Mark as sent
                scheduled.setNotificationStatus(NotificationStatus.SENT);
                scheduled.setSentAt(LocalDateTime.now());

                System.out.println("✅ Sent notification: " + scheduled.getMessage());

            } catch (Exception e) {
                // Handle failure (log it, maybe retry later)
                scheduled.setNotificationStatus(NotificationStatus.FAILED);
                System.out.println("❌ Failed to send notification: " + e.getMessage());
            }

            // Save the updated status
            scheduledNotificationRepository.save(scheduled);
        }
    }

    /**
     * Create the actual Notification entity that users see
     */
    private Notification createUserNotification(ScheduledNotification scheduled) {
        // Get the user from the CalendarEvent
        User user = scheduled.getCalendarEventId().getEventCreator();

        Notification notification = new Notification();
        notification.setType("EVENT_REMINDER");
        notification.setTitle("Event Reminder");
        notification.setMessage(scheduled.getMessage());
        notification.setSender("System");
        notification.setUser(user);
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    /**
     * Send real-time notification via WebSocket
     */
    private void sendRealTimeNotification(ScheduledNotification scheduled, Notification userNotification) {
        try {
            User user = scheduled.getCalendarEventId().getEventCreator();

            simpMessagingTemplate.convertAndSendToUser(
                    user.getEmail(), // Or user.getId().toString() depending on your WebSocket config
                    "/queue/notifications",
                    userNotification
            );

        } catch (Exception e) {
            System.out.println("⚠️ WebSocket delivery failed, but notification is saved: " + e.getMessage());
            // Notification is still saved in database, so user will see it when they check their notifications
        }
    }
}
