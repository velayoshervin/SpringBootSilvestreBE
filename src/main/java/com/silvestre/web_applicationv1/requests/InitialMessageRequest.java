package com.silvestre.web_applicationv1.requests;

import com.silvestre.web_applicationv1.entity.ChatAttachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InitialMessageRequest {
    private Long senderId;
    private Long receiverId;
    private String htmlContent;
    private List<AttachmentRequest> attachmentList;

}
