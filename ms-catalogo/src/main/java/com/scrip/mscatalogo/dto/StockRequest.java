package com.scrip.mscatalogo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockRequest(
        @NotNull @Min(1) Integer cantidad
) {
}
