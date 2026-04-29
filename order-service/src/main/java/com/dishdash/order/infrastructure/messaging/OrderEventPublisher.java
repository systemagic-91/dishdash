package com.dishdash.order.infrastructure.messaging;

import com.dishdash.order.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper mapper;

  public Mono<Void> publishOrderCreated(OrderCreatedEvent event) {

    // mono from callable envolve a operacao bloqueante em um mono
    // o kafkaTemplate.send() é imperativo por isso uso esse metodo aqui
    return Mono.fromCallable(() -> {
      String json = mapper.writeValueAsString(event);
      return kafkaTemplate.send("order-created", event.eventId(), json);
    })
      .flatMap(Mono::fromFuture)
      .doOnSuccess(result ->
          log.info("OrderCreatedEvent publicado. OrderId: {}", event.orderId()))
      .doOnError(error ->
          log.error("Erro ao publicar OrderCreatedEvent: {}", error.getMessage()))
      .then();
  }
}
