package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class    Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long venueId;

    private String name;

    private String address;

    private String imageUrl;

    @Column(name = "rental_amount")
    private Long rentalAmount;
}
