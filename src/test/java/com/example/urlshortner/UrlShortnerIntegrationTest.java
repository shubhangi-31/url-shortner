package com.example.urlshortner;

import com.example.urlshortner.entity.Url;
import com.example.urlshortner.repository.UrlRepository;
import com.example.urlshortner.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UrlShortnerIntegrationTest {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private RedisService redisService;

    @Test
    void shouldSaveAndRetrieveUrlFromDatabase() {

        String originalUrl =
                "https://integration-test-" + System.currentTimeMillis() + ".example.com";

        Url url = new Url();

        url.setOriginalUrl(originalUrl);
        url.setShortCode("integration" + System.currentTimeMillis());
        url.setCreatedAt(LocalDateTime.now());

        Url savedUrl = urlRepository.save(url);

        assertNotNull(savedUrl.getId());

        Url retrievedUrl = urlRepository
                .findByShortCode(url.getShortCode())
                .orElseThrow();

        assertEquals(
                originalUrl,
                retrievedUrl.getOriginalUrl()
        );
    }

    @Test
    void shouldStoreAndRetrieveUrlFromRedis() {

        redisService.cacheUrl(
                "integration2",
                "https://redis-test.example.com"
        );

        String result = redisService.getUrl("integration2");

        assertEquals(
                "https://redis-test.example.com",
                result
        );
    }
}