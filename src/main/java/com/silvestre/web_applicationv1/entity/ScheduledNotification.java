package com.silvestre.web_applicationv1.entity;

import com.silvestre.web_applicationv1.enums.NotificationStatus;
import com.silvestre.web_applicationv1.enums.NotificationUse;
import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduledNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationUse notificationFor = NotificationUse.ALL;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(nullable = false, length = 500)
    private String message;

    @ManyToOne
    @JoinColumn(name = "calendar_event_id", nullable = false)
    private CalendarEvent calendarEventId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationStatus notificationStatus= NotificationStatus.PENDING;
}
