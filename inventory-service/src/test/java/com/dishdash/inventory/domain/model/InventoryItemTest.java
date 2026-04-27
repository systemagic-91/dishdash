package com.dishdash.inventory.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dishdash.inventory.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("InventoryItem")
class InventoryItemTest {

  private final ProductId productId = new ProductId("1");

  @Nested
  @DisplayName("create")
  class Reserve{

    @Test
    @DisplayName("should create an inventory item")
    void shouldCreateAnInventoryItem() {

      InventoryItem inventoryItem = InventoryItem.create(productId, "Notebook",
          10);

      assertThat(inventoryItem).isNotNull();
      assertThat(inventoryItem.getProductId()).isEqualTo(productId);
      assertThat(inventoryItem.getProductName()).isEqualTo("Notebook");
      assertThat(inventoryItem.getAvailableQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("should throw exception when initialQuantity is negative")
    void shouldThrowExceptionWhenItemsListIsEmpty() {

      assertThatThrownBy(() -> InventoryItem.create(productId, "Notebook",
          -1))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Quantidade inicial não pode ser negativa.");
    }

    @Test
    @DisplayName("should throw exception when initialQuantity is zero")
    void shouldThrowExceptionWhenItemsListIsZero() {

      assertThatThrownBy(() -> InventoryItem.create(productId, "Notebook",
          0))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Quantidade inicial não pode ser negativa.");
    }
  }

  @Nested
  @DisplayName("reserve")
  class ReserveItem {

    @Test
    @DisplayName("should reserve an item")
    void shouldReserveAnItem() {

      InventoryItem inventoryItem = InventoryItem.create(productId, "Notebook",
          10);

      inventoryItem.reserve(1);

      assertThat(inventoryItem.getProductId()).isEqualTo(productId);
      assertThat(inventoryItem.getProductName()).isEqualTo("Notebook");
      assertThat(inventoryItem.getAvailableQuantity()).isEqualTo(9);
    }

    @Test
    @DisplayName("should throw exception when quantity to reserve is negative")
    void shouldThrowExceptionWhenItemsListIsNegative() {

      InventoryItem inventoryItem = InventoryItem.create(productId, "Notebook",
          10);

      assertThatThrownBy(() -> inventoryItem.reserve(-1))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Quantidade a reservar deve ser maior que zero.");
    }

    @Test
    @DisplayName("should throw exception when quantity to reserve is ZERO")
    void shouldThrowExceptionWhenItemsListIsZero() {

      InventoryItem inventoryItem = InventoryItem.create(productId, "Notebook",
          10);

      assertThatThrownBy(() -> inventoryItem.reserve(0))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Quantidade a reservar deve ser maior que zero.");
    }

    @Test
    @DisplayName("should throw exception when quantity to reserve grater than available")
    void shouldThrowExceptionWhenItemsListIsGraterThanAvailable() {

      InventoryItem inventoryItem = InventoryItem.create(productId, "Notebook",
          10);

      assertThatThrownBy(() -> inventoryItem.reserve(11))
          .isInstanceOf(InsufficientStockException.class)
          .hasMessageContaining("Estoque insuficiente para produto 1. Solicitado: 11, Disponível: 10");
    }
  }

  @Nested
  @DisplayName("Release")
  class ReleaseItem {

    @Test
    @DisplayName("should release item ")
    void shouldReleaseItem() {

      InventoryItem inventoryItem = InventoryItem.create(productId, "Notebook",
          9);

      inventoryItem.release(1);


      assertThat(inventoryItem.getProductId()).isEqualTo(productId);
      assertThat(inventoryItem.getProductName()).isEqualTo("Notebook");
      assertThat(inventoryItem.getAvailableQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("should throw exception when quantity is negative")
    void shouldThrowExceptionWhenItemsListIsNegative() {

      InventoryItem inventoryItem = InventoryItem.create(productId, "Notebook",
          10);

      assertThatThrownBy(() -> inventoryItem.release(-1))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Quantidade a liberar deve ser maior que zero.");
    }

    @Test
    @DisplayName("should throw exception when quantity is zero")
    void shouldThrowExceptionWhenItemsListIsZero() {

      InventoryItem inventoryItem = InventoryItem.create(productId, "Notebook",
          10);

      assertThatThrownBy(() -> inventoryItem.release(0))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Quantidade a liberar deve ser maior que zero.");
    }
  }
}