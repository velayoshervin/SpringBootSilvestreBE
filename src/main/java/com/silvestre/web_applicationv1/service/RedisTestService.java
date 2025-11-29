package com.silvestre.web_applicationv1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTestService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void testWriteRead() {
        String key = "test:message";
        String value = "Hello from Spring Boot 🚀";

        // Save to Redis
        redisTemplate.opsForValue().set(key, value);

        // Read back
        Object retrieved = redisTemplate.opsForValue().get(key);

        System.out.println("✅ Saved key: " + key);
        System.out.println("📨 Retrieved value: " + retrieved);
    }
}
