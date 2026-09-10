package com.example.urlshortner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisService redisService;

    @BeforeEach
    void setUp() {
        redisService = new RedisService(redisTemplate);
    }

    @Test
    void shouldSetValue() {

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        redisService.set(
                "url:3",
                "https://www.google.com"
        );

        verify(valueOperations)
                .set(
                        "url:3",
                        "https://www.google.com"
                );
    }

    @Test
    void shouldGetValue() {

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get("url:3"))
                .thenReturn("https://www.google.com");

        String result = redisService.get("url:3");

        assertEquals(
                "https://www.google.com",
                result
        );
    }

    @Test
    void shouldDeleteValue() {

        redisService.delete("url:3");

        verify(redisTemplate)
                .delete("url:3");
    }
    @Test
    void shouldUseCorrectUrlKey() {

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        redisService.getUrl("8");

        verify(valueOperations)
                .get("url:8");
    }
}