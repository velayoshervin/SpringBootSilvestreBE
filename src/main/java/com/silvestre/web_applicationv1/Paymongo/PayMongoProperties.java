package com.silvestre.web_applicationv1.Paymongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "paymongo")
@Getter
@Setter
public class PayMongoProperties {

    private String secretKey;
    private String publicKey;
    private String baseUrl;
    private String checkoutSessionEndpoint;
}

