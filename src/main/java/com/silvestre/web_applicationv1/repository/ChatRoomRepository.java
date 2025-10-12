package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

    @Query("SELECT DISTINCT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.id = :userId")
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);

    // Alternative: with pagination
    @Query("SELECT DISTINCT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.id = :userId")
    Page<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId, Pageable pageable);
}
