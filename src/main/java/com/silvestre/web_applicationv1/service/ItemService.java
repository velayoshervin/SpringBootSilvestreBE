package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.Item;
import com.silvestre.web_applicationv1.enums.ItemType;
import com.silvestre.web_applicationv1.repository.ItemRepo;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepo itemRepo;

    public Item createItem(Item item){
        return itemRepo.save(item);
    }

    public Item updateItem(Item existing){

    return itemRepo.save(existing);
    }

    public Item getItem(Long itemId){
        return itemRepo.findById(itemId).orElseThrow(()-> new ResourceNotFoundException("item doesn't exist"));
    }

    public PaginatedResponse<Item> getAllItemsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> pageData = itemRepo.findAll(pageable);
        return new PaginatedResponse<>(pageData);
    }

    public List<Item> getAllItemsNotPaged(){
        return itemRepo.findAll();
    }

    public PaginatedResponse<Item> findPageByType(int page, int size, String type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> pageData = itemRepo.findByType(type,pageable);
        return new PaginatedResponse<>(pageData);
    }






    public List<Item> findAll() {
        return itemRepo.findAll(Sort.by(Sort.Order.asc("type"), Sort.Order.asc("category")));
    }

    public Page<Item> findPageByTypeCategory( String type, String category,Pageable pageable) {
      return itemRepo.findByTypeAndCategory(type,category,pageable);
    }
}
