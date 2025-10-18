package com.silvestre.web_applicationv1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    private final String UPLOAD_LOCATION = "file:" + System.getProperty("user.dir") + "/post_uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/post_uploads/**")
                .addResourceLocations(UPLOAD_LOCATION);
    }
}