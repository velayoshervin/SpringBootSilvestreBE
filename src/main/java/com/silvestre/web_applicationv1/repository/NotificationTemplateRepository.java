package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate,Long> {

    List<NotificationTemplate> findByIsBaseTemplateTrue();
}
