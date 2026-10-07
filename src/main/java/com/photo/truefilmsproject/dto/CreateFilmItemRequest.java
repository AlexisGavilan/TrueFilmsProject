package com.photo.truefilmsproject.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFilmItemRequest(
        @NotBlank(message = "El título es obligatorio") String title,
        String synopsis,
        @NotBlank(message = "La URL del vídeo es obligatoria") String videoUrl,
        String thumbnailUrl,
        Integer releaseYear,
        Integer durationMinutes
) {}