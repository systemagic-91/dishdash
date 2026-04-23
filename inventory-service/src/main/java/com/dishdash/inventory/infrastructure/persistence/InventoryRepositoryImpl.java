package com.dishdash.inventory.infrastructure.persistence;

import com.dishdash.inventory.domain.model.InventoryItem;
import com.dishdash.inventory.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

  private final SpringDataInventoryRepository repository;
  private final InventoryMapper mapper;

  @Override
  public Mono<InventoryItem> save(InventoryItem item) {

    return repository.save(mapper.toDocument(item))
        .map(mapper::toDomain);
  }

  @Override
  public Mono<InventoryItem> findById(String id) {

    return repository.findById(id)
        .map(mapper::toDomain);
  }

  @Override
  public Mono<InventoryItem> findByProductId(String productId) {

    return repository.findByProductId(productId)
        .map(mapper::toDomain);
  }

  @Override
  public Flux<InventoryItem> findAll() {

    return repository.findAll()
        .map(mapper::toDomain);
  }
}
