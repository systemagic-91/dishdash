package com.dishdash.inventory.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dishdash.inventory.application.dto.ReserveStockRequest;
import com.dishdash.inventory.domain.exception.InsufficientStockException;
import com.dishdash.inventory.domain.exception.ProductNotFoundException;
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
@DisplayName("ReserveStockUseCase")
class ReserveStockUseCaseTest {

  @Mock
  private InventoryRepository inventoryRepository;

  @InjectMocks
  private ReserveStockUseCase reserveStockUseCase;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("should reserve stock and return updated item when stock is sufficient")
    void shouldReserveStockAndReturnUpdatedItemWhenStockIsSufficient() {

      InventoryItem item = buildItem(10);

      when(inventoryRepository.findByProductId("prod-1"))
          .thenReturn(Mono.just(item));

      when(inventoryRepository.save(any(InventoryItem.class)))
          .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

      ReserveStockRequest request = new ReserveStockRequest("prod-1", 3);


      StepVerifier.create(reserveStockUseCase.execute(request))
          .expectNextMatches(response ->
              response.availableQuantity() == 7 &&
                  response.productId().equals("prod-1")
          )
          .verifyComplete();

      verify(inventoryRepository, times(1)).save(any(InventoryItem.class));
    }

    @Test
    @DisplayName("should throw ProductNotFoundException when product does not exist")
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {

      when(inventoryRepository.findByProductId("prod-999"))
          .thenReturn(Mono.empty());

      ReserveStockRequest request = new ReserveStockRequest("prod-999", 1);

      StepVerifier.create(reserveStockUseCase.execute(request))
          .expectError(ProductNotFoundException.class)
          .verify();

      verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw InsufficientStockException when stock is not enough")
    void shouldThrowInsufficientStockExceptionWhenStockIsNotEnough() {

      InventoryItem item = buildItem(2);

      when(inventoryRepository.findByProductId("prod-1"))
          .thenReturn(Mono.just(item));

      ReserveStockRequest request = new ReserveStockRequest("prod-1", 5);

      StepVerifier.create(reserveStockUseCase.execute(request))
          .expectError(InsufficientStockException.class)
          .verify();

      verify(inventoryRepository, never()).save(any());
    }
  }

  private InventoryItem buildItem(int quantity) {

    return InventoryItem.reconstitute(
        "item-123",
        new ProductId("prod-1"),
        "Notebook",
        quantity
    );
  }
}