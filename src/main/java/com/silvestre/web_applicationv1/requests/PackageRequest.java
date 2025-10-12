package com.silvestre.web_applicationv1.requests;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PackageRequest {
        private String packageName;
        private String description;
        private List<String> imageUrls;
        private List<String> videoUrls;
        private List<Long> bundleIds;
    }

