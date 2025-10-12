package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long itemId;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal perUnitExcess;

    //for filter like, rentals etc
    private String category;

    //determine if package, add-on or performer
    private String type;

    private int pax;

    @ManyToMany
    @JoinTable(
            name = "item_events", // name of the join table
            joinColumns = @JoinColumn(name = "item_id"), // FK to Student
            inverseJoinColumns = @JoinColumn(name = "event_id") // FK to Course
    )
    private Set<Event> recommendedForEvents;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVideo> videos = new ArrayList<>();

}
