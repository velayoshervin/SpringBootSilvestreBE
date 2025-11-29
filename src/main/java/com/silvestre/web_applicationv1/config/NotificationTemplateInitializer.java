package com.silvestre.web_applicationv1.config;

import com.silvestre.web_applicationv1.entity.NotificationTemplate;
import com.silvestre.web_applicationv1.enums.NotificationUse;
import com.silvestre.web_applicationv1.repository.NotificationTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NotificationTemplateInitializer implements CommandLineRunner {

    @Autowired
    private NotificationTemplateRepository repository;

    @Override
    public void run(String... args) throws Exception {
            if(repository.count() == 0)
                createDefaultTemplates();
    }


    private void createDefaultTemplates(){


        NotificationTemplate oneWeekBefore = NotificationTemplate.builder()
                .name("1_WEEK_BEFORE")
                .offsetMinutes(-10080) // 7 days * 24 hours * 60 minutes
                .isActive(true)
                .isBaseTemplate(true)
                .build();

        NotificationTemplate threeDaysBefore = NotificationTemplate.builder()
                .name("3_DAYS_BEFORE")
                .offsetMinutes(-4320) // 3 days * 24 hours * 60 minutes
                .isActive(true)
                .isBaseTemplate(true)
                .build();

        NotificationTemplate oneDayBefore = NotificationTemplate.builder()
                .name("1_DAY_BEFORE")
                .offsetMinutes(-1440) // 24 hours * 60 minutes
                .isActive(true)
                .isBaseTemplate(true)
                .build();

        NotificationTemplate oneHourBefore = NotificationTemplate.builder()
                .name("1_HOUR_BEFORE")
                .offsetMinutes(-60)
                .isActive(true)
                .isBaseTemplate(true)
                .build();

        NotificationTemplate fifteenMinutesBefore = NotificationTemplate.builder()
                .name("15_MINUTES_BEFORE")
                .offsetMinutes(-15)
                .isActive(true)
                .isBaseTemplate(true)
                .build();

        repository.save(oneDayBefore);
        repository.save(oneHourBefore);
        repository.save(fifteenMinutesBefore);

        System.out.println("✅ Created 3 default notification templates for ALL entity types");

    }
}
