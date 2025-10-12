package com.silvestre.web_applicationv1.requests;

import com.silvestre.web_applicationv1.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultationRequest {


    private Long consultationId;

    private String fullName;

    private String address;

    private Long userId;

    private String email;

    private String contact;

    private LocalDate eventDate;

    private String eventType;

    private String status;
}
