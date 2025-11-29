package com.silvestre.web_applicationv1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class QuotationRedisService {


    @Autowired
    private StringRedisTemplate redisTemplate;

    public void trackQuotationExpiration(Long quotationId, int ttlSeconds) {
        String key = "quotation:" + quotationId;
        String value = "temporary"; // optional notes
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    public boolean isQuotationActive(Long quotationId) {
        String key = "quotation:" + quotationId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void removeQuotation(Long quotationId) {
        String key = "quotation:" + quotationId;
        redisTemplate.delete(key);
    }


}
