package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.CommentReplyDTO;
import com.silvestre.web_applicationv1.entity.Comment;
import com.silvestre.web_applicationv1.entity.Post;
import com.silvestre.web_applicationv1.repository.CommentRepository;
import com.silvestre.web_applicationv1.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    // GET /api/comments is what the frontend calls before client-side filtering
    @GetMapping
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
    
    // POST /api/comments/post/{postId}
    @PostMapping("/post/{postId}")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId, @RequestBody CommentReplyDTO commentDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment newComment = new Comment();
        newComment.setUsername(commentDTO.getUsername());
        newComment.setText(commentDTO.getText());
        newComment.setPost(post);

        Comment savedComment = commentRepository.save(newComment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }
}