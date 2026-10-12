package com.photo.truefilmsproject.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccessCodeRequest(
        @NotBlank(message = "El nuevo código no puede estar vacío") String newAccessCode
) {}
