package com.dishdash.inventory.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddStockRequest(

    @NotBlank(message = "productId é obrigatório")
    String productId,

    @NotBlank(message = "productName é obrigatório")
    String productName,

    @Min(value = 0, message = "Quantidade não pode ser negativa")
    int quantity
) {

}
