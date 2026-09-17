package com.photo.truefilmsproject.dto;

import java.time.LocalDate;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDate eventDate,
        boolean hasZip
) {}