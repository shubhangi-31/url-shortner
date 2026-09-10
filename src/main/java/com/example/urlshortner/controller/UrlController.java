package com.example.urlshortner.controller;

import com.example.urlshortner.dto.CreateUrlRequest;
import com.example.urlshortner.dto.UrlResponse;
import com.example.urlshortner.entity.Url;
import com.example.urlshortner.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(
            @Valid @RequestBody CreateUrlRequest request) {

        Url url = urlService.createShortUrl(
                request.originalUrl()
        );

        UrlResponse response = new UrlResponse(
                url.getShortCode(),
                "http://localhost:8080/api/urls/" + url.getShortCode(),
                url.getOriginalUrl(),
                url.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(
            @PathVariable String shortCode) {

        Url url = urlService.getUrlByShortCode(shortCode);

        return ResponseEntity
                .status(302)
                .header("Location", url.getOriginalUrl())
                .build();
    }
}