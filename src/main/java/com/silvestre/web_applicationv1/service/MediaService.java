package com.silvestre.web_applicationv1.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MediaService {
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserService userService;


    public List<Map<String, Object>> uploadAlbum(MultipartFile[] files, String folderName) throws IOException {
        List<Map<String, Object>> uploadedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", "albums/" + folderName,
                    "tags", List.of("album-" + folderName)
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            Map<String, Object> result = Map.of(
                    "public_id", uploadResult.get("public_id"),
                    "url", uploadResult.get("secure_url"),
                    "format", uploadResult.get("format"),
                    "bytes", uploadResult.get("bytes"),
                    "width", uploadResult.get("width"),
                    "height", uploadResult.get("height"),
                    "created_at", uploadResult.get("created_at")
            );

            uploadedImages.add(result);
        }

        return uploadedImages;
    }


    public UserResponse uploadAvatar(MultipartFile file, Long userId) {
        System.out.println("uploading avatar reached");
        User saved = new User();
        try {
            User user = userService.findUserById(userId);

            Map uploadParams = ObjectUtils.asMap(
                    "folder", "users/" + userId + "/avatars",
                    "transformation", new Transformation()
                            .width(500).height(500)
                            .crop("fill").gravity("face")
                            .quality("auto")
                            .fetchFormat("auto")
            );
            // Delete old avatar if exists
            if (user.getAvatarPublicId() != null) {
                cloudinary.uploader().destroy(user.getAvatarPublicId(), ObjectUtils.emptyMap());
            }
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            String secureUrl = result.get("secure_url").toString();
            String publicId = result.get("public_id").toString();
            user.setAvatarUrl(secureUrl);
            user.setAvatarPublicId(publicId);
            saved=  userService.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar to Cloudinary", e);
        }
        return new UserResponse(saved);
    }

}
