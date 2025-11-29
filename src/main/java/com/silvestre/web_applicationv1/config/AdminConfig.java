package com.silvestre.web_applicationv1.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class AdminConfig {

    @Value("${APP_ADMIN_ID}")
    private Long adminId;
}
