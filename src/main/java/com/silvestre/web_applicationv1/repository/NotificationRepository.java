package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Notification;
import com.silvestre.web_applicationv1.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
