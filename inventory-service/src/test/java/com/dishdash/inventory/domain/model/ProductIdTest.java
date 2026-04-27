package com.dishdash.inventory.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ProductId")
class ProductIdTest {

  @Test
  @DisplayName("should create ProductId when value is valid")
  void shouldCreateProductIdWhenValueIsValid() {

    ProductId productId = new ProductId("prod-123");

    assertThat(productId.value()).isEqualTo("prod-123");
  }

  @Test
  @DisplayName("should throw exception when value is null")
  void shouldThrowExceptionWhenValueIsNull() {

    assertThatThrownBy(() -> new ProductId(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("should throw exception when value is blank")
  void shouldThrowExceptionWhenValueIsBlank() {

    assertThatThrownBy(() -> new ProductId("  "))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("should be equal when values are the same")
  void shouldBeEqualWhenValuesAreTheSame() {

    ProductId id1 = new ProductId("prod-123");
    ProductId id2 = new ProductId("prod-123");

    assertThat(id1).isEqualTo(id2);
    assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
  }
}