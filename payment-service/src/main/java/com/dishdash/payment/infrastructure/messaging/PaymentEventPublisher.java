package com.dishdash.payment.infrastructure.messaging;

import com.dishdash.payment.application.dto.PaymentResponse;
import com.dishdash.payment.domain.event.PaymentProcessedEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper mapper;

  public Mono<Void> publishPaymentProcessed(PaymentResponse response) {

    return Mono.fromCallable(() -> {

      PaymentProcessedEvent event = new PaymentProcessedEvent(
          UUID.randomUUID().toString(),
          response.id(),
          response.orderId(),
          response.customerId(),
          response.amount(),
          response.status(),
          response.rejectionReason(),
          LocalDateTime.now()
      );

      String json = mapper.writeValueAsString(event);
      return kafkaTemplate.send("payment-processed", response.orderId(), json);
    })
        .flatMap(Mono::fromFuture)
        .doOnSuccess(result ->
            log.info("PaymentProcessedEvent publicado para orderId: {}", response.orderId()))
        .doOnError(error ->
            log.error("Erro ao publicar PaymentProcessedEvent: {}", error.getMessage()))
        .then();
  }
}
