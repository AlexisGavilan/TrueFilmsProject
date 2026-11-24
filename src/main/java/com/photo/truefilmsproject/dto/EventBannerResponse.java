package com.photo.truefilmsproject.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventBannerResponse(
        Long id,
        String title,
        String description,
        LocalDate eventDate,
        String bannerImageUrl,
        LocalDateTime expiresAt
) {}
