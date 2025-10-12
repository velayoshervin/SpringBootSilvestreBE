package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventThemes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ElementCollection
    @CollectionTable(
            name = "theme_photo_url;",
            joinColumns = @JoinColumn(name = "food_id")
    )
    @Column(name = "photoUrl")
    private List<String> photoUrl;

    @ElementCollection
    @CollectionTable(
            name = "theme_video_url;",
            joinColumns = @JoinColumn(name = "food_id")
    )
    @Column(name = "videoUrl")
    private List<String> video;
}
