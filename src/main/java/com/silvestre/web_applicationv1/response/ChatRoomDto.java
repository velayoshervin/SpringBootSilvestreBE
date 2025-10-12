package com.silvestre.web_applicationv1.response;

import com.silvestre.web_applicationv1.Dto.ChatMessageDto;
import com.silvestre.web_applicationv1.Dto.UserDto;
import com.silvestre.web_applicationv1.Dto.UserShowingRoleDto;
import com.silvestre.web_applicationv1.entity.ChatRoom;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {

    private Long chatRoomId;
    private String name;
    private List<UserShowingRoleDto> participants;
    private List<ChatMessageDto> messages;
    private ChatMessageDto lastMessage;




    public ChatRoomDto(ChatRoom chatRoom){
        this.chatRoomId = chatRoom.getChatRoomId();
        this.name = chatRoom.getName();
      this.participants=  chatRoom.getParticipants().stream().map(UserShowingRoleDto::new).toList();
      this.messages= chatRoom.getMessages().stream().map(ChatMessageDto::new).toList();

    }
}
