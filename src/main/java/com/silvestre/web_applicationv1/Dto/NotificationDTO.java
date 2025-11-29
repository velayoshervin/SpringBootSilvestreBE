package com.silvestre.web_applicationv1.Dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String sender;
    private Instant createdAt;
    private boolean read = false;
}
