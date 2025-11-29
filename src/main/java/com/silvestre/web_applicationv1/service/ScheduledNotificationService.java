package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.entity.CalendarEvent;
import com.silvestre.web_applicationv1.entity.NotificationTemplate;
import com.silvestre.web_applicationv1.entity.ScheduledNotification;
import com.silvestre.web_applicationv1.enums.NotificationStatus;
import com.silvestre.web_applicationv1.repository.NotificationTemplateRepository;
import com.silvestre.web_applicationv1.repository.ScheduledNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledNotificationService {

    @Autowired
    private ScheduledNotificationRepository repository;

    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;


    public void scheduleEventNotifications(CalendarEvent calendarEvent){

        List<NotificationTemplate> templates =notificationTemplateRepository.findByIsBaseTemplateTrue();

        for (NotificationTemplate template : templates) {

            LocalDateTime scheduledTime =  calendarEvent.getStartTime().toLocalDateTime().plusMinutes(template.getOffsetMinutes());

            if (!shouldScheduleNotification(scheduledTime)) {
                continue;
            }

            String message = generateMessage(calendarEvent.getTitle(),template.getName());
            ScheduledNotification notification = ScheduledNotification.builder()
                    .scheduledTime(scheduledTime)
                    .message(message)
                    .calendarEventId(calendarEvent)  // Direct reference to the event
                    .notificationStatus(NotificationStatus.PENDING)
                    .build();

            repository.save(notification);
        }



    }

    public String generateMessage(String eventTitle,String templateName ){

        if ("1_DAY_BEFORE".equals(templateName)) {
            return "Reminder: \"" + eventTitle + "\" is tomorrow";
        } else if ("1_HOUR_BEFORE".equals(templateName)) {
            return "Upcoming: \"" + eventTitle + "\" in 1 hour";
        } else if ("15_MINUTES_BEFORE".equals(templateName)) {
            return "Upcoming: \"" + eventTitle + "\" in 15 minutes";
        } else {
            return "Reminder: \"" + eventTitle + "\"";
        }

    }

    public boolean shouldScheduleNotification(LocalDateTime scheduledTime) {
        return !scheduledTime.isBefore(LocalDateTime.now());
    }

}
