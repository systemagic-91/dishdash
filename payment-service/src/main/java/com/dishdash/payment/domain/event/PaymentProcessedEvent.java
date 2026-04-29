package com.dishdash.payment.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentProcessedEvent(

    String eventId,
    String paymentId,
    String orderId,
    String customerId,
    BigDecimal amount,
    String status,          // "APPROVED" ou "REJECTED"
    String rejectionReason, // nulo quando aprovado
    LocalDateTime occurredAt
) {}