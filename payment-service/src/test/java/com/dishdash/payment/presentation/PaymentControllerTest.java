package com.dishdash.payment.presentation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.dishdash.payment.application.dto.PaymentResponse;
import com.dishdash.payment.application.dto.ProcessPaymentRequest;
import com.dishdash.payment.application.usecase.GetPaymentUseCase;
import com.dishdash.payment.application.usecase.ProcessPaymentUseCase;
import com.dishdash.payment.domain.exception.PaymentNotFoundException;
import com.dishdash.payment.domain.model.PaymentStatus;
import com.dishdash.payment.infrastructure.persistence.SpringDataPaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(PaymentController.class)
@DisplayName("PaymentController")
class PaymentControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private ProcessPaymentUseCase processPaymentUseCase;

  @MockitoBean
  private GetPaymentUseCase getPaymentUseCase;

  @MockitoBean
  private SpringDataPaymentRepository springDataPaymentRepository;

  private PaymentResponse paymentResponse;

  @BeforeEach
  void setUp() {

    paymentResponse = new PaymentResponse(
        UUID.randomUUID().toString(),
        "order-123",
        "customer-123",
        new BigDecimal("3500.00"),
        PaymentStatus.APPROVED.name(),
        null,
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }

  @Nested
  @DisplayName("POST /api/v1/payments")
  class PostPaymentRequest {

    @Test
    @DisplayName("should return 201 when payment is processed")
    void shouldReturn201WhenPaymentIsProcessed() {

      when(processPaymentUseCase.execute(any(ProcessPaymentRequest.class)))
          .thenReturn(Mono.just(paymentResponse));

      webTestClient.post()
          .uri("/api/v1/payments")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(new ProcessPaymentRequest("order-123", "customer-123", new BigDecimal("3500.00")))
          .exchange()
          .expectStatus()
          .isCreated()
          .expectBody()
          .jsonPath("$.id").isNotEmpty()
          .jsonPath("$.orderId").isEqualTo("order-123");
    }

    @Test
    @DisplayName("should return 404 when orderId is blank")
    void shouldReturn404WhenOrderIdIsBlank() {

      when(processPaymentUseCase.execute(any(ProcessPaymentRequest.class)))
          .thenReturn(Mono.just(paymentResponse));

      webTestClient.post()
          .uri("/api/v1/payments")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(new ProcessPaymentRequest("", "customer-123", new BigDecimal("3500.00")))
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectBody()
          .jsonPath("$.fields").isEqualTo("orderId: orderId é obrigatório");
    }

    @Test
    @DisplayName("should return 404 when amount is null")
    void shouldReturn404WhenAmountIsNull() {

      when(processPaymentUseCase.execute(any(ProcessPaymentRequest.class)))
          .thenReturn(Mono.just(paymentResponse));

      webTestClient.post()
          .uri("/api/v1/payments")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(new ProcessPaymentRequest("order-123", "customer-123", null))
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectBody()
          .jsonPath("$.fields").isEqualTo("amount: amount é obrigatório");
    }
  }

  @Nested
  @DisplayName("GET /api/v1/payments/{id}")
  class GetPaymentResponse {

    @Test
    @DisplayName("should return 200 when payment already exist")
    void shouldReturn2xxWhenPaymentAlreadyExist() {

      when(getPaymentUseCase.findById(anyString()))
          .thenReturn(Mono.just(paymentResponse));

      webTestClient.get()
          .uri("/api/v1/payments/" + paymentResponse.id())
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.id").isEqualTo(paymentResponse.id());
    }

    @Test
    @DisplayName("should return 404 when payment not found")
    void shouldReturn404WhenPaymentNotFound() {

      when(getPaymentUseCase.findById(anyString()))
          .thenReturn(Mono.error(new PaymentNotFoundException("payment-123")));

      webTestClient.get()
          .uri("/api/v1/payments/" + paymentResponse.id())
          .exchange()
          .expectStatus()
          .isNotFound()
          .expectBody()
          .jsonPath("$.message").isEqualTo("Pagamento não encontrado: payment-123");
    }
  }

  @Nested
  @DisplayName("GET /api/v1/payments/order/{orderId}")
  class GetPaymentOrderResponse {

    @Test
    @DisplayName("should return 200 when payment of order exist")
    void shouldReturn200WhenPaymentOfOrderExist() {

      when(getPaymentUseCase.findByOrderId(anyString()))
          .thenReturn(Mono.just(paymentResponse));

      webTestClient.get()
          .uri("/api/v1/payments/order/" + paymentResponse.id())
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.id").isEqualTo(paymentResponse.id());
    }

    @Test
    @DisplayName("should return 404 when payment of order not found")
    void shouldReturn200WhenPaymentOfOrderNotFound() {

      when(getPaymentUseCase.findByOrderId(anyString()))
          .thenReturn(Mono.error(new PaymentNotFoundException("payment-123")));

      webTestClient.get()
          .uri("/api/v1/payments/order/" + paymentResponse.id())
          .exchange()
          .expectStatus()
          .isNotFound()
          .expectBody()
          .jsonPath("$.message").isEqualTo("Pagamento não encontrado: payment-123");
    }
  }
}