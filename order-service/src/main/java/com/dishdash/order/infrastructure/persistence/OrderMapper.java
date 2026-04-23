package com.dishdash.order.infrastructure.persistence;

import com.dishdash.order.domain.model.CustomerId;
import com.dishdash.order.domain.model.Order;
import com.dishdash.order.domain.model.OrderItem;
import com.dishdash.order.domain.model.OrderStatus;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

  // converte dominio para document para salvar no banco
  public OrderDocument toDocument(Order order) {

    return OrderDocument.builder()
        .id(order.getId())
        .customerId(order.getCustomerId().value())
        .items(order.getItems().stream().map(this::toItemDocument).toList())
        .status(order.getStatus().name())
        .createdAt(order.getCreatedAt())
        .updatedAt(order.getUpdatedAt())
        .build();
  }

  public Order toDomain(OrderDocument document) {

    List<OrderItem> items = document.getItems().stream()
        .map(this::toItemDomain)
        .toList();

    return Order.reconstitute(
        document.getId(),
        new CustomerId(document.getCustomerId()),
        items,
        document.getCreatedAt(),
        document.getUpdatedAt(),
        OrderStatus.valueOf(document.getStatus()));
  }

  private OrderItemDocument toItemDocument(OrderItem item) {

    return OrderItemDocument.builder()
        .productId(item.getProductId())
        .productName(item.getProductName())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .build();
  }

  private OrderItem toItemDomain(OrderItemDocument document) {

    return OrderItem.of(
        document.getProductId(),
        document.getProductName(),
        document.getQuantity(),
        document.getUnitPrice());
  }
}
