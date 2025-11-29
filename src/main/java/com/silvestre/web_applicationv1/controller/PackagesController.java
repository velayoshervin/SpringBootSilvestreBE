package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.MenuBundle;
import com.silvestre.web_applicationv1.entity.PackageBundle;
import com.silvestre.web_applicationv1.entity.Packages;
import com.silvestre.web_applicationv1.repository.MenuBundleRepository;
import com.silvestre.web_applicationv1.repository.PackageBundleRepository;
import com.silvestre.web_applicationv1.repository.PackagesRepository;
import com.silvestre.web_applicationv1.requests.PackageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/public/api/packages")
public class PackagesController {

    @Autowired
    private PackagesRepository repository;

    @Autowired
    private PackageBundleRepository packageBundleRepository;

    @Autowired
    private MenuBundleRepository menuBundleRepository;

    @GetMapping
    public ResponseEntity<?> getAllPackages(){
        return  ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createPackage(@RequestBody PackageRequest dto) {
        Packages pkg = new Packages();
        pkg.setPackageName(dto.getPackageName());
        pkg.setDescription(dto.getDescription());
        pkg.setPhotoImageUrls(dto.getImageUrls());
        pkg.setVideoUrls(dto.getVideoUrls());

        MenuBundle menuBundle = null;

        if(dto.getMenuBundleId() !=null){

            Optional<MenuBundle> menuBundleOptional = menuBundleRepository.findById(dto.getMenuBundleId());

            if(menuBundleOptional.isPresent())
                menuBundle = menuBundleOptional.get();
            pkg.setMenuBundle(menuBundle);
        }

        List<PackageBundle> bundles = packageBundleRepository.findAllById(dto.getBundleIds());
        pkg.setPackageBundles(bundles);
        repository.save(pkg);
        return ResponseEntity.ok(pkg);
    }

    //@RequestMapping("public/api/packages") -- placed on class
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> handlePackageUpdate(@PathVariable Long id,@RequestBody PackageRequest packageRequest){

        Packages packages = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("package not found"));

        Optional<MenuBundle> menuBundle = menuBundleRepository.findById(packageRequest.getMenuBundleId()); //food

        menuBundle.ifPresent(packages::setMenuBundle);

        if(menuBundle.isPresent()){
            packages.setMenuBundlePrice(packageRequest.getMenuBundlePrice());
            packages.setMenuBundleTotalPrice(packageRequest.getMenuBundleTotal());
        }

        if (packageRequest.getBundleIds() != null && !packageRequest.getBundleIds().isEmpty()) {
            List<PackageBundle> bundles = packageBundleRepository.findAllById(packageRequest.getBundleIds());
            packages.setPackageBundles(bundles);
        } else {
            packages.setPackageBundles(new ArrayList<>());
        }

        packages.getPhotoImageUrls().clear();
        if (packageRequest.getImageUrls() != null) {
            packages.getPhotoImageUrls().addAll(packageRequest.getImageUrls());
        }

        // ✅ FIX: Clear and re-add video URLs
        packages.getVideoUrls().clear();
        if (packageRequest.getVideoUrls() != null) {
            packages.getVideoUrls().addAll(packageRequest.getVideoUrls());
        }


        packages.setPackageName(packageRequest.getPackageName());
        packages.setDescription(packageRequest.getDescription());
        packages.setPax(packageRequest.getPax());
        packages.setPrice(packageRequest.getGrandTotal());
//        packages.setPhotoImageUrls(packageRequest.getImageUrls());
//        packages.setVideoUrls(packageRequest.getVideoUrls());


        Packages update = repository.save(packages);

        return ResponseEntity.ok(update);
    }

}
