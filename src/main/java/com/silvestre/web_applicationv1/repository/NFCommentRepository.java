package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.NFComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NFCommentRepository extends JpaRepository<NFComment,Long> {
}
