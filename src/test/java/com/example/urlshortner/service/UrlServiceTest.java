package com.example.urlshortner.service;

import com.example.urlshortner.entity.Url;
import com.example.urlshortner.exception.ShortUrlNotFoundException;
import com.example.urlshortner.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisService redisService;

    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlRepository = mock(UrlRepository.class);
        redisService = mock(RedisService.class);

        urlService = new UrlService(
                urlRepository,
                redisService
        );
    }

    @Test
    void shouldCreateShortUrl() {

        Url savedUrl = new Url();
        savedUrl.setOriginalUrl("https://www.google.com");
        savedUrl.setCreatedAt(LocalDateTime.now());

        when(urlRepository.findByOriginalUrl("https://www.google.com"))
                .thenReturn(Optional.empty());

        when(urlRepository.save(any(Url.class)))
                .thenAnswer(invocation -> {
                    Url url = invocation.getArgument(0);

                    if (url.getId() == null) {
                        url.setId(100L);
                    }

                    return url;
                });

        Url result = urlService.createShortUrl(
                "https://www.google.com"
        );

        assertEquals(
                "https://www.google.com",
                result.getOriginalUrl()
        );

        assertEquals(
                "1C",
                result.getShortCode()
        );

        verify(urlRepository, times(2))
                .save(any(Url.class));
    }

    @Test
    void shouldReturnExistingShortUrl() {

        Url existingUrl = new Url();

        existingUrl.setId(100L);
        existingUrl.setShortCode("1C");
        existingUrl.setOriginalUrl("https://www.google.com");
        existingUrl.setCreatedAt(LocalDateTime.now());

        when(urlRepository.findByOriginalUrl("https://www.google.com"))
                .thenReturn(Optional.of(existingUrl));

        Url result = urlService.createShortUrl(
                "https://www.google.com"
        );

        assertEquals(
                "1C",
                result.getShortCode()
        );

        assertEquals(
                "https://www.google.com",
                result.getOriginalUrl()
        );

        verify(urlRepository, never())
                .save(any(Url.class));
    }
    @Test
    void shouldReturnUrlFromRedisCache() {

        when(redisService.getUrl("8"))
                .thenReturn("https://www.youtube.com");

        Url result = urlService.getUrlByShortCode("8");

        assertEquals(
                "8",
                result.getShortCode()
        );

        assertEquals(
                "https://www.youtube.com",
                result.getOriginalUrl()
        );

        verify(redisService)
                .getUrl("8");

        verify(urlRepository, never())
                .findByShortCode("8");
    }
    @Test
    void shouldFetchFromDatabaseWhenRedisCacheMisses() {

        Url url = new Url();

        url.setId(8L);
        url.setShortCode("8");
        url.setOriginalUrl("https://www.youtube.com");
        url.setCreatedAt(LocalDateTime.now());

        when(redisService.getUrl("8"))
                .thenReturn(null);

        when(urlRepository.findByShortCode("8"))
                .thenReturn(Optional.of(url));

        Url result = urlService.getUrlByShortCode("8");

        assertEquals(
                "8",
                result.getShortCode()
        );

        assertEquals(
                "https://www.youtube.com",
                result.getOriginalUrl()
        );

        verify(redisService)
                .getUrl("8");

        verify(urlRepository)
                .findByShortCode("8");

        verify(redisService)
                .cacheUrl(
                        "8",
                        "https://www.youtube.com"
                );
    }
    @Test
    void shouldThrowExceptionWhenUrlDoesNotExist() {

        when(redisService.getUrl("999"))
                .thenReturn(null);

        when(urlRepository.findByShortCode("999"))
                .thenReturn(Optional.empty());

        assertThrows(
                ShortUrlNotFoundException.class,
                () -> urlService.getUrlByShortCode("999")
        );

        verify(redisService)
                .getUrl("999");

        verify(urlRepository)
                .findByShortCode("999");

        verify(redisService, never())
                .cacheUrl(anyString(), anyString());
    }
}