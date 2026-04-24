package com.dishdash.order.application.usecase;


import com.dishdash.order.application.dto.OrderResponse;
import com.dishdash.order.domain.exception.OrderNotFoundException;
import com.dishdash.order.domain.model.CustomerId;
import com.dishdash.order.domain.model.Order;
import com.dishdash.order.domain.model.OrderItem;
import com.dishdash.order.domain.model.OrderStatus;
import com.dishdash.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelOrderUseCase")
class CancelOrderUseCaseTest {

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private CancelOrderUseCase cancelOrderUseCase;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("should cancel order when order is PENDING")
    void shouldCancelOrderWhenOrderIsPending() {

      Order pendingOrder = buildOrderHelper("order-123", OrderStatus.PENDING);

      when(orderRepository.findById("order-123"))
          .thenReturn(Mono.just(pendingOrder));
      when(orderRepository.save(any(Order.class)))
          .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

      StepVerifier.create(cancelOrderUseCase.execute("order-123"))
          .expectNextMatches(Execute::isCancelled)
          .verifyComplete();

      verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("should throw OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {

      when(orderRepository.findById("order-999"))
          .thenReturn(Mono.empty());

      StepVerifier.create(cancelOrderUseCase.execute("order-999"))
          .expectError(OrderNotFoundException.class)
          .verify();

      verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw IllegalStateException when order is already PAID")
    void shouldThrowIllegalStateExceptionWhenOrderIsAlreadyPaid() {

      Order paidOrder = buildOrderHelper("order-123", OrderStatus.PAID);

      when(orderRepository.findById("order-123"))
          .thenReturn(Mono.just(paidOrder));

      StepVerifier.create(cancelOrderUseCase.execute("order-123"))
          .expectError(IllegalStateException.class)
          .verify();

      verify(orderRepository, never()).save(any());
    }

    private static boolean isCancelled(OrderResponse response) {
      return response.status().equals("CANCELLED");
    }
  }

  private Order buildOrderHelper(String id, OrderStatus status) {

    return Order.reconstitute(
        id,
        new CustomerId("customer-123"),
        List.of(OrderItem.of("prod-1", "Notebook", 1, new BigDecimal("3500.00"))),
        LocalDateTime.now(),
        LocalDateTime.now(),
        status
    );
  }
}