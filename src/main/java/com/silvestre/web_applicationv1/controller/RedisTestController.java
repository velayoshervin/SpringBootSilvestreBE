package com.silvestre.web_applicationv1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/public/test-redis")
public class RedisTestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("")
    public String testRedis() {
        try {
            redisTemplate.opsForValue().set("testKey", "Hello Redis!");
            String value = (String) redisTemplate.opsForValue().get("testKey");
            return "✅ Redis connection OK — value: " + value;
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Redis connection failed: " + e.getMessage();
        }
    }

    @PostMapping("/set-temp")
    public String setTempKey(){
        String key = "test:expire:123";
        redisTemplate.opsForValue().set(key,"Allowed Time", Duration.ofSeconds(30));
        return "✅ Key " + key + " set with 30s TTL";
    }

    @GetMapping("/test-expire")
    public String testExpire() {
        String key = "booking:123";
        redisTemplate.opsForValue().set(key, "temporary booking", Duration.ofSeconds(30));
        return "✅ Set key '" + key + "' to expire in 30 seconds.";
    }

}
