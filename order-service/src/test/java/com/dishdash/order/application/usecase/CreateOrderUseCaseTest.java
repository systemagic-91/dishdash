package com.dishdash.order.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dishdash.order.application.dto.CreateOrderRequest;
import com.dishdash.order.application.dto.CreateOrderRequest.OrderItemRequest;
import com.dishdash.order.application.dto.OrderResponse;
import com.dishdash.order.domain.model.Order;
import com.dishdash.order.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderUseCase")
class CreateOrderUseCaseTest {

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private CreateOrderUseCase createOrderUseCase;

  private CreateOrderRequest request;

  @BeforeEach
  void setUp() {

    OrderItemRequest orderItem = new OrderItemRequest("prod-1", "Notebook",
        1, new BigDecimal("3500.00"));

    request = new CreateOrderRequest(
        "customer-123",
        List.of(orderItem)
    );
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("should create order and return response when request is valid")
    void shouldCreateOrderAndReturnResponseWhenRequestIsValid() {

      when(orderRepository.save(any(Order.class)))
          .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
      // thenAnswer com invocation.getArgument(0) retorna o próprio objeto
      // passado como argumento — simula o comportamento real do banco

      StepVerifier.create(createOrderUseCase.execute(request))
          .expectNextMatches(this::requestIsValid)
          .verifyComplete();

      verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("should propagate exception when repository fails")
    void shouldPropagateExceptionWhenRepositoryFails() {

      when(orderRepository.save(any(Order.class)))
          .thenReturn(Mono.error(new RuntimeException("MongoDB insisponível")));

      StepVerifier.create(createOrderUseCase.execute(request))
          .expectErrorMatches(this::isMongoDBAvailable)
          .verify();

      verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("should throw exception when items list is empty")
    void shouldThrowExceptionWhenItemsListIsEmpty() {

      CreateOrderRequest emptyItemsRequest = new CreateOrderRequest(
          "customer-123", List.of()
      );

      StepVerifier.create(createOrderUseCase.execute(emptyItemsRequest))
          .expectError(IllegalArgumentException.class)
          .verify();

      verify(orderRepository, never()).save(any());
    }

    private boolean requestIsValid(OrderResponse response) {

      return response.customerId().equals("customer-123") &&
          response.status().equals("PENDING") &&
          response.totalAmount().compareTo(new BigDecimal("3500.00")) == 0 &&
          response.id() != null;
    }

    private boolean isMongoDBAvailable(Throwable error) {

      return error instanceof RuntimeException && error.getMessage().equals("MongoDB insisponível");
    }
  }
}