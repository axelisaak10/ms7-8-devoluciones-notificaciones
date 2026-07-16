package com.scrip.mscatalogo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        @NotBlank String titulo,
        @NotBlank String isbn,
        @NotBlank String autor,
        @NotBlank String categoria,
        @NotNull @Min(0) Integer stock
) {
}
