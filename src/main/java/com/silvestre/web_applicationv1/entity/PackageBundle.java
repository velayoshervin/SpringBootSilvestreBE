package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PackageBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageBundleId;

    private String name;

    private String description;

    private boolean customizable;

    @ManyToMany
    @JoinTable(
            name = "package_bundle_item",
            joinColumns = @JoinColumn(name = "package_bundle_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();


    @Transient
    public BigDecimal getTotalPrice() {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        return items.stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
