package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Food;
import com.silvestre.web_applicationv1.enums.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food,Long> {


    Page<Food> findByCategory(FoodCategory category, Pageable pageable);
}
