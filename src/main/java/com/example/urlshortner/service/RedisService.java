package com.example.urlshortner.service;

import com.example.urlshortner.util.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public String getUrl(String shortCode) {
        return get(RedisKeyUtil.urlKey(shortCode));
    }

    public void cacheUrl(String shortCode, String originalUrl) {
        redisTemplate.opsForValue().set(
                RedisKeyUtil.urlKey(shortCode),
                originalUrl,
                Duration.ofHours(1)
        );    }
}