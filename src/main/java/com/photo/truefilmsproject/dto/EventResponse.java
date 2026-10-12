package com.photo.truefilmsproject.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDate eventDate,
        String accessCode,
        boolean hasZip,
        LocalDateTime createdAt
) {}