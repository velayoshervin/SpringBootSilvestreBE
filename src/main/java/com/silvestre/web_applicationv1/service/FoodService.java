package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.entity.Food;
import com.silvestre.web_applicationv1.enums.FoodCategory;
import com.silvestre.web_applicationv1.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    public Page<Food> getAllFoods(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }

    public Optional<Food> getFoodById(Long id) {
        return foodRepository.findById(id);
    }

    public Food createFood(Food food) {
        return foodRepository.save(food);
    }

    public Food updateFood(Long id, Food updatedFood) {
        return foodRepository.findById(id)
                .map(existing -> {
                    existing.setDescription(updatedFood.getDescription());
                    existing.setStringUrl(updatedFood.getStringUrl());
                    existing.setCategory(updatedFood.getCategory());
                    existing.setIngredients(updatedFood.getIngredients());
                    existing.setSpecialty(updatedFood.isSpecialty());
                    return foodRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Food not found"));
    }


    public Page<Food> getFoodByCategory(FoodCategory category,Pageable pageable) {

        return foodRepository.findByCategory(category,pageable);
    }
}
