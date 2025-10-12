package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.PackageBundle;
import com.silvestre.web_applicationv1.entity.Packages;
import com.silvestre.web_applicationv1.repository.PackageBundleRepository;
import com.silvestre.web_applicationv1.repository.PackagesRepository;
import com.silvestre.web_applicationv1.requests.PackageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("public/api/packages")
public class PackagesController {

    @Autowired
    private PackagesRepository repository;

    @Autowired
    private PackageBundleRepository packageBundleRepository;

    @GetMapping
    private ResponseEntity<?> getAllPackages(){
        return  ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createPackage(@RequestBody PackageRequest dto) {
        Packages pkg = new Packages();
        pkg.setPackageName(dto.getPackageName());
        pkg.setDescription(dto.getDescription());
        pkg.setPhotoImageUrls(dto.getImageUrls());
        pkg.setVideoUrls(dto.getVideoUrls());

        List<PackageBundle> bundles = packageBundleRepository.findAllById(dto.getBundleIds());
        pkg.setPackageBundles(bundles);
        repository.save(pkg);
        return ResponseEntity.ok(pkg);
    }




}
