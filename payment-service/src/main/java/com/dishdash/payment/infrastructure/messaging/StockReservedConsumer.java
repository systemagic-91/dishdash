package com.dishdash.payment.infrastructure.messaging;

import com.dishdash.payment.application.dto.ProcessPaymentRequest;
import com.dishdash.payment.application.usecase.ProcessPaymentUseCase;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockReservedConsumer {

  private final ProcessPaymentUseCase processPaymentUseCase;
  private final PaymentEventPublisher paymentEventPublisher;
  private final ObjectMapper mapper;

  @KafkaListener(
      topics = "stock-reserved",
      groupId = "payment-group"
  )
  public void consume(String message) {

    log.info("StockReservedEvent recebido: {}", message);

    try {

      Map<String, Object> event = mapper.readValue(message, Map.class);

      String orderId = (String) event.get("orderId");
      String customerId = (String) event.get("customerId");

      List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");

      BigDecimal totalAmount = items.stream()
          .map(item -> {

            BigDecimal unitPrice = new BigDecimal(item.get("unitPrice").toString());

            int quantity = (int) item.get("quantity");

            return unitPrice.multiply(BigDecimal.valueOf(quantity));
          })
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      ProcessPaymentRequest request = new ProcessPaymentRequest(orderId, customerId, totalAmount);

      processPaymentUseCase.execute(request)
          .flatMap(paymentEventPublisher::publishPaymentProcessed)
          .doOnError(error ->
              log.error("Erro ao processar pagamento para orderId: {} - {}", orderId, error.getMessage()))
          .onErrorComplete()
          .subscribe();

    } catch (Exception e) {
      log.error("Erro ao processar StockReservedEvent: {}", e.getMessage());
    }
  }
}
