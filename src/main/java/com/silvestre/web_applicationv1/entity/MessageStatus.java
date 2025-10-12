package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="chat_message_id")
    private ChatMessage message;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    private boolean isRead = false;

    private Instant readAt;
}
