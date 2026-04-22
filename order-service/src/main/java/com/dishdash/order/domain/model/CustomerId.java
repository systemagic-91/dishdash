package com.dishdash.order.domain.model;


import java.util.Objects;

// Value Object: identidade baseada no VALOR, não na referencia
// Dois CustomerId com o mesmo valor sao iguais -
// Por que record e não uma classe normal?
// Records do Java 21 são perfeitos para Value Objects — imutáveis por padrão,
// equals/hashCode/toString gerados automaticamente.
// Um VO deve ser imutável, e o record garante isso.

public record CustomerId(String value) {

  public CustomerId {

    Objects.requireNonNull(value, "Customer id não pode ser nulo.");

    if (value.isBlank()) {
      throw new IllegalArgumentException("CustomerId não pode ser vazio.");
    }
  }
}
