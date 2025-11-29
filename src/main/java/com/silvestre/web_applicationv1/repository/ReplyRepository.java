package com.silvestre.web_applicationv1.repository;


import com.silvestre.web_applicationv1.entity.Comment;
import com.silvestre.web_applicationv1.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply,Long> {
    List<Reply> findByCommentOrderByIdAsc(Comment comment);
}