package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.Notification;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification n) {
        if (n == null) return null;

        return NotificationDTO.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .read(n.isRead())
                .message(n.getMessage())
                .sender(n.getSender())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
