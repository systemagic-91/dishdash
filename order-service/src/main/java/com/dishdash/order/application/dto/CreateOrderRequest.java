package com.dishdash.order.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(

    @NotBlank(message = "customerId é obrigatorio")
    String customerId,

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    @Valid
    List<OrderItemRequest> items
) {

  // esse record é aninhado por que só faz sentido a existencia dele
  // dentro de create order request e isso deixa esse relacionamento claro
  public record OrderItemRequest(

      @NotBlank(message = "productId é obrigatório")
      String productId,

      @NotBlank(message = "productName é obrigatório")
      String productName,

      @Min(value = 1, message = "Quantidade minima é 1")
      int quantity,

      @NotNull(message = "unitPrice é obrigatório")
      @DecimalMin(value = "0.01", message = "Preco minimo é 0.01")
      BigDecimal unitPrice
  ) {}
}
