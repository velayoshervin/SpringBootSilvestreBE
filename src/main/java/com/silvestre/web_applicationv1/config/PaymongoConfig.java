package com.silvestre.web_applicationv1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class PaymongoConfig {

    @Value("${paymongo.secretKey}")
    private String secretKey;

    @Value("${paymongo.publicKey}")
    private String publicKey;

    @Bean
    public String paymongoSecretAuthHeader() {
        // This returns something like: "Basic c2tfdGVzdF8xMjM0NTY6"
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }

    @Bean
    public String paymongoPublicAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((publicKey + ":").getBytes());
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}

//paymongo.base-url= https://api.paymongo.com/v1
//paymongo.checkout-session-endpoint= /checkout_sessions
//paymongo.cancel-url=""
//paymongo.success-url="PAYMONGO_SUCCESS_URL"
//paymongo.statement_descriptor= "" #possibly name of store
//paymongo.secretKey=${PAYMONGO_SECRET_KEY}
//paymongo.publicKey=${PAYMONGO_PUBLIC_KEY}
