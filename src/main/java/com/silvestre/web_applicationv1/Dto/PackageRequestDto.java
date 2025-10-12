package com.silvestre.web_applicationv1.Dto;

import java.util.List;

public class PackageRequestDto {
    private String packageName;

    // Send existing bundle IDs instead of full entities
    private List<Long> bundleIds;
    private String description;

    // Optional: media URLs
    private List<String> videoUrls;
    private List<String> imageUrls;
}
