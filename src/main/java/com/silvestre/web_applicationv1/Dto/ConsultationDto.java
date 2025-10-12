package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.Consultation;
import com.silvestre.web_applicationv1.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationDto {

    private  Long consultationId;

    private UserDto user;

    private String fullName;

    private String email;

    private String contact;

    private LocalDate eventDate;

    private String eventType;

    private String status;

    private LocalDateTime created;

    private LocalDateTime updated;

    public ConsultationDto(Consultation consultation){

        this.user = new UserDto(consultation.getUser());
        this.email = consultation.getEmail();
        this.contact= consultation.getContact();
        this.eventDate = consultation.getEventDate();
        this.status= consultation.getStatus();
        this.eventType = consultation.getEventType();
        this.fullName =consultation.getFullName();
        this.created= consultation.getCreatedAt();
        this.updated = consultation.getUpdatedAt();
        this.consultationId = consultation.getConsultationId();
    }

}


