package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.ChatRoom;
import com.silvestre.web_applicationv1.entity.MessageStatus;
import com.silvestre.web_applicationv1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {

    long countByRecipientAndIsReadFalse(User recipient);

    List<MessageStatus> findByRecipientAndMessage_ChatRoomAndIsReadFalse(
            User recipient, ChatRoom chatRoom);

    @Modifying
    @Query("UPDATE MessageStatus ms SET ms.isRead = true, ms.readAt = :now " +
            "WHERE ms.recipient = :user AND ms.message.chatRoom = :chatRoom AND ms.isRead = false")
    void markMessagesAsRead(@Param("user") User user,
                            @Param("chatRoom") ChatRoom chatRoom,
                            @Param("now") Instant now);
}
