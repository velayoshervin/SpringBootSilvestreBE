package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.Dto.ChatMessageDto;
import com.silvestre.web_applicationv1.Dto.UserDto;
import com.silvestre.web_applicationv1.Dto.UserShowingRoleDto;
import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.ChatAttachment;
import com.silvestre.web_applicationv1.entity.ChatMessage;
import com.silvestre.web_applicationv1.entity.ChatRoom;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.ChatAttachmentRepository;
import com.silvestre.web_applicationv1.repository.ChatMessageRepository;
import com.silvestre.web_applicationv1.repository.ChatRoomRepository;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.requests.AttachmentRequest;
import com.silvestre.web_applicationv1.response.ChatRoomDto;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatAttachmentRepository chatAttachmentRepository;

    @Autowired
    private UserRepository userRepository;


    //this one is for creating gc
    public ChatRoom createChatRoom(List<Long> participantIds, String name) {
        ChatRoom room = new ChatRoom();
        room.setName(name);

        for (Long userId : participantIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            room.getParticipants().add(user);
        }

        return chatRoomRepository.save(room);
    }

    public ChatMessage sendMessageToChatRoom(Long chatroomId, User sender, String content){
        ChatRoom existing = chatRoomRepository.findById(chatroomId).orElseThrow(()->
                new ResourceNotFoundException("chat room doesnt exist"));

        boolean isParticipant = existing.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(sender.getId()));

        if (!isParticipant) {
            throw new SecurityException("User is not a participant in this chat");
        }

        ChatMessage message = new ChatMessage();
        message.setChatRoom(existing);
        message.setSender(sender);
        message.setContent(content);
        message.setTimeSent(Instant.now());
        return chatMessageRepository.save(message);
    }


    public ChatRoomDto sendMessageRequest(Long senderId, Long targetId, String htmlContent, List<AttachmentRequest> attachments){

    //so far attachment is not supported

        User sender = userRepository.findById(senderId).orElseThrow(()->
                new ResourceNotFoundException("invalid sender Id"));
        User receiver =userRepository.findById(targetId).orElseThrow(()->
                new ResourceNotFoundException("invalid receiver Id"));

        String safeHtml = sanitizeHtml(htmlContent);

        ChatMessage chatMessage= new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setContent(safeHtml);
        chatMessage.setTimeSent(Instant.now());



        ChatRoom room =  new ChatRoom();
        room.getMessages().add(chatMessage);
        room.getParticipants().add(sender);
        room.getParticipants().add(receiver);


        chatMessage.setChatRoom(room);

        ChatRoom save = chatRoomRepository.save(room);

        return new ChatRoomDto(save);

    }




    public ChatMessage sendMessage(Long chatRoomId, Long senderId, String content,
                                   List<AttachmentRequest> attachments) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setChatRoom(room);
        message.setContent(content);

        // Add attachments if any
        if (attachments != null) {
            for (AttachmentRequest ar : attachments) {
                ChatAttachment attachment = new ChatAttachment();
                attachment.setFilename(ar.getFilename());
                attachment.setUrl(ar.getUrl());
                attachment.setFileSize(ar.getFileSize());
                attachment.setChatMessage(message);

                message.getAttachments().add(attachment);
            }
        }

        return chatMessageRepository.save(message);
    }


    public List<ChatMessage> loadMessages(Long roomId, int page, int size) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messagePage =
                chatMessageRepository.findByChatRoomOrderByTimeSentDesc(room, pageable);
        List<ChatMessage> messages = messagePage.getContent();

        Collections.reverse(messages);

        return messages;
    }

    public List<ChatRoomDto> getUserChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserId(userId);

        return chatRooms.stream()
                .map(this::convertToChatRoomDto)
                .collect(Collectors.toList());
    }

    private ChatRoomDto convertToChatRoomDto(ChatRoom chatRoom) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setChatRoomId(chatRoom.getChatRoomId());
        dto.setName(chatRoom.getName());


        // Convert participants
        dto.setParticipants(chatRoom.getParticipants().stream()
                .map(UserShowingRoleDto::new)
                .collect(Collectors.toList()));

        // Get last message for preview
        if (!chatRoom.getMessages().isEmpty()) {
            ChatMessage lastMessage = chatRoom.getMessages().get(chatRoom.getMessages().size() - 1);

            ChatMessageDto chatMessageDto = new ChatMessageDto(lastMessage);
            dto.setLastMessage(chatMessageDto);
           List<ChatMessageDto> chatMessageDtoList =chatRoom.getMessages().stream().map(ChatMessageDto::new).toList();

            dto.setMessages(chatMessageDtoList);
        }

        return dto;
    }



    private String sanitizeHtml(String html) {
        return Jsoup.clean(html, Safelist.basic());
    }
}
