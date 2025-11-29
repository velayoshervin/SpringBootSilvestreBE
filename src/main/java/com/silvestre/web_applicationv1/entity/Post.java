//package com.silvestre.web_applicationv1.entity;
//
//import java.util.List;
//
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.OrderBy;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Table(name = "post")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class Post {
// @Id
//@GeneratedValue(strategy = GenerationType.IDENTITY)
//private Long id;
//
//private String title;
//
//private String content;
//
//
//private String imageUrl;
//
//
//
//@ManyToOne
// @JoinColumn(name = "user_id")
//private User author;
//
//@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
// @OrderBy("id DESC") // Newest comments first
//private List<Comment> comments;
//}

package com.silvestre.web_applicationv1.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "title", nullable = false)
 private String title;

 @Column(name = "content", columnDefinition = "TEXT")
 private String content;

 @Column(name = "image_url")
 private String imageUrl;

 @Column(name = "created_at")
 private LocalDateTime createdAt = LocalDateTime.now();



 // Other fields...

 @PrePersist
 protected void onCreate() {
  createdAt = LocalDateTime.now();
 }

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "author_id")
 @JsonIgnore
 private User author;

 @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
 @OrderBy("id ASC")
 @JsonIgnore
 private List<Comment> comments = new ArrayList<>();

 @Transient
 private String authorName;

 // Safe method to get author name
 public String getAuthorName() {
  if (author != null) {
   // Use the user's full name or email
   if (author.getFirstname() != null && author.getLastname() != null) {
    return author.getFirstname() + " " + author.getLastname();
   } else if (author.getEmail() != null) {
    return author.getEmail();
   }
  }
  return authorName;
 }

}