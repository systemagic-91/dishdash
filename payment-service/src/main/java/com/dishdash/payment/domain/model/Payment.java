package com.dishdash.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Payment {

  private final String id;
  private final String orderId;
  private final String customerId;
  private final BigDecimal amount;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private PaymentStatus status;
  private String rejectionReason;

  private Payment(String id, String orderId, String customerId, BigDecimal amount,
      LocalDateTime createdAt, LocalDateTime updatedAt, PaymentStatus status,
      String rejectionReason ) {

    this.id = id;
    this.orderId = orderId;
    this.customerId = customerId;
    this.amount = amount;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.status = status;
    this.rejectionReason = rejectionReason;
  }

  public static Payment create(String orderId, String customerId, BigDecimal amount) {

    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Valor do pagamento deve ser maior que zero");
    }

    return new Payment(
        UUID.randomUUID().toString(),
        orderId,
        customerId,
        amount,
        LocalDateTime.now(),
        LocalDateTime.now(),
        PaymentStatus.PENDING,
        null
    );
  }

  public static Payment reconstitute(String id, String orderId, String customerId, BigDecimal amount,
      LocalDateTime createdAt, LocalDateTime updatedAt, PaymentStatus status, String rejectionReason) {

    return new Payment(id,  orderId, customerId, amount, createdAt, updatedAt, status, rejectionReason);
  }

  // valida regras de negocio para aprovar pagamento
  public void approve() {

    if (status != PaymentStatus.PENDING) {
      throw new IllegalStateException("Apenas pagamentos pendentes podem ser aprovados. Status atual:" + this.status);
    }

    this.status = PaymentStatus.APPROVED;
    this.updatedAt = LocalDateTime.now();
  }

  // valida regras de negocio para rejeitar
  public void reject(String reason) {

    if (status != PaymentStatus.PENDING) {
      throw new IllegalStateException("Apenas pagamentos pendentes podem ser rejeitados. Status atual:" + this.status);
    }

    this.status = PaymentStatus.REJECTED;
    this.updatedAt = LocalDateTime.now();
    this.rejectionReason = reason;
  }

  public void refund() {

    if (status != PaymentStatus.APPROVED) {
      throw new IllegalStateException("Apenas pagamentos aprovados podem ser estornados. Status atual:" + this.status);
    }

    this.status = PaymentStatus.REFUNDED;
    this.updatedAt = LocalDateTime.now();
  }
}
