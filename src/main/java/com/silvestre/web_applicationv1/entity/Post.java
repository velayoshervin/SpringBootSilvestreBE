package com.silvestre.web_applicationv1.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne; 
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
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

private String title;

private String content;


private String imageUrl; 



@ManyToOne 
 @JoinColumn(name = "user_id") 
private User author; 

@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
 @OrderBy("id DESC") // Newest comments first
private List<Comment> comments;
}