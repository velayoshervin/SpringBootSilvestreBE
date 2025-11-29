package com.silvestre.web_applicationv1.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisConfigTest {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void printConfig() {
        System.out.println("Redis Host: " + redisHost);
        System.out.println("Redis Port: " + redisPort);

        // This shows the actual connection factory configuration
        LettuceConnectionFactory factory =
                (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        System.out.println("Connected to: " + factory.getHostName() + ":" + factory.getPort());
    }
}
