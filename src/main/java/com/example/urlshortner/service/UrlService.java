package com.example.urlshortner.service;

import com.example.urlshortner.entity.Url;
import com.example.urlshortner.exception.ShortUrlNotFoundException;
import com.example.urlshortner.repository.UrlRepository;
import com.example.urlshortner.util.Base62Encoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final RedisService redisService;

    public UrlService(UrlRepository urlRepository, RedisService redisService){
        this.urlRepository=urlRepository;
        this.redisService = redisService;
    }

    public Url createShortUrl(String originalUrl){

        //Existing hai toh puraana wala otherwise
        Url existingUrl = urlRepository
                .findByOriginalUrl(originalUrl)
                .orElse(null);

        if(existingUrl != null) {
                return existingUrl;
        }

        Url url=new Url();
        url.setOriginalUrl(originalUrl);
        url.setCreatedAt(LocalDateTime.now());
//        url.setClickCount(0L);
//        url.setExpiresAt(expiresAt);

        // Pehle Save karenge to generate Id
        urlRepository.save(url);

        // Id jo aayega saved entry ka usko encode krenge aur short code me convert karenge
        String shortCode = Base62Encoder.encode(url.getId());
        url.setShortCode(shortCode);

        // Save again with the generated short code
        return urlRepository.save(url);
    }
//    public Url getUrlByShortCode(String shortCode) {
//        return urlRepository.findByShortCode(shortCode)
//                .orElseThrow(() ->
//                        new ShortUrlNotFoundException("Short URL not found"));
//    }

    public Url getUrlByShortCode(String shortCode) {

        // 1. Check Redis first
        String originalUrl = redisService.getUrl(shortCode);
        if (originalUrl != null) {
            Url url = new Url();
            url.setShortCode(shortCode);
            url.setOriginalUrl(originalUrl);
            return url;
        }
        // 2. Redis miss → check MySQL
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException("Short URL not found"));
        // 3. Store the result in Redis
        redisService.cacheUrl(
                url.getShortCode(),
                url.getOriginalUrl()
        );
        return url;
    }

    public Url saveUrl(Url url){

        return urlRepository.save(url);
    }
}



//createShortUrl()
//        ↓
//Create and save a short URL
//
//
//getUrlByShortCode()
//        ↓
//Find an existing short URL
//        ↓
//Return the Url entity