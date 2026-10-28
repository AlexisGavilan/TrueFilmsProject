package com.photo.truefilmsproject.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record DownloadSelectionRequest(
        @NotEmpty(message = "Debes seleccionar al menos una foto para descargar")
        List<Long> photoIds
) {}