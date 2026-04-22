package com.dishdash.order.application.dto;

import com.dishdash.order.domain.model.Order;
import com.dishdash.order.domain.model.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    String id,
    String customerId,
    List<OrderItemResponse> items,
    String status,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

  // o dto sabe se construir a partir do domínio isso evita o Mapper
  // separado para casos simples
  public static OrderResponse from(Order order) {

    return new OrderResponse(
        order.getId(),
        order.getCustomerId().value(),
        order.getItems().stream().map(OrderItemResponse::from).toList(),
        order.getStatus().name(),
        order.totalAmount(),
        order.getCreatedAt(),
        order.getUpdatedAt()
    );
  }

  public record OrderItemResponse(
      String productId,
      String productName,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice
  ) {
    public static OrderItemResponse from(OrderItem item) {

      return new OrderItemResponse(
          item.getProductId(),
          item.getProductName(),
          item.getQuantity(),
          item.getUnitPrice(),
          item.totalPrice()
      );
    }
  }
}
