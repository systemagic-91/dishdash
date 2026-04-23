package com.dishdash.inventory.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReserveStockRequest(

    @NotBlank(message = "productId é obrigatorio")
    String productId,

    @Min(value = 1, message = "Quantidade minima é 1")
    int quantity
) {

}
