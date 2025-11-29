package com.silvestre.web_applicationv1.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@SpringBootTest
public class RedisTestConnection {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisConnection() {
        System.out.println("🧪 Testing Redis Cloud connection...");

        try {
            // Test basic operations
            redisTemplate.opsForValue().set("test-key", "Hello Redis Cloud!", Duration.ofMinutes(1));

            String value = (String) redisTemplate.opsForValue().get("test-key");

            if ("Hello Redis Cloud!".equals(value)) {
                System.out.println("✅ SUCCESS: Connected to Redis Cloud!");
                System.out.println("📨 Retrieved value: " + value);
            } else {
                System.out.println("❌ FAILED: Values don't match");
            }

        } catch (Exception e) {
            System.err.println("❌ FAILED: Could not connect to Redis Cloud");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

