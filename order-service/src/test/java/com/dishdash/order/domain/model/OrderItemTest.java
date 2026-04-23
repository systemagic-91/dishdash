package com.dishdash.order.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("OrderItem")
class OrderItemTest {

  @Nested
  @DisplayName("of")
  class of {

    @Test
    @DisplayName("should create item when all fields are valid")
    void shouldCreateItemWhenAllFieldsAreValid() {

      OrderItem orderItem = OrderItem.of("prod-1", "Notebook", 2,
          new BigDecimal("3500.00"));

      assertThat(orderItem.getProductId()).isEqualTo("prod-1");
      assertThat(orderItem.getProductName()).isEqualTo("Notebook");
      assertThat(orderItem.getQuantity()).isEqualTo(2);
      assertThat(orderItem.getUnitPrice()).isEqualByComparingTo(new BigDecimal("3500.00"));
    }

    @Test
    @DisplayName("should calculate total price correctly")
    void shouldCalculateTotalPriceCorrectly() {

      OrderItem orderItem = OrderItem.of("prod-1", "Notebook", 2,
          new BigDecimal("3500.00"));

      assertThat(orderItem.totalPrice()).isEqualByComparingTo(new BigDecimal("7000.00"));
    }

    @Test
    @DisplayName("should throw exception when quantity is zero")
    void shouldThrowExceptionWhenQuantityIsZero() {

      assertThatThrownBy(() -> OrderItem.of("prod-1", "Notebook", 0,
          new BigDecimal("3500.00")))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Quantity deve ser maior que zero");
    }

    @Test
    @DisplayName("should throw exception when quantity is negative")
    void shouldThrowExceptionWhenQuantityIsNegative() {

      assertThatThrownBy(() -> OrderItem.of("prod-1", "Notebook", -1,
          new BigDecimal("100.00")))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Quantity deve ser maior que zero");
    }

    @Test
    @DisplayName("should throw exception when unit price is zero")
    void shouldThrowExceptionWhenUnitPriceIsZero() {

      assertThatThrownBy(() -> OrderItem.of("prod-1", "Notebook", 1,
          BigDecimal.ZERO))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Preço deve ser maior que zero");
    }

    @Test
    @DisplayName("should throw exception when productId is null")
    void shouldThrowExceptionWhenProductIdIsNull() {

      assertThatThrownBy(() -> OrderItem.of(null, "Notebook", 1,
          new BigDecimal("100.00")))
          .isInstanceOf(NullPointerException.class);
    }
  }






}