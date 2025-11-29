package com.silvestre.web_applicationv1.entity;


import com.silvestre.web_applicationv1.enums.NotificationUse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.service.annotation.GetExchange;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer offsetMinutes;

    @Builder.Default
    private Boolean isActive = true;

    private Boolean isBaseTemplate;
}
