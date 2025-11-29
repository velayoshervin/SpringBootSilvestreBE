//package com.silvestre.web_applicationv1.service;
//
//import java.io.IOException;
//import java.nio.file.Files; // ADDED
//import java.nio.file.Path;  // ADDED
//import java.nio.file.Paths; // ADDED
//import java.nio.file.StandardCopyOption; // ADDED
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile; // Used for filename cleaning
//
//import com.silvestre.web_applicationv1.entity.Post;
//import com.silvestre.web_applicationv1.entity.User;
//import com.silvestre.web_applicationv1.repository.PostRepository;
//import com.silvestre.web_applicationv1.repository.UserRepository;
//
//@Service
//public class PostService {
//
//    // Define the public URL prefix for the frontend and the local directory path
//    private static final String PUBLIC_PATH_PREFIX = "/post_uploads/";
//
//    // Define the base path for uploads (relative to application's root)
//    private static final Path UPLOAD_PATH = Paths.get("post_uploads");
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public Post createPost(Post post, MultipartFile imageFile, String username) {
//        try {
//            System.out.println("🚀 Creating post for user: " + username);
//
//            // Find user logic...
//            User user = userRepository.findByEmail(username)
//                .orElseThrow(() -> new RuntimeException("User not found: " + username));
//
//            post.setAuthor(user);
//
//            // Handle image upload
//            if (imageFile != null && !imageFile.isEmpty()) {
//                System.out.println("📸 Processing image file: " + imageFile.getOriginalFilename());
//                String imageUrl = saveImage(imageFile);
//                post.setImageUrl(imageUrl);
//                System.out.println("✅ Image saved with URL: " + imageUrl);
//            } else {
//                System.out.println("ℹ️ No image file provided");
//            }
//
//            // Save post logic...
//            Post savedPost = postRepository.save(post);
//            System.out.println("💾 Post saved successfully with ID: " + savedPost.getId());
//            return savedPost;
//
//        } catch (Exception e) {
//            System.out.println("❌ Failed to create post (SERVER EXCEPTION): " + e.getMessage());
//            e.printStackTrace(); // 🛑 CHECK THIS STACK TRACE FOR THE ROOT CAUSE!
//            throw new RuntimeException("Failed to create post: " + e.getMessage(), e);
//        }
//    }
//
//    private String saveImage(MultipartFile imageFile) throws IOException {
//
//        // 1. CRITICAL: Create directory using NIO. This is more reliable than java.io.File.
//        if (Files.notExists(UPLOAD_PATH)) {
//            Files.createDirectories(UPLOAD_PATH);
//            System.out.println("📁 Upload directory created at: " + UPLOAD_PATH.toAbsolutePath());
//        } else {
//            System.out.println("📁 Upload directory already exists at: " + UPLOAD_PATH.toAbsolutePath());
//        }
//
//        // 2. Generate unique filename
//        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
//        String fileName = System.currentTimeMillis() + "_" + originalFilename;
//
//        // 3. Define the full destination path
//        Path destinationFile = UPLOAD_PATH.resolve(fileName);
//
//        // 4. Save file using NIO
//        Files.copy(imageFile.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
//
//        System.out.println("✅ File saved successfully to: " + destinationFile.toAbsolutePath());
//
//        // 5. Return the public URL path
//        return PUBLIC_PATH_PREFIX + fileName;
//    }
//
//    // ... (rest of your PostService methods: getAllPosts, getPostById, etc.)
//    public List<Post> getAllPosts() {
//        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
//        System.out.println("📋 Retrieved " + posts.size() + " posts");
//        return posts;
//    }
//    public Optional<Post> getPostById(Long id) {
//        return postRepository.findById(id);
//    }
//    public Post updatePost(Long id, Post postDetails) {
//        Post post = postRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
//
//        post.setTitle(postDetails.getTitle());
//        post.setContent(postDetails.getContent());
//
//        return postRepository.save(post);
//    }
//    public void deletePost(Long id) {
//        Post post = postRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
//
//        postRepository.delete(post);
//    }
//}

package com.silvestre.web_applicationv1.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.silvestre.web_applicationv1.entity.Comment;
import com.silvestre.web_applicationv1.entity.Post;
import com.silvestre.web_applicationv1.entity.Reply;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.CommentRepository;
import com.silvestre.web_applicationv1.repository.PostRepository;
import com.silvestre.web_applicationv1.repository.ReplyRepository;
import com.silvestre.web_applicationv1.repository.UserRepository;

@Service
public class PostService {

    private static final String PUBLIC_PATH_PREFIX = "/post_uploads/";
    private static final Path UPLOAD_PATH = Paths.get("post_uploads");

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    public int getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return 0;
    }

    public boolean hasUserLikedPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return false;
    }

    public void likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        System.out.println("👍 Like functionality not implemented yet for post: " + postId);
    }

    public void unlikePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        System.out.println("👎 Unlike functionality not implemented yet for post: " + postId);
    }

    public int getCommentsCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        long count = commentRepository.countByPost(post);
        return (int) count;
    }

    public Comment addComment(Long postId, String text, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setUsername(username);
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    public List<Comment> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return commentRepository.findByPostOrderByIdAsc(post);
    }

    public Reply addReply(Long commentId, String text, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Reply reply = new Reply();
        reply.setText(text);
        reply.setUsername(username);
        reply.setComment(comment);

        return replyRepository.save(reply);
    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    public Post createPost(Post post, MultipartFile imageFile, String username) {
        try {
            System.out.println("🚀 ========== POST CREATION START ==========");
            System.out.println("📝 Username from principal: " + username);

            System.out.println("👤 STEP 1 - Looking up user by email: " + username);
            Optional<User> userOptional = userRepository.findByEmail(username);

            if (userOptional.isEmpty()) {
                System.out.println("❌ USER LOOKUP FAILED - No user found with email: " + username);
                System.out.println("🔍 Available users in database:");
                userRepository.findAll().forEach(u -> System.out.println("   - " + u.getEmail()));
                throw new RuntimeException("User not found: " + username);
            }

            User user = userOptional.get();
            System.out.println("✅ STEP 1 SUCCESS - User found: " + user.getEmail() + " (ID: " + user.getId() + ")");
            post.setAuthor(user);

            if (imageFile != null && !imageFile.isEmpty()) {
                System.out.println("🖼️ STEP 2 - Processing image file...");
                try {
                    String imageUrl = saveImage(imageFile);
                    post.setImageUrl(imageUrl);
                    System.out.println("✅ STEP 2 SUCCESS - Image saved: " + imageUrl);
                } catch (Exception e) {
                    System.out.println("❌ STEP 2 FAILED - Image processing error: " + e.getMessage());
                    throw e;
                }
            } else {
                System.out.println("ℹ️ STEP 2 SKIPPED - No image provided");
                post.setImageUrl(null);
            }

            System.out.println("💾 STEP 3 - Preparing post for database save...");
            System.out.println("   - Title: " + post.getTitle());
            System.out.println("   - Content: " + post.getContent());
            System.out.println("   - Author: " + (post.getAuthor() != null ? post.getAuthor().getEmail() : "NULL"));
            System.out.println("   - Image URL: " + post.getImageUrl());

            System.out.println("💾 STEP 4 - Saving post to database...");
            try {
                Post savedPost = postRepository.save(post);
                System.out.println("✅ STEP 4 SUCCESS - Post saved with ID: " + savedPost.getId());
                System.out.println("🎉 ========== POST CREATION COMPLETE ==========");
                return savedPost;
            } catch (Exception e) {
                System.out.println("❌ STEP 4 FAILED - Database save error: " + e.getMessage());
                System.out.println("❌ Error type: " + e.getClass().getName());
                e.printStackTrace();
                throw e;
            }

        } catch (Exception e) {
            System.out.println("💥 ========== POST CREATION FAILED ==========");
            System.out.println("💥 Final error: " + e.getMessage());
            System.out.println("💥 Error type: " + e.getClass().getName());
            e.printStackTrace();
            System.out.println("💥 ===========================================");
            throw new RuntimeException("Failed to create post: " + e.getMessage(), e);
        }
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        try {
            System.out.println("🖼️ Starting image save process...");

            if (Files.notExists(UPLOAD_PATH)) {
                System.out.println("📁 Creating upload directory...");
                Files.createDirectories(UPLOAD_PATH);
                System.out.println("✅ Upload directory created at: " + UPLOAD_PATH.toAbsolutePath());
            } else {
                System.out.println("📁 Upload directory exists at: " + UPLOAD_PATH.toAbsolutePath());
            }

            System.out.println("🔐 Checking directory permissions...");
            System.out.println("   - Readable: " + Files.isReadable(UPLOAD_PATH));
            System.out.println("   - Writable: " + Files.isWritable(UPLOAD_PATH));
            System.out.println("   - Executable: " + Files.isExecutable(UPLOAD_PATH));

            String originalFilename = org.springframework.util.StringUtils.cleanPath(imageFile.getOriginalFilename());
            String fileName = System.currentTimeMillis() + "_" + originalFilename;
            Path destinationFile = UPLOAD_PATH.resolve(fileName);

            System.out.println("💾 Saving file: " + fileName);
            System.out.println("   - Destination: " + destinationFile.toAbsolutePath());

            Files.copy(imageFile.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            if (Files.exists(destinationFile)) {
                long fileSize = Files.size(destinationFile);
                System.out.println("✅ File saved successfully!");
                System.out.println("   - Final size: " + fileSize + " bytes");
                System.out.println("   - Final path: " + destinationFile.toAbsolutePath());
            } else {
                System.out.println("❌ File save verification failed - file doesn't exist!");
            }

            String publicUrl = PUBLIC_PATH_PREFIX + fileName;
            System.out.println("🌐 Public URL: " + publicUrl);
            return publicUrl;

        } catch (Exception e) {
            System.out.println("❌ Image save process failed: " + e.getMessage());
            throw e;
        }
    }



    public List<Reply> getReplies(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return replyRepository.findByCommentOrderByIdAsc(comment);
    }

    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        System.out.println("📋 Retrieved " + posts.size() + " posts");
        return posts;
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());

        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        postRepository.delete(post);
    }
}