package com.dishdash.payment.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Payment")
class PaymentTest {

  @Nested
  @DisplayName("create")
  class Create {

    @Test
    @DisplayName("should create payment with PENDING status")
    void shouldCreatePaymentWithPendingStatus() {

      Payment payment = Payment.create(
          "order-123", "customer-123",
          new BigDecimal("3500.00")
      );

      assertThat(payment.getId()).isNotNull();
      assertThat(payment.getOrderId()).isEqualTo("order-123");
      assertThat(payment.getCustomerId()).isEqualTo("customer-123");
      assertThat(payment.getAmount()).isEqualTo("3500.00");
      assertThat(payment.getStatus().name()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("should throw exception when amount is zero")
    void shouldThrowExceptionWhenAmountIsZero() {

      assertThatThrownBy(() -> Payment
          .create("order-123", "customer-123", BigDecimal.ZERO))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Valor do pagamento deve ser maior que zero");
    }

    @Test
    @DisplayName("should throw exception when amount is negative")
    void shouldThrowExceptionWhenAmountIsNegative() {

      assertThatThrownBy(() -> Payment
          .create("order-123", "customer-123", new BigDecimal("-3500.00")))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Valor do pagamento deve ser maior que zero");
    }

    @Test
    @DisplayName("should generate unique ids for each payment")
    void shouldGenerateUniqueIdsForEachPayment() {

      Payment payment1 = Payment.create(
          "order-123", "customer-123",
          new BigDecimal("3500.00")
      );

      Payment payment2 = Payment.create(
          "order-123", "customer-123",
          new BigDecimal("3500.00")
      );

      assertThat(payment1.getId()).isNotEqualTo(payment2.getId());
    }
  }

  @Nested
  @DisplayName("approve")
  class Approve {

    @Test
    @DisplayName("should approve payments when status is PENDING")
    void shouldApprovePaymentsWhenStatusIsPending() {

      Payment payment = Payment.create(
          "order-123", "customer-123",
          new BigDecimal("3500.00")
      );

      payment.approve();

      assertThat(payment.getStatus().name()).isEqualTo("APPROVED");
    }

    @Test
    @DisplayName("should throw exception when status isn't PENDING")
    void shouldThrowExceptionWhenStatusIsNotPending() {

      Payment payment = Payment.create(
          "order-123", "customer-123",
          new BigDecimal("3500.00")
      );

      payment.approve();

      assertThatThrownBy(payment::approve)
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Apenas pagamentos pendentes podem ser aprovados. Status atual:APPROVED");
    }
  }

  @Nested
  @DisplayName("reject")
  class Reject {

    @Test
    @DisplayName("should reject payments with reason when status is PENDING")
    void shouldRejectPaymentsWithReasonWhenStatusIsPending() {

      Payment payment = Payment.create(
          "order-123", "customer-123",
          new BigDecimal("3500.00")
      );

      payment.reject("Pagamento cancelado");

      assertThat(payment.getStatus().name()).isEqualTo("REJECTED");
      assertThat(payment.getRejectionReason()).isEqualTo("Pagamento cancelado");
    }

    @Test
    @DisplayName("should throw exception when status isn't PENDING")
    void shouldThrowExceptionWhenStatusIsNotPending() {

      Payment payment = Payment.create(
          "order-123", "customer-123",
          new BigDecimal("3500.00")
      );

      payment.approve();

      assertThatThrownBy(() -> payment.reject("Pagamento cancelado"))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Apenas pagamentos pendentes podem ser rejeitados. Status atual:APPROVED");
    }
  }

  @Nested
  @DisplayName("refund")
  class Refund {

    @Test
    @DisplayName("should refund payment when status is APPROVED")
    void shouldRefundPaymentWhenStatusIsAPPROVED() {

      Payment payment = Payment.create(
          "order-123", "customer-123",
          new BigDecimal("3500.00")
      );

      payment.approve();
      payment.refund();

      assertThat(payment.getStatus().name()).isEqualTo("REFUNDED");
    }

    @Test
    @DisplayName("should throw exception when status isn't APPROVED")
    void shouldThrowExceptionWhenStatusIsNotAPPROVED() {

      Payment payment = Payment.create(
          "order-123", "customer-123",
          new BigDecimal("3500.00")
      );

      payment.reject("Pagamento cancelado");

      assertThatThrownBy(payment::refund)
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Apenas pagamentos aprovados podem ser estornados. Status atual:REJECTED");
    }
  }
}


















