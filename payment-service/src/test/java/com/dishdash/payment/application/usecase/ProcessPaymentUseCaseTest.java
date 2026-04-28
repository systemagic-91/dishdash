package com.dishdash.payment.application.usecase;

import static com.dishdash.payment.domain.model.PaymentStatus.APPROVED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dishdash.payment.application.dto.ProcessPaymentRequest;
import com.dishdash.payment.domain.model.Payment;
import com.dishdash.payment.domain.model.PaymentStatus;
import com.dishdash.payment.domain.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessPaymentUseCase")
class ProcessPaymentUseCaseTest {

  @Mock
  private PaymentRepository paymentRepository;

  @InjectMocks
  private ProcessPaymentUseCase processPaymentUseCase;


  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("should approve payment when amount is less than 10000")
    void shouldApprovePaymentWhenAmountIsLessThan10000() {

      when(paymentRepository.findByOrderId(anyString()))
          .thenReturn(Mono.empty());

      when(paymentRepository.save(any(Payment.class)))
          .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
      // thenAnswer com invocation.getArgument(0) retorna o próprio objeto
      // passado como argumento — simula o comportamento real do banco

      ProcessPaymentRequest request = new ProcessPaymentRequest(
          "order-123", "customer-123", new BigDecimal("3500.00")
      );

      StepVerifier.create(processPaymentUseCase.execute(request))
          .expectNextMatches(response ->
                response.status().equals(APPROVED.name()) &&
                response.orderId().equals("order-123") &&
                response.rejectionReason() == null
          )
          .verifyComplete();

      verify(paymentRepository, times(1)).findByOrderId(anyString());
      verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("should reject payment when amount is grater than 10000")
    void shouldRejectPaymentWhenAmountIsGraterThan10000() {

      String rejectReason = "Valor acima do limite permitido";

      when(paymentRepository.findByOrderId(anyString()))
          .thenReturn(Mono.empty());

      when(paymentRepository.save(any(Payment.class)))
          .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

      ProcessPaymentRequest request = new ProcessPaymentRequest(
          "order-123", "customer-123", new BigDecimal("13500.00")
      );

      StepVerifier.create(processPaymentUseCase.execute(request))
          .expectNextMatches(response ->
              response.status().equals(PaymentStatus.REJECTED.name()) &&
              response.orderId().equals("order-123") &&
              response.rejectionReason().equals(rejectReason)
          )
          .verifyComplete();

      verify(paymentRepository, times(1)).findByOrderId(anyString());
      verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("should approve payment when amount is exactly at the limit")
    void shouldApprovePaymentWhenAmountIsExactlyAtTheLimit() {

      when(paymentRepository.findByOrderId("order-123"))
          .thenReturn(Mono.empty());

      when(paymentRepository.save(any(Payment.class)))
          .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

      ProcessPaymentRequest request = new ProcessPaymentRequest(
          "order-123", "customer-123", new BigDecimal("10000.00")
      );

      StepVerifier.create(processPaymentUseCase.execute(request))
          .expectNextMatches(response ->
              response.status().equals("APPROVED"))
          .verifyComplete();

      verify(paymentRepository, times(1)).findByOrderId("order-123");
      verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("should return existing payment when order id is already created")
    void shouldReturnExistingPaymentWhenOrderIdIsAlreadyCreated() {

      when(paymentRepository.findByOrderId(anyString()))
          .thenReturn(Mono.just(buildExistingPayment("order-123", APPROVED)));

      ProcessPaymentRequest request = new ProcessPaymentRequest(
          "order-123", "customer-123", new BigDecimal("13500.00")
      );

      StepVerifier.create(processPaymentUseCase.execute(request))
          .expectNextMatches(response ->
              response.status().equals(APPROVED.name()) &&
              response.orderId().equals("order-123") &&
              response.id().equals("payment-123") &&
              response.rejectionReason() == null
          )
          .verifyComplete();

      verify(paymentRepository, times(1)).findByOrderId(anyString());
      verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("should return existing rejected payment without reprocessing")
    void shouldReturnExistingRejectedPaymentWithoutReprocessing() {

      Payment rejectedPayment = buildExistingPayment("order-123", PaymentStatus.REJECTED);

      when(paymentRepository.findByOrderId("order-123"))
          .thenReturn(Mono.just(rejectedPayment));

      ProcessPaymentRequest request = new ProcessPaymentRequest(
          "order-123", "customer-123", new BigDecimal("3500.00")
      );

      StepVerifier.create(processPaymentUseCase.execute(request))
          .expectNextMatches(response ->
              response.status().equals("REJECTED"))
          .verifyComplete();

      verify(paymentRepository, times(1)).findByOrderId(anyString());
      verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("should propagate exception when repository fails on save")
    void shouldPropagateExceptionWhenRepositoryFailsOnSave() {

      when(paymentRepository.findByOrderId("order-123"))
          .thenReturn(Mono.empty());

      when(paymentRepository.save(any(Payment.class)))
          .thenReturn(Mono.error(new RuntimeException("MongoDB indisponível")));

      ProcessPaymentRequest request = new ProcessPaymentRequest(
          "order-123", "customer-123", new BigDecimal("3500.00")
      );

      StepVerifier.create(processPaymentUseCase.execute(request))
          .expectErrorMatches(error ->
              error instanceof RuntimeException &&
              error.getMessage().equals("MongoDB indisponível"))
          .verify();

      verify(paymentRepository, times(1)).findByOrderId(anyString());
      verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("should propagate exception when repository fails on findByOrderId")
    void shouldPropagateExceptionWhenRepositoryFailsOnFindByOrderId() {

      when(paymentRepository.findByOrderId("order-123"))
          .thenReturn(Mono.error(new RuntimeException("Timeout no MongoDB")));

      ProcessPaymentRequest request = new ProcessPaymentRequest(
          "order-123", "customer-123", new BigDecimal("3500.00")
      );

      StepVerifier.create(processPaymentUseCase.execute(request))
          .expectError(RuntimeException.class)
          .verify();

      verify(paymentRepository, times(1)).findByOrderId(anyString());
      verify(paymentRepository, never()).save(any());
    }
  }

  private Payment buildExistingPayment(String orderId, PaymentStatus status) {

    return Payment.reconstitute(
        "payment-123", orderId, "customer-123",
        new BigDecimal("3500.00"), LocalDateTime.now(),
        LocalDateTime.now(), status, null);
  }
}