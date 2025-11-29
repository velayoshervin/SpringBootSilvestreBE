package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.MenuBundle;
import com.silvestre.web_applicationv1.repository.MenuBundleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu-bundle")
public class MenuBundleController {

    @Autowired
    private MenuBundleRepository menuBundleRepository;

    @PostMapping
    public ResponseEntity<MenuBundle> createMenuBundle(@RequestBody MenuBundle menuBundle) {

        System.out.println("printing :"+menuBundle);

        if (menuBundleRepository.existsByName(menuBundle.getName())) {
            return ResponseEntity.badRequest().build();
        }
        MenuBundle saved = menuBundleRepository.save(menuBundle);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<MenuBundle>> getAllMenuBundles() {
        List<MenuBundle> bundles = menuBundleRepository.findAll();
        return ResponseEntity.ok(bundles);
    }

    @GetMapping("/active")
    public ResponseEntity<List<MenuBundle>> getActiveMenuBundles() {
        List<MenuBundle> activeBundles = menuBundleRepository.findByActiveTrue();
        return ResponseEntity.ok(activeBundles);
    }
    @GetMapping("/{id}")
    public ResponseEntity<MenuBundle> getMenuBundleById(@PathVariable Long id) {
        Optional<MenuBundle> menuBundle = menuBundleRepository.findById(id);
        return menuBundle.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuBundle> updateMenuBundle(@PathVariable Long id, @RequestBody MenuBundle menuBundleDetails) {
        Optional<MenuBundle> existing = menuBundleRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MenuBundle menuBundle = existing.get();

        // Check if name is being changed and if it already exists
        if (!menuBundle.getName().equals(menuBundleDetails.getName()) &&
                menuBundleRepository.existsByName(menuBundleDetails.getName())) {
            return ResponseEntity.badRequest().build();
        }

        // Update fields
        menuBundle.setName(menuBundleDetails.getName());
        menuBundle.setDescription(menuBundleDetails.getDescription());
        menuBundle.setBeefOptions(menuBundleDetails.getBeefOptions());
        menuBundle.setPorkOptions(menuBundleDetails.getPorkOptions());
        menuBundle.setChickenOptions(menuBundleDetails.getChickenOptions());
        menuBundle.setFishOptions(menuBundleDetails.getFishOptions());
        menuBundle.setVegetableOptions(menuBundleDetails.getVegetableOptions());
        menuBundle.setPastaOptions(menuBundleDetails.getPastaOptions());
        menuBundle.setDessertOptions(menuBundleDetails.getDessertOptions());
        menuBundle.setSoupOptions(menuBundleDetails.getSoupOptions());
        menuBundle.setJuiceOptions(menuBundleDetails.getJuiceOptions());
        menuBundle.setIncludesRice(menuBundleDetails.isIncludesRice());
        menuBundle.setIncludesWater(menuBundleDetails.isIncludesWater());
        menuBundle.setBasePrice(menuBundleDetails.getBasePrice());
        menuBundle.setActive(menuBundleDetails.isActive());
        menuBundle.setPreselectedFoods(menuBundleDetails.getPreselectedFoods());

        MenuBundle updated = menuBundleRepository.save(menuBundle);
        return ResponseEntity.ok(updated);
    }


}
