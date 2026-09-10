package com.example.urlshortner.util;

public class RedisKeyUtil {
    private RedisKeyUtil(){

    }
    public static String urlKey(String shortCode){
        return "url:" + shortCode;
    }
}
