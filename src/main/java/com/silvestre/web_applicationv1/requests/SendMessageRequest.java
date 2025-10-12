package com.silvestre.web_applicationv1.requests;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMessageRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
    private List<AttachmentRequest> attachments;
}
