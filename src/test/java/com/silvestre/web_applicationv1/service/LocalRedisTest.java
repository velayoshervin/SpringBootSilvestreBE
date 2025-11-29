package com.silvestre.web_applicationv1.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class LocalRedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testLocalRedis() {
        try {
            redisTemplate.opsForValue().set("test", "local-redis-working");
            String result = (String) redisTemplate.opsForValue().get("test");
            System.out.println("✅ Local Redis Success: " + result);
        } catch (Exception e) {
            System.err.println("❌ Local Redis failed: " + e.getMessage());
        }
    }
}