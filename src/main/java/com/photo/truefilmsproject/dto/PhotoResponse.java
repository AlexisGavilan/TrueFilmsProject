package com.photo.truefilmsproject.dto;

public record PhotoResponse(
        Long id,
        String fileName,
        String thumbnailUrl,
        String originalUrl,
        Long fileSize
) {}