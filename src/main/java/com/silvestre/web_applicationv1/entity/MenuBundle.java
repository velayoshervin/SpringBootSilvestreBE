package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuBundleId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int beefOptions;
    private int porkOptions;
    private int chickenOptions;
    private int fishOptions;
    private int vegetableOptions;
    private int pastaOptions;
    private int dessertOptions;
    private int soupOptions;
    private int juiceOptions;
    private boolean includesRice;
    private boolean includesWater;
    
    @Column(columnDefinition = "TEXT")
    private String preselectedFoods;

    private BigDecimal basePrice;

    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
