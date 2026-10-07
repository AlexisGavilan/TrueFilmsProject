package com.photo.truefilmsproject.dto;

import com.photo.truefilmsproject.entity.MediaType;

public record PortfolioItemResponse(
        Long id,
        String title,
        String description,
        String mediaUrl,
        MediaType mediaType,
        Integer displayOrder
) {}