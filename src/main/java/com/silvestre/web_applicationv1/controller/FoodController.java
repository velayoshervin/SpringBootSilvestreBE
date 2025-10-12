package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.Food;
import com.silvestre.web_applicationv1.enums.FoodCategory;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/api/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping
    public ResponseEntity<?> getAllFoods(@PageableDefault(size = 10,sort = "category",
            direction = Sort.Direction.DESC)Pageable pageable){

         Page<Food> foods = foodService.getAllFoods(pageable);

         return ResponseEntity.ok(new PaginatedResponse<>(foods));
    }

    @GetMapping("/category")
    public ResponseEntity<?> getFoodsByCategory(@RequestParam FoodCategory category,@PageableDefault(size = 10)Pageable pageable){
        Page<Food> foods= foodService.getFoodByCategory(category,pageable);
        return  ResponseEntity.ok(new PaginatedResponse<>(foods));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFoodById(@PathVariable Long id){

        Food food = foodService.getFoodById(id).orElseThrow(()-> new ResourceNotFoundException("food not found"));
        return ResponseEntity.ok(food);
    }

    @PostMapping()
    public ResponseEntity<?> createFood(@RequestBody Food food){

        Food created = foodService.createFood(food);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody Food food, @PathVariable Long id){

        Food updated= foodService.updateFood( id,food);

        return  ResponseEntity.ok(updated);

    }

}
