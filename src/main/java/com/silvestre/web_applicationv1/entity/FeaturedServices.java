package com.silvestre.web_applicationv1.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeaturedServices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long featuredServiceId;

    private String serviceName;

    private String serviceDescription;

    //featured-video
    private String heroVideoUrl;

    private String heroDescription;

    //featured-gallery
    private String galleryTitle;
    private String galleryDescription;


    @ElementCollection
    private List<String> galleryImages;

    //Blooper section

    private String bloopersVideo;
    private String bloopersTitle;
    private String bloopersDescription;

}
