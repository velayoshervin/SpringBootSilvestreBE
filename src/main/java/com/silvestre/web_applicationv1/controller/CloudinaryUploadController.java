package com.silvestre.web_applicationv1.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.silvestre.web_applicationv1.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

import org.apache.commons.codec.binary.Hex;

@RestController
@RequestMapping("/upload")
public class CloudinaryUploadController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping //Good for profile
    public String upload(@RequestParam("file") MultipartFile file) throws IOException, IOException {
        Map<?, ?> uploadParams = ObjectUtils.asMap(
                "folder", "dev-profile-images" //
        );
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return result.get("secure_url").toString();
        }

    @PostMapping("/album")
    public ResponseEntity<?> uploadAlbum(@RequestParam("files") MultipartFile[] files,
                                         @RequestParam("folder") String folderName) {
        try {
            List<Map<String, Object>> uploaded = mediaService.uploadAlbum(files, folderName);
            return ResponseEntity.ok(uploaded);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}