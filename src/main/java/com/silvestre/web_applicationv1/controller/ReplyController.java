package com.silvestre.web_applicationv1.controller;


import com.silvestre.web_applicationv1.Dto.CommentReplyDTO;
import com.silvestre.web_applicationv1.entity.Comment;
import com.silvestre.web_applicationv1.entity.Reply;
import com.silvestre.web_applicationv1.repository.CommentRepository;
import com.silvestre.web_applicationv1.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class ReplyController {


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    // KEEP THIS METHOD ONLY IN ReplyController
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<Reply> addReplyToComment(
            @PathVariable Long commentId,
            @RequestBody CommentReplyDTO replyDTO) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        Reply reply = new Reply();
        reply.setUsername(replyDTO.getUsername());
        reply.setText(replyDTO.getText());
        reply.setComment(comment);

        Reply savedReply = replyRepository.save(reply);
        return new ResponseEntity<>(savedReply, HttpStatus.CREATED);
    }
}
