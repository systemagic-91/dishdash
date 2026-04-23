package com.dishdash.inventory.domain.exception;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(String productId) {

    super("Produto não encontrado no estoque: " + productId);
  }
}
