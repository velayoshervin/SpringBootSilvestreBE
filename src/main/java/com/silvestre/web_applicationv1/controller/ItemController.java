package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.Event;
import com.silvestre.web_applicationv1.entity.Item;
import com.silvestre.web_applicationv1.entity.ItemPhoto;
import com.silvestre.web_applicationv1.entity.ItemVideo;
import com.silvestre.web_applicationv1.repository.EventRepository;
import com.silvestre.web_applicationv1.repository.ItemRepo;
import com.silvestre.web_applicationv1.requests.ItemRequest;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    @GetMapping("/food")
    public ResponseEntity<?> getFoods(){
        List<Item> foods = itemRepo.findByType("Food");
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/all-record")
    public ResponseEntity<?> getItemRecord(){
        List<Item> items =itemService.findAll();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{type}/{category}")
    public ResponseEntity<?> getItemsByTypeCategory(@PathVariable String type,
                                                    @PathVariable String category,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size

    ){
        System.out.println(type);
        System.out.println(category);
        Pageable pageable = PageRequest.of(page,size);
        Page<Item> items = itemService.findPageByTypeCategory(type,category,pageable);

        return ResponseEntity.ok(new PaginatedResponse<>(items));
    }



    @GetMapping
    public PaginatedResponse<Item> getPagedItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return itemService.getAllItemsPaged(page, size);
    }

    @GetMapping("/addOns")
    public PaginatedResponse<Item> getAddOns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return itemService.findPageByType(page,size, "add-on");
    }

    @GetMapping("/type")
    public PaginatedResponse<Item> getItemsByType(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String type){
        return itemService.findPageByType(page,size,type);
    }





   @GetMapping("/{itemId}")
    public Item getItem(@PathVariable Long itemId){
        return itemService.getItem(itemId);
    }

    @PostMapping
    public Item create(@RequestBody ItemRequest item){

        Item create = Item.builder().pax(item.getPax()).description(item.getDescription()).
                perUnitExcess(item.getPerUnitExcess()).name(item.getName()).
                price(item.getPrice()).category(item.getCategory()).
                type(item.getType()).build();

        Set<Long> eventIds= item.getRecommendedForEvents();
        Set<Event> events = eventIds.stream()
                .map(eventRepository::findById)          // returns Optional<Event>
                .flatMap(Optional::stream).collect(Collectors.toSet());            // unwrap existing events

        create.setRecommendedForEvents(events);

        if (item.getPhotos() != null) { 
            List<ItemPhoto> photoEntities = item.getPhotos().stream()
                    .map(url -> {
                        ItemPhoto photo = new ItemPhoto();
                        photo.setUrl(url);
                        photo.setItem(create);
                        return photo;
                    })
                    .collect(Collectors.toList());
            create.setPhotos(photoEntities);
        }

        if (item.getVideos() != null) {
            List<ItemVideo> videoEntities = item.getVideos().stream()
                    .map(url -> {
                        ItemVideo video = new ItemVideo();
                        video.setUrl(url);
                        video.setItem(create);
                        return video;
                    })
                    .collect(Collectors.toList());
            create.setVideos(videoEntities);
        }

        return itemService.createItem(create);
    }

    @PutMapping
    public Item update(@RequestBody ItemRequest itemRequest, @RequestParam Long id) {
        Item existing = itemService.getItem(id);

        existing.setName(itemRequest.getName());
        existing.setDescription(itemRequest.getDescription());
        existing.setPrice(itemRequest.getPrice());
        existing.setPerUnitExcess(itemRequest.getPerUnitExcess());
        existing.setCategory(itemRequest.getCategory());
        existing.setType(itemRequest.getType());
        existing.setPax(itemRequest.getPax());

        // Events
        Set<Event> events = itemRequest.getRecommendedForEvents().stream()
                .map(eventRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        existing.setRecommendedForEvents(events);

        // Photos: remove URLs not in the request
        existing.getPhotos().removeIf(photo -> !itemRequest.getPhotos().contains(photo.getUrl()));

        // Add new URLs
        for (String url : itemRequest.getPhotos()) {
            boolean exists = existing.getPhotos().stream()
                    .anyMatch(photo -> photo.getUrl().equals(url));
            if (!exists) {
                ItemPhoto newPhoto = new ItemPhoto();
                newPhoto.setUrl(url);
                newPhoto.setItem(existing);
                existing.getPhotos().add(newPhoto);
            }
        }

        // Videos: remove URLs not in the request
        existing.getVideos().removeIf(video -> !itemRequest.getVideos().contains(video.getUrl()));

        // Add new URLs
        for (String url : itemRequest.getVideos()) {
            boolean exists = existing.getVideos().stream()
                    .anyMatch(video -> video.getUrl().equals(url));
            if (!exists) {
                ItemVideo newVideo = new ItemVideo();
                newVideo.setUrl(url);   
                newVideo.setItem(existing);
                existing.getVideos().add(newVideo);
            }
        }
        return itemService.updateItem(existing);
    }

    }
