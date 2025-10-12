package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.PackageBundleDTO;
import com.silvestre.web_applicationv1.entity.Item;
import com.silvestre.web_applicationv1.entity.PackageBundle;
import com.silvestre.web_applicationv1.repository.ItemRepo;
import com.silvestre.web_applicationv1.repository.PackageBundleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/package-bundle")
public class PackageBundleController {

    @Autowired
    private PackageBundleRepository repository;

    @Autowired
    private ItemRepo itemRepo;

    @PostMapping
    public ResponseEntity<?> createBundle(@RequestBody PackageBundle bundle){

        List<Item> attachedItems = bundle.getItems().stream()
                .map(item -> itemRepo.findById(item.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found")))
                .toList();
        bundle.setItems(attachedItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(bundle));
    }

    @GetMapping
    public List<PackageBundleDTO> getAllBundles(){
        List<PackageBundle>  bundles= repository.findAll();

        List<PackageBundleDTO> dtos = bundles.stream().map(bundle -> {
            PackageBundleDTO dto = new PackageBundleDTO();
            dto.setPackageBundleId(bundle.getPackageBundleId());
            dto.setName(bundle.getName());
            dto.setDescription(bundle.getDescription());
            dto.setCustomizable(bundle.isCustomizable());
            dto.setItems(bundle.getItems());
            return dto;
        }).toList();

        return dtos;
    }

    @GetMapping("/{id}")
    public PackageBundle getBundle(@PathVariable long id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Bundle not found"));
    }


}
