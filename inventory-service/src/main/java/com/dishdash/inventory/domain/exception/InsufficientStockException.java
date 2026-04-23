package com.dishdash.inventory.domain.exception;

public class InsufficientStockException extends RuntimeException {

  public InsufficientStockException(String productId,
      int requested, int available) {

    super(String.format(
        "Estoque insuficiente para produto %s. Solicitado: %d, Disponível: %d",
        productId, requested, available
    ));
  }
}
