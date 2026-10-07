package com.photo.truefilmsproject.dto;

import com.photo.truefilmsproject.entity.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePortfolioItemRequest(
        @NotBlank(message = "El título es obligatorio") String title,
        String description,
        @NotBlank(message = "La URL del recurso es obligatoria") String mediaUrl,
        @NotNull(message = "El tipo de medio (IMAGE/VIDEO) es obligatorio") MediaType mediaType,
        Integer displayOrder
) {}