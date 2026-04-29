package com.dishdash.inventory.infrastructure.messaging;

import com.dishdash.inventory.application.dto.ReserveStockRequest;
import com.dishdash.inventory.application.usecase.ReserveStockUseCase;
import com.dishdash.inventory.domain.exception.InsufficientStockException;
import com.dishdash.inventory.domain.exception.ProductNotFoundException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

  private final ReserveStockUseCase reserveStockUseCase;
  private final StockEventPublisher stockEventPublisher;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "order-created",
      groupId = "inventory-group"
  )
  public void consume(String message) {

    log.info("OrderCreated recebida: {}", message);

    try {

      // o inventory service nao conhece as classes do order-service (Map)
      Map<String, Object> event = objectMapper.readValue(message, Map.class);

      String orderId = (String) event.get("orderId");
      String customerId = (String) event.get("customerId");
      List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");

      if (items == null || items.isEmpty()) {

        log.warn("OrderCreated sem itens. OrderId: {}", orderId);
        return;
      }

      for (Map<String, Object> item : items) {

        String productId = (String) item.get("productId");
        int quantity = (int) item.get("quantity");

        reserveStockUseCase.execute(new ReserveStockRequest(productId, quantity))
            .doOnSuccess(response ->
                log.info("Estoque reservado para produto: {}", productId))
            .doOnError(InsufficientStockException.class, e ->
                log.warn("Estoque insuficiente para produto: {}", productId))
            .doOnError(ProductNotFoundException.class, e ->
                log.warn("Produto não encontrado: {}",  productId))
            .onErrorComplete()
            .subscribe();
      }

      stockEventPublisher.publishStockReserved(orderId, customerId, items)
          .doOnError(e -> log.error("Falha ao publicar StockReservedEvent para orderId: {}", orderId))
          .onErrorComplete()
          .subscribe();

    } catch (Exception e) {
      log.error("Erro ao processar o OrderCreatedEvent: {}", e.getMessage(), e);
    }
  }
}
