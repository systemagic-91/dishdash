package com.dishdash.inventory.application.usecase;

import com.dishdash.inventory.application.dto.AddStockRequest;
import com.dishdash.inventory.application.dto.InventoryResponse;
import com.dishdash.inventory.domain.model.InventoryItem;
import com.dishdash.inventory.domain.model.ProductId;
import com.dishdash.inventory.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AddStockUseCase {

  private final InventoryRepository inventoryRepository;

  public Mono<InventoryResponse> execute(AddStockRequest request) {

    return inventoryRepository.findByProductId(request.productId())
        .flatMap(item -> {
          // produto ja existe no banco, so adicionamos quantidade
          item.release(request.quantity());
          return inventoryRepository.save(item);
        })
        .switchIfEmpty(
            // produto nao existe -> criamos um novo
            // Mono.defer serve pra adiar a execução até o momento certo.
            // Para criacao/chamada em banco/logica que NAO deve rodar sempre -> usar Mono.defer
            Mono.defer(() -> {

              InventoryItem newItem = InventoryItem.create(
                  new ProductId(request.productId()),
                  request.productName(),
                  request.quantity()
              );

              return inventoryRepository.save(newItem);
            })
        )
        .map(InventoryResponse::from);
  }
}
