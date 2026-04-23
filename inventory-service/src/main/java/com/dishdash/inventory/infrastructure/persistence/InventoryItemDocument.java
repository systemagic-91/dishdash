package com.dishdash.inventory.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventory")
public class InventoryItemDocument {

  @Id
  private String id;

  // @Indexed garante indice no Mongo para buscar por product id
  // sem indice o mongo faz full collection scan (lento em prod)
  @Indexed(unique = true)
  private String productId;

  private String productName;

  private int availableQuantity;
}
