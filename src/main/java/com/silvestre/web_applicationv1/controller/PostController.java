package com.silvestre.web_applicationv1.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.silvestre.web_applicationv1.entity.Post;
import com.silvestre.web_applicationv1.repository.PostRepository;
import com.silvestre.web_applicationv1.service.PostService;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile imageFile, // This parameter was missing
            Principal principal) {
        
        // 1. Create a Post entity from the request parameters
        Post newPost = new Post();
        newPost.setTitle(title);
        newPost.setContent(content);

        // 2. Call service with ALL THREE parameters: Post, MultipartFile, String
        Post savedPost = postService.createPost(newPost, imageFile, principal.getName());
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }
}