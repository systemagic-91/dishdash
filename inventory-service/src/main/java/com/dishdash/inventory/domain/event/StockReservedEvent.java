package com.dishdash.inventory.domain.event;

import java.time.LocalDateTime;
import java.util.List;

public record StockReservedEvent(

    String eventId,
    String orderId, // id do pedido original
    String customerId,
    List<ReservedItemEvent> items,
    LocalDateTime occurredAt

) {
  public record ReservedItemEvent(
      String productId,
      int reservedQuantity
  ) {}
}
