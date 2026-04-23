package com.dishdash.payment.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProcessPaymentRequest(

    @NotBlank(message = "orderId é obrigatório")
    String orderId,

    @NotBlank(message = "customerId é obrigatório")
    String customerId,

    @NotNull(message = "amount é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor mínimo é 0.01")
    BigDecimal amount
) {

}
