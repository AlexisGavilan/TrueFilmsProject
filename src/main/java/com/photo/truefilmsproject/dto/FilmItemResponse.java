package com.photo.truefilmsproject.dto;

public record FilmItemResponse(
        Long id,
        String title,
        String synopsis,
        String videoUrl,
        String thumbnailUrl,
        Integer releaseYear,
        Integer durationMinutes
) {}