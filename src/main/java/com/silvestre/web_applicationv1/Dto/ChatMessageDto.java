package com.silvestre.web_applicationv1.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.silvestre.web_applicationv1.entity.ChatAttachment;
import com.silvestre.web_applicationv1.entity.ChatMessage;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {

    private Long messageId;
    private UserShowingRoleDto sender;
    private Long chatRoomId;
    private String content;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Manila")
    private Instant timeSent;

    private List<ChatAttachmentDTO> attachments;


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatAttachmentDTO {
        private Long id;
        private String url;
        // add more fields if needed

        public ChatAttachmentDTO(ChatAttachment attachment){
            this.id = attachment.getAttachmentId();
            this.url = attachment.getUrl();
        }
    }

    public ChatMessageDto(ChatMessage chatMessage){
        this.messageId = chatMessage.getMessageId();

        this.sender = new UserShowingRoleDto(chatMessage.getSender());

        this.chatRoomId = chatMessage.getChatRoom().getChatRoomId();

        this.content = chatMessage.getContent();

        this.timeSent= chatMessage.getTimeSent();

        List<ChatAttachment> attachmentList = chatMessage.getAttachments();

        this.attachments = attachmentList.stream().map(ChatAttachmentDTO::new).toList();

    }

}
