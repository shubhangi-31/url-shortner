package com.example.urlshortner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ShortUrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleShortUrlNotFound(ShortUrlNotFoundException shortUrlNotFoundException){
        return shortUrlNotFoundException.getMessage();
    }
}
