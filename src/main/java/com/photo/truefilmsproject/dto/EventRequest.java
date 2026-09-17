package com.photo.truefilmsproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EventRequest(
        @NotBlank(message = "El título es obligatorio") String title,
        String description,
        @NotNull(message = "La fecha es obligatoria") LocalDate eventDate
) {}