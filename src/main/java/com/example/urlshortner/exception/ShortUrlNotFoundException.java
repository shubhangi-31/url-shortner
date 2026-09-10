package com.example.urlshortner.exception;

public class ShortUrlNotFoundException extends RuntimeException{
    public ShortUrlNotFoundException(String message){
        super(message);
    }
}
