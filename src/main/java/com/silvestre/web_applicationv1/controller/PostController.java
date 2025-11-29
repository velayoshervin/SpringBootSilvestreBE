//package com.silvestre.web_applicationv1.controller;
//
//import java.security.Principal;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.silvestre.web_applicationv1.entity.Post;
//import com.silvestre.web_applicationv1.repository.PostRepository;
//import com.silvestre.web_applicationv1.service.PostService;
//
//@RestController
//@RequestMapping("/api/posts")
//@CrossOrigin(origins = "http://localhost:5173")
//public class PostController {
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private PostService postService;
//
//    @GetMapping
//    public List<Post> getAllPosts() {
//        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
//    }
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Post> createPost(
//            @RequestParam("title") String title,
//            @RequestParam("content") String content,
//            @RequestPart(value = "image", required = false) MultipartFile imageFile, // This parameter was missing
//            Principal principal) {
//
//        // 1. Create a Post entity from the request parameters
//        Post newPost = new Post();
//        newPost.setTitle(title);
//        newPost.setContent(content);
//
//        // 2. Call service with ALL THREE parameters: Post, MultipartFile, String
//        Post savedPost = postService.createPost(newPost, imageFile, principal.getName());
//        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
//    }
//}

package com.silvestre.web_applicationv1.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.silvestre.web_applicationv1.entity.Comment;
import com.silvestre.web_applicationv1.entity.Post;
import com.silvestre.web_applicationv1.entity.Reply;
import com.silvestre.web_applicationv1.service.PostService;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        try {
            System.out.println("🎯 Fetching all posts...");
            List<Post> posts = postService.getAllPosts();

            for (Post post : posts) {
                if (post.getAuthor() != null) {
                    String authorName = post.getAuthor().getFirstname() + " " + post.getAuthor().getLastname();
                    post.setAuthorName(authorName);
                    System.out.println("🎯 Post " + post.getId() + " author: " + authorName);
                }
            }

            System.out.println("🎯 Successfully fetched " + posts.size() + " posts");
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            System.out.println("❌ Error fetching posts: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long postId) {
        try {
            int likeCount = postService.getLikeCount(postId);
            return ResponseEntity.ok(likeCount);
        } catch (Exception e) {
            System.out.println("❌ Failed to get like count: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{postId}/liked")
    public ResponseEntity<Boolean> hasUserLikedPost(@PathVariable Long postId, Principal principal) {
        try {
            boolean hasLiked = postService.hasUserLikedPost(postId, principal.getName());
            return ResponseEntity.ok(hasLiked);
        } catch (Exception e) {
            System.out.println("❌ Failed to check like status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId, Principal principal) {
        try {
            postService.likePost(postId, principal.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("❌ Failed to like post: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, Principal principal) {
        try {
            postService.unlikePost(postId, principal.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("❌ Failed to unlike post: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{postId}/comments/count")
    public ResponseEntity<Integer> getCommentsCount(@PathVariable Long postId) {
        try {
            int commentCount = postService.getCommentsCount(postId);
            return ResponseEntity.ok(commentCount);
        } catch (Exception e) {
            System.out.println("❌ Failed to get comments count: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<Reply>> getReplies(@PathVariable Long commentId) {
        try {
            List<Reply> replies = postService.getReplies(commentId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            System.out.println("❌ Failed to get replies: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Principal principal) {

        System.out.println("🎯 ========== CONTROLLER - POST CREATION START ==========");
        System.out.println("🎯 Principal name: " + principal.getName());
        System.out.println("🎯 Principal class: " + principal.getClass().getName());
        System.out.println("🎯 Title: " + title);
        System.out.println("🎯 Content length: " + content.length());
        System.out.println("🎯 Image file: " + (imageFile != null ? imageFile.getOriginalFilename() : "null"));

        Post newPost = new Post();
        newPost.setTitle(title);
        newPost.setContent(content);

        Post savedPost = postService.createPost(newPost, imageFile, principal.getName());

        if (savedPost.getAuthor() != null) {
            String authorName = savedPost.getAuthor().getFirstname() + " " + savedPost.getAuthor().getLastname();
            savedPost.setAuthorName(authorName);
        }

        System.out.println("🎯 ========== CONTROLLER - POST CREATION SUCCESS ==========");
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateDto postUpdateDto,
            Principal principal) {
        try {
            System.out.println("📝 Updating post: " + postId);

            Post postDetails = new Post();
            postDetails.setTitle(postUpdateDto.getTitle());
            postDetails.setContent(postUpdateDto.getContent());

            Post updatedPost = postService.updatePost(postId, postDetails);

            if (updatedPost.getAuthor() != null) {
                String authorName = updatedPost.getAuthor().getFirstname() + " " + updatedPost.getAuthor().getLastname();
                updatedPost.setAuthorName(authorName);
            }

            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            System.out.println("❌ Failed to update post: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Principal principal) {
        try {
            System.out.println("🗑️ Deleting post: " + postId);
            postService.deletePost(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("❌ Failed to delete post: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId,
                                              @RequestBody CommentDto commentDto,
                                              Principal principal) {
        try {
            Comment comment = postService.addComment(postId, commentDto.getText(), principal.getName());
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("❌ Failed to add comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        try {
            List<Comment> comments = postService.getComments(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            System.out.println("❌ Failed to get comments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<Reply> addReply(@PathVariable Long commentId,
                                          @RequestBody ReplyDto replyDto,
                                          Principal principal) {
        try {
            Reply reply = postService.addReply(commentId, replyDto.getText(), principal.getName());
            return new ResponseEntity<>(reply, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("❌ Failed to add reply: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Principal principal) {
        try {
            postService.deleteComment(commentId, principal.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("❌ Failed to delete comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}

class CommentDto {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

class ReplyDto {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

class PostUpdateDto {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}