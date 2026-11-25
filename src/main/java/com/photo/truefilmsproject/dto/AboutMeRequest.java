package com.photo.truefilmsproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AboutMeRequest(
        String heroImageUrl,
        @NotBlank(message = "El título es obligatorio") String title,
        String subtitle,
        String description,
        @Size(min = 4, max = 4, message = "Deben proporcionarse exactamente 4 imágenes de galería")
        List<String> galleryImages
) {}