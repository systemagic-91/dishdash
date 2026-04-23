package com.dishdash.inventory.domain.repository;

import com.dishdash.inventory.domain.model.InventoryItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryRepository {

  Mono<InventoryItem> save(InventoryItem item);

  Mono<InventoryItem> findById(String id);

  // Busca por product id e nao pelo id interno
  // O order service conhece o product-id e nao o id interno do estoque
  Mono<InventoryItem> findByProductId(String productId);

  Flux<InventoryItem> findAll();
}
