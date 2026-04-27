package com.dishdash.inventory.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dishdash.inventory.application.dto.AddStockRequest;
import com.dishdash.inventory.application.dto.InventoryResponse;
import com.dishdash.inventory.application.dto.ReserveStockRequest;
import com.dishdash.inventory.application.usecase.AddStockUseCase;
import com.dishdash.inventory.application.usecase.ReserveStockUseCase;
import com.dishdash.inventory.domain.exception.InsufficientStockException;
import com.dishdash.inventory.domain.exception.ProductNotFoundException;
import com.dishdash.inventory.infrastructure.persistence.SpringDataInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(InventoryController.class)
@DisplayName("InventoryController")
class InventoryControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private AddStockUseCase addStockUseCase;

  @MockitoBean
  private ReserveStockUseCase reserveStockUseCase;

  @MockitoBean
  private SpringDataInventoryRepository springDataInventoryRepository;

  private InventoryResponse inventoryResponse;

  @BeforeEach
  void setUp() {

    inventoryResponse = new InventoryResponse(
        "item-123",
        "prod-1",
        "Notebook",
        10
    );
  }

  @Nested
  @DisplayName("POST /api/v1/inventory")
  class AddStock {

    @Test
    @DisplayName("should return 201 and inventory response when request is valid")
    void shouldReturn201AndInventoryResponseWhenRequestIsValid() {

      when(addStockUseCase.execute(any(AddStockRequest.class)))
          .thenReturn(Mono.just(inventoryResponse));

      webTestClient.post()
          .uri("/api/v1/inventory")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(new AddStockRequest("prod-1", "Notebook", 10))
          .exchange()
          .expectStatus().isCreated()
          .expectBody()
          .jsonPath("$.productId").isEqualTo("prod-1")
          .jsonPath("$.availableQuantity").isEqualTo(10);
    }

    @Test
    @DisplayName("should return 400 when productId is blank")
    void shouldReturn400WhenProductIdIsBlank() {

      webTestClient.post()
          .uri("/api/v1/inventory")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(new AddStockRequest("", "Notebook", 10))
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectBody()
          .jsonPath("$.fields").isEqualTo("productId: productId é obrigatório");
    }
  }

  @Nested
  @DisplayName("POST /api/v1/inventory/reserve")
  class ReserveStock {

    @Test
    @DisplayName("should return 200 and updated inventory when stock is sufficient")
    void shouldReturn200AndUpdatedInventoryWhenStockIsSufficient() {

      InventoryResponse reservedResponse = new InventoryResponse(
          "item-123", "prod-1", "Notebook", 7
      );

      when(reserveStockUseCase.execute(any(ReserveStockRequest.class)))
          .thenReturn(Mono.just(reservedResponse));

      webTestClient.post()
          .uri("/api/v1/inventory/reserve")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(new ReserveStockRequest("prod-1", 3))
          .exchange()
          .expectStatus().isOk()
          .expectBody()
          .jsonPath("$.availableQuantity").isEqualTo(7);
    }

    @Test
    @DisplayName("should return 404 when product does not exist")
    void shouldReturn404WhenProductDoesNotExist() {

      when(reserveStockUseCase.execute(any(ReserveStockRequest.class)))
          .thenReturn(Mono.error(new ProductNotFoundException("prod-999")));

      webTestClient.post()
          .uri("/api/v1/inventory/reserve")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(new ReserveStockRequest("prod-999", 1))
          .exchange()
          .expectStatus()
          .isNotFound()
          .expectBody()
          .jsonPath("$.message").isEqualTo("Produto não encontrado no estoque: prod-999");
    }

    @Test
    @DisplayName("should return 422 when stock is insufficient")
    void shouldReturn422WhenStockIsInsufficient() {

      when(reserveStockUseCase.execute(any(ReserveStockRequest.class)))
          .thenReturn(Mono.error(
              new InsufficientStockException("prod-1", 5, 2)));

      webTestClient.post()
          .uri("/api/v1/inventory/reserve")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(new ReserveStockRequest("prod-1", 5))
          .exchange()
          .expectStatus().isEqualTo(422)
          .expectBody()
          .jsonPath("$.message").isEqualTo("Estoque insuficiente para produto "
              + "prod-1. Solicitado: 5, Disponível: 2");
    }

    @Test
    @DisplayName("should return 400 when quantity is zero")
    void shouldReturn400WhenQuantityIsZero() {

      webTestClient.post()
          .uri("/api/v1/inventory/reserve")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(new ReserveStockRequest("prod-1", 0))
          .exchange()
          .expectStatus().isBadRequest()
          .expectBody()
          .jsonPath("$.fields").isEqualTo("quantity: Quantidade minima é 1");
    }
  }
}