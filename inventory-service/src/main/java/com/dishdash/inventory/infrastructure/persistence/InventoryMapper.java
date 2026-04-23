package com.dishdash.inventory.infrastructure.persistence;

import com.dishdash.inventory.domain.model.InventoryItem;
import com.dishdash.inventory.domain.model.ProductId;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

  public InventoryItemDocument toDocument(InventoryItem item) {

    return InventoryItemDocument.builder()
        .id(item.getId())
        .productId(item.getProductId().value())
        .productName(item.getProductName())
        .availableQuantity(item.getAvailableQuantity())
        .build();
  }

  public InventoryItem toDomain(InventoryItemDocument document) {

    return InventoryItem.reconstitute(
        document.getId(),
        new ProductId(document.getProductId()),
        document.getProductName(),
        document.getAvailableQuantity()
    );
  }
}
