package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.ChatMessageDto;
import com.silvestre.web_applicationv1.Dto.UserShowingRoleDto;
import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.ChatMessage;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.requests.ChatRoomMessageRequest;
import com.silvestre.web_applicationv1.requests.InitialMessageRequest;
import com.silvestre.web_applicationv1.requests.SendMessageRequest;
import com.silvestre.web_applicationv1.response.ChatRoomDto;
import com.silvestre.web_applicationv1.service.ChatService;
import com.silvestre.web_applicationv1.service.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@PreAuthorize("isAuthenticated()")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> loadMessages(@PathVariable Long roomId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "50") int size){

        List<ChatMessage> messages = chatService.loadMessages(roomId, page, size);

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/direct-message")
    public ResponseEntity<?> handleMessageRequest(@RequestBody InitialMessageRequest request){


    ChatRoomDto response= chatService.sendMessageRequest(request.getSenderId(),request.getReceiverId(), request.getHtmlContent(),
                request.getAttachmentList());

      return  ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/chatRooms/{userId}")
    public ResponseEntity<?> getChatRoomsByUserId(@PathVariable Long userId){
        List<ChatRoomDto>  chatRooms =  chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }


    @PostMapping("/{chatRoomId}/send-messages")
    public ResponseEntity<?> sendMessageToChat(
            @PathVariable Long chatRoomId,
            @RequestBody ChatRoomMessageRequest request) {

        Long userId = request.getSenderId();

        User sender= userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("invalid user"));

        ChatMessage message = chatService.sendMessageToChatRoom(chatRoomId, sender, request.getContent());

        ChatMessageDto dto = new ChatMessageDto();
        dto.setMessageId(message.getMessageId());
        dto.setChatRoomId(message.getChatRoom().getChatRoomId());
        dto.setMessageId(message.getMessageId());
        dto.setContent(message.getContent());
        dto.setTimeSent(message.getTimeSent());

        UserShowingRoleDto showingRoleDto= new UserShowingRoleDto(message.getSender());
        dto.setSender(showingRoleDto);

        return ResponseEntity.ok(dto);

    }

}
