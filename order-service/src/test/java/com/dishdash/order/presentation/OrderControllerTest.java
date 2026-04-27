package com.dishdash.order.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dishdash.order.application.dto.CreateOrderRequest;
import com.dishdash.order.application.dto.CreateOrderRequest.OrderItemRequest;
import com.dishdash.order.application.dto.OrderResponse;
import com.dishdash.order.application.dto.OrderResponse.OrderItemResponse;
import com.dishdash.order.application.usecase.CancelOrderUseCase;
import com.dishdash.order.application.usecase.CreateOrderUseCase;
import com.dishdash.order.application.usecase.GetOrderUseCase;
import com.dishdash.order.domain.exception.OrderNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


// @WebFluxTest carrega só o controller especificado
// Sem isso ele tentaria carregar todos os controllers do projeto
@WebFluxTest(controllers = OrderController.class)
@DisplayName("OrderController")
class OrderControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  // @MockitoBean registra o mock no contexto Spring
  // O controller recebe esse mock via injeção de dependência
  @MockitoBean
  private CreateOrderUseCase createOrderUseCase;

  @MockitoBean
  private GetOrderUseCase getOrderUseCase;

  @MockitoBean
  private CancelOrderUseCase cancelOrderUseCase;

  private OrderResponse orderResponse;
  private CreateOrderRequest createOrderRequest;

  @BeforeEach
  void setUp() {

    var orderItemResponse = new OrderItemResponse("prod-1", "Notebook", 1,
        new BigDecimal("3500.00"), new BigDecimal("3500.00"));

    orderResponse = new OrderResponse("order-123", "customer-123",
        List.of(orderItemResponse), "PENDING",  new BigDecimal("3500.00"),
        LocalDateTime.now(), LocalDateTime.now());

    var orderItemRequest = new OrderItemRequest("prod-1", "Notebook",
        1, new BigDecimal("3500.00"));

    createOrderRequest = new CreateOrderRequest("customer-123", List.of(orderItemRequest));
  }

  @Nested
  @DisplayName("POST /api/v1/orders")
  class CreateOrder {

    @Test
    @DisplayName("should return 201 and order response when request is valid")
    void shouldReturn201AndOrderResponseWhenRequestIsValid() {

      when(createOrderUseCase.execute(any(CreateOrderRequest.class)))
          .thenReturn(Mono.just(orderResponse));

      webTestClient.post()
          .uri("/api/v1/orders")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(createOrderRequest)
          .exchange()
          .expectStatus()
          .isCreated()
          .expectBody()
          .jsonPath("$.id").isEqualTo("order-123")
          .jsonPath("$.customerId").isEqualTo("customer-123")
          .jsonPath("$.status").isEqualTo("PENDING")
          .jsonPath("$.totalAmount").isEqualTo(3500.00);
    }

    @Test
    @DisplayName("shoud return 400 when customer Id is blank")
    void shouldReturn400WhenCustomerIdIsBlank() {

      var orderItemRequest = new OrderItemRequest(
          "prod-1", "Notebook", 1 , new BigDecimal("3500.00"));

      CreateOrderRequest invalidRequest = new CreateOrderRequest("", List.of(orderItemRequest));

      webTestClient.post()
          .uri("/api/v1/orders")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidRequest)
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectBody()
          .jsonPath("$.fields").isEqualTo("customerId: customerId é obrigatorio");
    }

    @Test
    @DisplayName("should return 400 when items list is empty")
    void shouldReturn400WhenItemsListIsEmpty() {

      CreateOrderRequest invalidRequest = new CreateOrderRequest("customer--123", List.of());

      webTestClient.post()
          .uri("/api/v1/orders")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidRequest)
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectBody()
          .jsonPath("$.fields").isEqualTo("items: Pedido deve ter pelo menos um item");
    }
  }

  @Nested
  @DisplayName("GET /api/v1/orders/{id}")
  class FindById {

    @Test
    @DisplayName("should return 200 and order when order exists")
    void shouldReturn200AndOrderWhenOrderExists() {

      when(getOrderUseCase.findById("order-123"))
          .thenReturn(Mono.just(orderResponse));

      webTestClient.get()
          .uri("/api/v1/orders/order-123")
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.id").isEqualTo("order-123")
          .jsonPath("$.status").isEqualTo("PENDING");
    }

    @Test
    @DisplayName("should return 404 when order does not exist")
    void shouldReturn404WhenOrderDoesNotExist() {

      when(getOrderUseCase.findById("order-123"))
          .thenReturn(Mono.error(new OrderNotFoundException("order-123")));

      webTestClient.get()
          .uri("/api/v1/orders/order-123")
          .exchange()
          .expectStatus()
          .isNotFound()
          .expectBody()
          .jsonPath("$.message").isEqualTo("Pedido não encontrado order-123");
    }
  }

  @Nested
  @DisplayName("GET /api/v1/orders")
  class FindAll {

    @Test
    @DisplayName("should return 200 and list of orders")
    void shouldReturn200AndListOfOrders() {

      OrderResponse response = new OrderResponse(
          "order-456", "customer-456",
          List.of(), "PENDING",
          BigDecimal.ZERO, LocalDateTime.now(),
          LocalDateTime.now()
      );

      when(getOrderUseCase.findAll())
          .thenReturn(Flux.just(orderResponse, response));

      webTestClient.get()
          .uri("/api/v1/orders")
          .exchange()
          .expectStatus()
          .isOk()
          .expectBodyList(OrderResponse.class)
          .hasSize(2);
    }

    @Test
    @DisplayName("should return 200 and empty list when no orders exist")
    void shouldReturn200AndEmptyListWhenNoOrdersExist() {

      when(getOrderUseCase.findAll()).thenReturn(Flux.empty());

      webTestClient.get()
          .uri("/api/v1/orders")
          .exchange()
          .expectStatus()
          .isOk()
          .expectBodyList(OrderResponse.class)
          .hasSize(0);
    }
  }

  @Nested
  @DisplayName("PATCH /api/v1/orders/{id}/cancel")
  class CancelOrder {

    @Test
    @DisplayName("should return 200 and cancelled order when order exists")
    void shouldReturn200AndCancelOrderWhenOrderExists() {

      OrderResponse response = new OrderResponse(
          "order-123", "customer-123",
          List.of(), "CANCELLED",
          new BigDecimal("3500.00"),
          LocalDateTime.now(), LocalDateTime.now()
      );

      when(cancelOrderUseCase.execute("order-123"))
          .thenReturn(Mono.just(response));

      webTestClient.patch()
          .uri("/api/v1/orders/order-123/cancel")
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.status").isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("should return 404 when order does not exist")
    void shouldReturn404WhenOrderDoesNotExist() {

      when(cancelOrderUseCase.execute("order-123"))
          .thenReturn(Mono.error(new OrderNotFoundException("order-123")));

      webTestClient.patch()
          .uri("/api/v1/orders/order-123/cancel")
          .exchange()
          .expectStatus()
          .isNotFound()
          .expectBody()
          .jsonPath("$.message").isEqualTo("Pedido não encontrado order-123");
    }

    @Test
    @DisplayName("should return 422 when order cannot be cancelled")
    void shouldReturn422WhenOrderCannotBeCancelled() {

      when(cancelOrderUseCase.execute("order-123"))
          .thenReturn(Mono.error(new IllegalStateException("Pedido já pago não pode ser cancelado")));


      webTestClient.patch()
          .uri("/api/v1/orders/order-123/cancel")
          .exchange()
          .expectStatus()
          .is4xxClientError()
          .expectBody()
          .jsonPath("$.message").isEqualTo("Pedido já pago não pode ser cancelado");
    }
  }
}