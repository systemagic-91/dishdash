package com.dishdash.inventory.domain.model;

import com.dishdash.inventory.domain.exception.InsufficientStockException;
import java.util.UUID;
import lombok.Getter;

@Getter
public class InventoryItem {

  private final String id;
  private final ProductId productId;
  private final String productName;
  private int availableQuantity;

  // construtor privado para a propria classe controlar como esse objeto pode ser criado
  private InventoryItem(String id, ProductId productId, String productName, int availableQuantity) {

    this.id = id;
    this.productId = productId;
    this.productName = productName;
    this.availableQuantity = availableQuantity;
  }

  public static InventoryItem create(ProductId productId, String productName, int initialQuantity) {

    if (initialQuantity <= 0) {
      throw new IllegalArgumentException("Quantidade inicial não pode ser negativa.");
    }

    return new InventoryItem(
        UUID.randomUUID().toString(),
        productId,
        productName,
        initialQuantity
    );
  }

  public static InventoryItem reconstitute(String id, ProductId productId, String productName,
      int availableQuantity) {

    return new InventoryItem(id, productId, productName, availableQuantity);
  }

  // Regra de negócio: reservar estoque
  // Por que lança exceção e não retorna boolean?
  // Porque "não ter estoque suficiente" é uma violação de regra de negócio,
  // não um fluxo normal. Exceções comunicam isso claramente.
  public void reserve(int quantity) {

    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantidade a reservar deve ser maior que zero.");
    }

    if (this.availableQuantity < quantity) {
      throw new InsufficientStockException(productId.value(), quantity, this.availableQuantity);
    }

    this.availableQuantity -= quantity;
  }

  // Regra para devolver estoque quando pedido é cancelado
  public void release(int quantity) {

    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantidade a liberar deve ser maior que zero.");
    }

    this.availableQuantity += quantity;
  }

}
