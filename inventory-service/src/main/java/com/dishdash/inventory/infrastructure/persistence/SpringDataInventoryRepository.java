package com.dishdash.inventory.infrastructure.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SpringDataInventoryRepository
    extends ReactiveMongoRepository<InventoryItemDocument, String> {

  Mono<InventoryItemDocument> findByProductId(String productId);
}
