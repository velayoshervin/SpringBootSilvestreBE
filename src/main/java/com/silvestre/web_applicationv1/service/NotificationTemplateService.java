package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.entity.NotificationTemplate;
import com.silvestre.web_applicationv1.repository.NotificationTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationTemplateService {

    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    public List<NotificationTemplate> findAllBaseNotificationTemplate(){
        return notificationTemplateRepository.findByIsBaseTemplateTrue();
    }

}
