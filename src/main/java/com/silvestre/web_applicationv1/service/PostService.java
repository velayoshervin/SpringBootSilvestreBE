package com.silvestre.web_applicationv1.service;

import java.io.IOException;
import java.nio.file.Files; // ADDED
import java.nio.file.Path;  // ADDED
import java.nio.file.Paths; // ADDED
import java.nio.file.StandardCopyOption; // ADDED
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile; // Used for filename cleaning

import com.silvestre.web_applicationv1.entity.Post;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.PostRepository;
import com.silvestre.web_applicationv1.repository.UserRepository;

@Service
public class PostService {

    // Define the public URL prefix for the frontend and the local directory path
    private static final String PUBLIC_PATH_PREFIX = "/post_uploads/";
    
    // Define the base path for uploads (relative to application's root)
    private static final Path UPLOAD_PATH = Paths.get("post_uploads"); 

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(Post post, MultipartFile imageFile, String username) {
        try {
            System.out.println("🚀 Creating post for user: " + username);
            
            // Find user logic...
            User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            post.setAuthor(user);
            
            // Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                System.out.println("📸 Processing image file: " + imageFile.getOriginalFilename());
                String imageUrl = saveImage(imageFile);
                post.setImageUrl(imageUrl); 
                System.out.println("✅ Image saved with URL: " + imageUrl);
            } else {
                System.out.println("ℹ️ No image file provided");
            }
            
            // Save post logic...
            Post savedPost = postRepository.save(post);
            System.out.println("💾 Post saved successfully with ID: " + savedPost.getId());
            return savedPost;
            
        } catch (Exception e) {
            System.out.println("❌ Failed to create post (SERVER EXCEPTION): " + e.getMessage());
            e.printStackTrace(); // 🛑 CHECK THIS STACK TRACE FOR THE ROOT CAUSE!
            throw new RuntimeException("Failed to create post: " + e.getMessage(), e);
        }
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        
        // 1. CRITICAL: Create directory using NIO. This is more reliable than java.io.File.
        if (Files.notExists(UPLOAD_PATH)) {
            Files.createDirectories(UPLOAD_PATH);
            System.out.println("📁 Upload directory created at: " + UPLOAD_PATH.toAbsolutePath());
        } else {
            System.out.println("📁 Upload directory already exists at: " + UPLOAD_PATH.toAbsolutePath());
        }
        
        // 2. Generate unique filename
        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        
        // 3. Define the full destination path
        Path destinationFile = UPLOAD_PATH.resolve(fileName);
        
        // 4. Save file using NIO
        Files.copy(imageFile.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("✅ File saved successfully to: " + destinationFile.toAbsolutePath());
        
        // 5. Return the public URL path
        return PUBLIC_PATH_PREFIX + fileName; 
    }

    // ... (rest of your PostService methods: getAllPosts, getPostById, etc.)
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