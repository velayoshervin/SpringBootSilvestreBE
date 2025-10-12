package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Packages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    private String packageName;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "packages_package_bundle",
            joinColumns = @JoinColumn(name = "package_id"),
            inverseJoinColumns = @JoinColumn(name = "package_bundle_id")
    )
    private List<PackageBundle> packageBundles = new ArrayList<>();


    @ElementCollection
    @CollectionTable(
            name = "package_video_urls",
            joinColumns = @JoinColumn(name = "package_id")
    )
    @Column(name = "video_url")
    private List<String> videoUrls = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "package_photo_urls",
            joinColumns = @JoinColumn(name = "package_id")
    )
    @Column(name = "photo_url")
    private List<String> photoImageUrls = new ArrayList<>();
}


