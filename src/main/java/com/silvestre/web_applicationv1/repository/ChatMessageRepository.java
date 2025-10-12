package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.ChatMessage;
import com.silvestre.web_applicationv1.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByChatRoomOrderByTimeSentDesc(ChatRoom room, Pageable pageable);
}
