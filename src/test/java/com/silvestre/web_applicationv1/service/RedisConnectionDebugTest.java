package com.silvestre.web_applicationv1.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisConnectionDebugTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void debugConnection() {
        LettuceConnectionFactory factory =
                (LettuceConnectionFactory) redisTemplate.getConnectionFactory();

        System.out.println("=== Redis Connection Details ===");
        System.out.println("Host: " + factory.getHostName());
        System.out.println("Port: " + factory.getPort());
        System.out.println("SSL: " + factory.isUseSsl());

        try {
            System.out.println("=== Testing Connection ===");
            // Try a simple ping first
            String result = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();
            System.out.println("Ping successful: " + result);

        } catch (Exception e) {
            System.err.println("=== CONNECTION FAILED ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());

            // Print the full stack trace to see root cause
            e.printStackTrace();
        }
    }
}
