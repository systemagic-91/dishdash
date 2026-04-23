package com.dishdash.order.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Order")
class OrderTest {

  private final CustomerId customerId = new CustomerId("1");
  private final OrderItem validItem = OrderItem.of(
      "prod-1", "Notebook", 1, new BigDecimal("35000.00")
  );

  @Nested
  @DisplayName("create")
  class Create {

    @Test
    @DisplayName("should create order with PENDING status when items are valid")
    void shouldCreateOrderWithPendingStatusWhenItemnsAreValid() {

      List<OrderItem> items = List.of(validItem);

      Order order = Order.create(customerId, items);

      assertThat(order.getId()).isNotNull();
      assertThat(order.getCustomerId()).isEqualTo(customerId);
      assertThat(order.getItems()).hasSize(items.size());
      assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
      assertThat(order.getCreatedAt()).isNotNull();
      assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should calculate total amount correctly")
    void shouldCalculateTotalAmountCorrectly() {

      OrderItem item1 = OrderItem.of("prod-1", "Notebook", 2, new BigDecimal("3500.00"));
      OrderItem item2 = OrderItem.of("prod-2", "Mouse", 3, new BigDecimal("150.00"));

      Order order = Order.create(customerId, List.of(item1, item2));

      assertThat(order.totalAmount())
          .isEqualByComparingTo(new BigDecimal("7450.00"));
    }

    @Test
    @DisplayName("should throw exception when items list is empty")
    void shouldThrowExceptionWhenItemsListIsEmpty() {

      assertThatThrownBy(() -> Order.create(customerId, List.of()))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("pelo menos um item");
    }

    @Test
    @DisplayName("should throw exception when items list is null")
    void shouldThrowExceptionWhenItemsListIsNull() {
      assertThatThrownBy(() -> Order.create(customerId, null))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should generate unique id for each order")
    void shouldGenerateUniqueIdForEachOrder() {

      Order order1 = Order.create(customerId, List.of(validItem));
      Order order2 = Order.create(customerId, List.of(validItem));

      assertThat(order1.getId()).isNotEqualTo(order2.getId());
    }

    @Test
    @DisplayName("should make items list immutable after creation")
    void shouldMakeItemsListImmutableAfterCreation() {

      Order order = Order.create(customerId, List.of(validItem));

      assertThatThrownBy(() -> order.getItems().add(validItem))
          .isInstanceOf(UnsupportedOperationException.class);
    }
  }

  @Nested
  @DisplayName("cancel")
  class Cancel {

    @Test
    @DisplayName("should cancel order when status is PENDING")
    void shouldCancelOrderWhenStatusIsPending() {

      Order order = Order.create(customerId, List.of(validItem));

      assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);

      order.cancel();

      assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
      assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should throw exception when cancelling a PAID order")
    void shoudThrowExceptionWhenCancellingOrder() {

      Order order = Order.reconstitute("order-id", customerId, List.of(validItem),
          LocalDateTime.now(), LocalDateTime.now(), OrderStatus.PAID);

      assertThatThrownBy(order::cancel)
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Pedido já pago não pode ser cancelado");
    }

    @Test
    @DisplayName("should throw exception when cancelling an already CANCELLED order")
    void shoudThrowExceptionWhenCancellingAnAlreadyCancelledOrder() {

      Order order = Order.create(customerId, List.of(validItem));

      order.cancel();

      assertThatThrownBy(order::cancel)
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Pedido já está cancelado");
    }
  }

  @Nested
  @DisplayName("confirm")
  class Confirm {

    @Test
    @DisplayName("should confirm order when status is PENDING")
    void shouldConfirmOrderWhenStatusIsPending() {

      Order order = Order.create(customerId, List.of(validItem));

      order.confirm();

      assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("should throw exception when confirming a non-PENDING order")
    void shouldThrowExceptionWhenConfirmingAnNonPendingOrder() {

      Order order = Order.create(customerId, List.of(validItem));

      order.cancel();

      assertThatThrownBy(order::confirm)
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Apenas pedidos PENDING podem ser confirmados");
    }
  }
}