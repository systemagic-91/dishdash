package com.dishdash.order.application.usecase;

import static org.mockito.Mockito.when;

import com.dishdash.order.application.dto.OrderResponse;
import com.dishdash.order.domain.exception.OrderNotFoundException;
import com.dishdash.order.domain.model.CustomerId;
import com.dishdash.order.domain.model.Order;
import com.dishdash.order.domain.model.OrderItem;
import com.dishdash.order.domain.model.OrderStatus;
import com.dishdash.order.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetOrderUseCase")
class GetOrderUseCaseTest {

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private GetOrderUseCase getOrderUseCase;

  @Nested
  @DisplayName("findById")
  class FindById {

    @Test
    @DisplayName("should return order response when order exists")
    void shouldReturnOrderResponseWhenOrderExists() {

      Order order = buildOrderHelper("order-123");

      when(orderRepository.findById("order-123"))
          .thenReturn(Mono.just(order));

      StepVerifier.create(getOrderUseCase.findById("order-123"))
          .expectNextMatches(FindById::orderExists)
          .verifyComplete();
    }

    @Test
    @DisplayName("should throw OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {

      when(orderRepository.findById("order-999"))
          .thenReturn(Mono.empty());

      StepVerifier.create(getOrderUseCase.findById("order-999"))
          .expectError(OrderNotFoundException.class)
          .verify();
    }

    private static boolean orderExists(OrderResponse response) {

      return response.id().equals("order-123") &&
          response.customerId().equals("customer-123");
    }
  }

  @Nested
  @DisplayName("findAll")
  class FindAll {

    @Test
    @DisplayName("should return all orders as flux")
    void shouldReturnAllOrdersAsFlux() {

      Order order1 = buildOrderHelper("order-1");
      Order order2 = buildOrderHelper("order-2");

      when(orderRepository.findAll())
          .thenReturn(Flux.just(order1, order2));

      StepVerifier.create(getOrderUseCase.findAll())
          .expectNextMatches(r -> r.id().equals("order-1"))
          .expectNextMatches(r -> r.id().equals("order-2"))
          .verifyComplete();
    }

    @Test
    @DisplayName("should return empty flux when no orders exist")
    void shouldReturnEmptyFluxWhenNoOrdersExist() {

      when(orderRepository.findAll())
          .thenReturn(Flux.empty());

      StepVerifier.create(getOrderUseCase.findAll())
          .verifyComplete(); // completa sem emitir nenhum elemento
    }
  }

  private Order buildOrderHelper(String id) {

    return Order.reconstitute(
        id,
        new CustomerId("customer-123"),
        List.of(OrderItem.of("prod-1", "Notebook", 1, new BigDecimal("3500.00"))),
        LocalDateTime.now(),
        LocalDateTime.now(),
        OrderStatus.PENDING
    );
  }
}