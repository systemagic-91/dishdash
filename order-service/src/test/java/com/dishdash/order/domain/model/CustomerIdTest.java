package com.dishdash.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CustomerId")
class CustomerIdTest {

  @Test
  @DisplayName("should create CustomerId when value is valid")
  void shouldCreateCustomerIdWhenValueIsValid() {

    CustomerId customerId = new CustomerId("customer-123");

    assertThat(customerId.value()).isEqualTo("customer-123");
  }

  @Test
  @DisplayName("should throw exception when value is null")
  void shouldThrowExceptionWhenValueIsNull() {

    assertThatThrownBy(() -> new CustomerId(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Customer id não pode ser nulo.");
  }

  @Test
  @DisplayName("should throw exception when value is blank")
  void shouldThrowExceptionWhenValueIsBlank() {

    assertThatThrownBy(() -> new CustomerId("  "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("CustomerId não pode ser vazio.");
  }

  @Test
  @DisplayName("should be equal when values are the same")
  void shouldBeEqualWhenValuesAreTheSame() {

    // Records têm equals automático baseado no valor
    CustomerId id1 = new CustomerId("customer-123");
    CustomerId id2 = new CustomerId("customer-123");

    assertThat(id1).isEqualTo(id2);
    assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
  }
}