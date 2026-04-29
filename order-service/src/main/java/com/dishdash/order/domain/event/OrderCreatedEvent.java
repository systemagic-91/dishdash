package com.dishdash.order.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Evento imutavel
public record OrderCreatedEvent(

    String eventId, // id unico do evento
    String orderId,
    String customerId,
    List<OrderItemEvent> items,
    BigDecimal totalAmount,
    LocalDateTime occurredAt // qnd o evento aconteceu
) {

  public record OrderItemEvent(
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice
  ) {}
}
