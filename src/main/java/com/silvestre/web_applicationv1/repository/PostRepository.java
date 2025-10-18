package com.silvestre.web_applicationv1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.silvestre.web_applicationv1.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}