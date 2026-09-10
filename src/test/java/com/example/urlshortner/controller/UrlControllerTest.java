package com.example.urlshortner.controller;

import com.example.urlshortner.entity.Url;
import com.example.urlshortner.exception.ShortUrlNotFoundException;
import com.example.urlshortner.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlControllerTest {

    private UrlService urlService;
    private UrlController urlController;

    @BeforeEach
    void setUp() {
        urlService = mock(UrlService.class);
        urlController = new UrlController(urlService);
    }

    @Test
    void shouldCreateShortUrl() {

        Url url = new Url();

        url.setId(100L);
        url.setShortCode("1C");
        url.setOriginalUrl("https://www.google.com");
        url.setCreatedAt(LocalDateTime.now());

        when(urlService.createShortUrl("https://www.google.com"))
                .thenReturn(url);

        // We'll test the controller directly through the request DTO
        var request = new com.example.urlshortner.dto.CreateUrlRequest(
                "https://www.google.com"
        );

        ResponseEntity<com.example.urlshortner.dto.UrlResponse> response =
                urlController.createShortUrl(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(
                "1C",
                response.getBody().shortCode()
        );

        assertEquals(
                "https://www.google.com",
                response.getBody().originalUrl()
        );

        verify(urlService)
                .createShortUrl("https://www.google.com");
    }

    @Test
    void shouldRedirectToOriginalUrl() {

        Url url = new Url();

        url.setShortCode("1C");
        url.setOriginalUrl("https://www.google.com");

        when(urlService.getUrlByShortCode("1C"))
                .thenReturn(url);

        ResponseEntity<Void> response =
                urlController.redirectToOriginalUrl("1C");

        assertEquals(
                302,
                response.getStatusCode().value()
        );

        assertEquals(
                "https://www.google.com",
                response.getHeaders().getFirst("Location")
        );

        verify(urlService)
                .getUrlByShortCode("1C");
    }

    @Test
    void shouldReturn404WhenShortCodeDoesNotExist() {

        when(urlService.getUrlByShortCode("invalid"))
                .thenThrow(
                        new ShortUrlNotFoundException(
                                "Short URL not found"
                        )
                );

        assertThrows(
                ShortUrlNotFoundException.class,
                () -> urlController.redirectToOriginalUrl("invalid")
        );

        verify(urlService)
                .getUrlByShortCode("invalid");
    }
}