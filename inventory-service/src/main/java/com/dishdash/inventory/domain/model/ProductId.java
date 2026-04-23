package com.dishdash.inventory.domain.model;


import java.util.Objects;

// Value Object -> Garante que um ProductId nunca seja nulo ou vazio

public record ProductId(String value) {

  public ProductId {

    Objects.requireNonNull(value, "ProductId não pode ser nulo.");

    if (value.isBlank()) {
      throw new IllegalArgumentException("ProductId não pode ser vazio.");
    }
  }
}
