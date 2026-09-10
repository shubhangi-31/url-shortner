package com.example.urlshortner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
@Getter
@Setter
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="short_code",unique = true)
    private String shortCode;

    @Column(name="original_url",unique = true,length = 500)
    private String originalUrl;

    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt;

//    @Column(name = "expires_at")
//    private LocalDateTime expiresAt;
//
//    @Column(name = "click_count",nullable = false)
//    private long clickCount=0L;

}
