package com.dishdash.payment.application.dto;

import com.dishdash.payment.domain.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

    String id,
    String orderId,
    String customerId,
    BigDecimal amount,
    String status,
    String rejectionReason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {

  public static PaymentResponse from(Payment payment) {

    return new PaymentResponse(
        payment.getId(),
        payment.getOrderId(),
        payment.getCustomerId(),
        payment.getAmount(),
        payment.getStatus().name(),
        payment.getRejectionReason(),
        payment.getCreatedAt(),
        payment.getUpdatedAt()
    );
  }
}
