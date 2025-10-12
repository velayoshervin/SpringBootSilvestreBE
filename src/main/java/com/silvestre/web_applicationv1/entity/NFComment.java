package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NFComment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private NFPost post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // who sends

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    private NFComment parent; // top-level comment has null

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NFLike> likes = new ArrayList<>();
}
