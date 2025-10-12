package com.silvestre.web_applicationv1.entity;

import com.silvestre.web_applicationv1.enums.FoodCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Food {

    @Id
    @GeneratedValue
    private Long foodId;

    private String description;

    private String stringUrl;

    @Enumerated(EnumType.STRING)
    private FoodCategory category;

    @ElementCollection
    @CollectionTable(
            name = "food_ingredients",
            joinColumns = @JoinColumn(name = "food_id")
    )
    @Column(name = "ingredient")
    private List<String> ingredients;

    private boolean specialty;
}
