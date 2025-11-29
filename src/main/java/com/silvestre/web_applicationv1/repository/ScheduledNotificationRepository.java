package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.ScheduledNotification;
import com.silvestre.web_applicationv1.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledNotificationRepository extends JpaRepository<ScheduledNotification, Long> {
    List<ScheduledNotification> findByScheduledTimeBeforeAndNotificationStatus(LocalDateTime now, NotificationStatus notificationStatus);
}
