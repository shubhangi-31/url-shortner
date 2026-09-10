package com.example.urlshortner.dto;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CreateUrlRequest (
    @NotBlank(message = "Url cannot be empty")
    @Pattern(
            regexp = "^(https?://).+",
            message = "Url must start with http:// or https://"
            )
    @Schema(
            example = "https://www.youtube.com"
    )
    String originalUrl
//    LocalDateTime expiresAt
){}
