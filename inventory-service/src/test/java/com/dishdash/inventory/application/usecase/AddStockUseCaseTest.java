package com.dishdash.inventory.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dishdash.inventory.application.dto.AddStockRequest;
import com.dishdash.inventory.domain.model.InventoryItem;
import com.dishdash.inventory.domain.model.ProductId;
import com.dishdash.inventory.domain.repository.InventoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddStockUseCase")
class AddStockUseCaseTest {

  @Mock
  private InventoryRepository inventoryRepository;

  @InjectMocks
  private AddStockUseCase addStockUseCase;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("should create new item when product does not exist")
    void shouldCreateNewItemWhenProductDoesNotExist() {

      when(inventoryRepository.findByProductId("prod-1"))
          .thenReturn(Mono.empty());

      when(inventoryRepository.save(any(InventoryItem.class)))
          .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

      AddStockRequest request = new AddStockRequest("prod-1", "Notebook", 10);

      StepVerifier.create(addStockUseCase.execute(request))
          .expectNextMatches(response ->
              response.productId().equals("prod-1") &&
                  response.availableQuantity() == 10)
          .verifyComplete();
    }

    @Test
    @DisplayName("should add quantity to existing item when product already exists")
    void shouldAddQuantityToExistingItemWhenProductAlreadyExists() {

      InventoryItem existing = InventoryItem.reconstitute(
          "item-123",
          new ProductId("prod-1"),
          "Notebook",
          5
      );

      when(inventoryRepository.findByProductId("prod-1"))
          .thenReturn(Mono.just(existing));

      when(inventoryRepository.save(any(InventoryItem.class)))
          .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

      AddStockRequest request = new AddStockRequest("prod-1", "Notebook", 10);

      StepVerifier.create(addStockUseCase.execute(request))
          .expectNextMatches(response -> response.availableQuantity() == 15)
          .verifyComplete();
    }
  }
}