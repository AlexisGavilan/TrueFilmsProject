package com.photo.truefilmsproject.dto;

import java.util.List;

public record AboutMeResponse(
        Long id,
        String heroImageUrl,
        String title,
        String subtitle,
        String description,
        List<String> galleryImages
) {}