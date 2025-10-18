package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.FeaturedServices;
import com.silvestre.web_applicationv1.repository.FeaturedServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/featured-services")
public class FeaturedServicesController {

    @Autowired
    private FeaturedServicesRepository featuredServicesRepository;


    @GetMapping
    public ResponseEntity<List<FeaturedServices>> getAllServices() {
        try {
            List<FeaturedServices> services = featuredServicesRepository.findAll();
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeaturedServices> getServiceById(@PathVariable Long id) {
        try {
            Optional<FeaturedServices> service = featuredServicesRepository.findById(id);
            return service.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<FeaturedServices> createService(@RequestBody FeaturedServices service) {
        try {
            FeaturedServices savedService = featuredServicesRepository.save(service);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedService);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeaturedServices> updateService(@PathVariable Long id, @RequestBody FeaturedServices serviceDetails) {
        try {
            Optional<FeaturedServices> optionalService = featuredServicesRepository.findById(id);

            if (optionalService.isPresent()) {
                FeaturedServices service = optionalService.get();

                // Update fields
                service.setServiceName(serviceDetails.getServiceName());
                service.setServiceDescription(serviceDetails.getServiceDescription());
                service.setHeroVideoUrl(serviceDetails.getHeroVideoUrl());
                service.setHeroDescription(serviceDetails.getHeroDescription());
                service.setGalleryTitle(serviceDetails.getGalleryTitle());
                service.setGalleryDescription(serviceDetails.getGalleryDescription());
                service.setGalleryImages(serviceDetails.getGalleryImages());
                service.setBloopersVideo(serviceDetails.getBloopersVideo());
                service.setBloopersTitle(serviceDetails.getBloopersTitle());
                service.setBloopersDescription(serviceDetails.getBloopersDescription());

                FeaturedServices updatedService = featuredServicesRepository.save(service);
                return ResponseEntity.ok(updatedService);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        try {
            if (featuredServicesRepository.existsById(id)) {
                featuredServicesRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
