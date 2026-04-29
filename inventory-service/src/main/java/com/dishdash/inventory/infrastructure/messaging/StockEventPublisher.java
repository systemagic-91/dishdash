package com.dishdash.inventory.infrastructure.messaging;

import com.dishdash.inventory.domain.event.StockReservedEvent;
import com.dishdash.inventory.domain.event.StockReservedEvent.ReservedItemEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventPublisher {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper mapper;

  public Mono<Void> publishStockReserved(String orderId, String customerId, List<Map<String,
      Object>> items) {

    return Mono.fromCallable(() -> {
      List<ReservedItemEvent> reservedItems = items.stream()
          .map(item -> new ReservedItemEvent(
              (String) item.get("productId"),
              (int) item.get("quantity")
          )).toList();

      StockReservedEvent event = new StockReservedEvent(
          UUID.randomUUID().toString(),
          orderId,
          customerId,
          reservedItems,
          LocalDateTime.now()
      );

      String json = mapper.writeValueAsString(event);
      return kafkaTemplate.send("stock-reserved", orderId, json);
    })
    .flatMap(Mono::fromFuture)
    .doOnSuccess(result ->
        log.info("StockReservedEvent publicado para orderId: {}", orderId))
    .doOnError(error ->
        log.error("Erro ao publicar StockReservedEvent: {}", error.getMessage()))
    .then();
  }
}
