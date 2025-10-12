package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface ItemRepo extends JpaRepository<Item,Long> {

    public Page<Item> findByCategory(String category ,Pageable pageable);

    public Page<Item> findByType(String type , Pageable pageable);

    public List<Item> findByType(String type);

    Page<Item> findByTypeAndCategory(String type, String category,Pageable pageable);
}
