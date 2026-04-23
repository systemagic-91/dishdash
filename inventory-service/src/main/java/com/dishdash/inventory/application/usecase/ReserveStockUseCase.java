package com.dishdash.inventory.application.usecase;

import com.dishdash.inventory.application.dto.InventoryResponse;
import com.dishdash.inventory.application.dto.ReserveStockRequest;
import com.dishdash.inventory.domain.exception.ProductNotFoundException;
import com.dishdash.inventory.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReserveStockUseCase {

  private final InventoryRepository inventoryRepository;

  public Mono<InventoryResponse> execute(ReserveStockRequest request) {

    return inventoryRepository.findByProductId(request.productId())
        .switchIfEmpty(Mono.error(new ProductNotFoundException(request.productId())))
        .flatMap(item -> {
          // lembrando que reserve() pode lancar exception
          // o webflux vai capturar e propagar como erro no stream reativo
          item.reserve(request.quantity());
          return inventoryRepository.save(item);
        })
        .map(InventoryResponse::from);
  }
}
