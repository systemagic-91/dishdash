package com.dishdash.order.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItem {

  private final String productId;
  private final String productName;
  private final int quantity;
  private final BigDecimal unitPrice;

  // regra de negocio dentro das enditades em que a regra pertence
  public BigDecimal totalPrice() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
  }

  // validacao de invariantes -> sao regras que SEMPRE devem ser verdadeiras para o objeto existir
  // nao deixa criar um item invalido
  // o Metodo of funciona como um construtor controlado para criar um objeto q sempre seja valido!
  public static OrderItem of(String productId, String productName, int quantity, BigDecimal unitPrice) {

    Objects.requireNonNull(productId, "productId obrigatório");
    Objects.requireNonNull(unitPrice, "unitPrice obrigatório");

    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity deve ser maior que zero");
    }

    if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Preço deve ser maior que zero");
    }

    return OrderItem.builder()
        .productId(productId)
        .productName(productName)
        .quantity(quantity)
        .unitPrice(unitPrice)
        .build();
  }

}
