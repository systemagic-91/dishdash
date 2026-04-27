package com.dishdash.inventory.application.dto;

import com.dishdash.inventory.domain.model.InventoryItem;

public record InventoryResponse(

    String id,
    String productId,
    String productName,
    int availableQuantity
) {

  public static InventoryResponse from(InventoryItem item) {

    return new InventoryResponse(
        item.getId(),
        item.getProductId().value(),
        item.getProductName(),
        item.getAvailableQuantity()
    );
  }
}
