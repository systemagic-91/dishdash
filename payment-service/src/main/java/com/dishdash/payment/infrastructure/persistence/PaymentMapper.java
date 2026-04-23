package com.dishdash.payment.infrastructure.persistence;

import com.dishdash.payment.domain.model.Payment;
import com.dishdash.payment.domain.model.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

  public PaymentDocument toDocument(Payment payment) {

    return PaymentDocument.builder()
        .id(payment.getId())
        .orderId(payment.getOrderId())
        .customerId(payment.getCustomerId())
        .amount(payment.getAmount())
        .status(payment.getStatus().name())
        .rejectionReason(payment.getRejectionReason())
        .createdAt(payment.getCreatedAt())
        .updatedAt(payment.getUpdatedAt())
        .build();
  }

  public Payment toDomain(PaymentDocument document) {

    return Payment.reconstitute(
        document.getId(),
        document.getOrderId(),
        document.getCustomerId(),
        document.getAmount(),
        document.getCreatedAt(),
        document.getUpdatedAt(),
        PaymentStatus.valueOf(document.getStatus()),
        document.getRejectionReason()
    );
  }
}
