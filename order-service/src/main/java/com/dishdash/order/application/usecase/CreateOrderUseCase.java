package com.dishdash.order.application.usecase;

import com.dishdash.order.application.dto.CreateOrderRequest;
import com.dishdash.order.application.dto.OrderResponse;
import com.dishdash.order.domain.event.OrderCreatedEvent;
import com.dishdash.order.domain.event.OrderCreatedEvent.OrderItemEvent;
import com.dishdash.order.domain.model.CustomerId;
import com.dishdash.order.domain.model.Order;
import com.dishdash.order.domain.model.OrderItem;
import com.dishdash.order.domain.repository.OrderRepository;
import com.dishdash.order.infrastructure.messaging.OrderEventPublisher;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

  private final OrderRepository orderRepository;
  private final OrderEventPublisher orderEventPublisher;

  public Mono<OrderResponse> execute(CreateOrderRequest request) {

    // Usamos Mono.defer para garantir execução lazy (sob demanda).
    // Tudo dentro desse bloco só será executado no momento da inscrição (subscribe),
    // ou seja, quando o fluxo reativo realmente rodar.
    //
    // Isso é importante porque Order.create() pode lançar exceção (ex: lista vazia).
    // Sem o defer, essa exceção seria lançada imediatamente (fora do Mono),
    // quebrando o fluxo reativo e impedindo que o StepVerifier (ou o consumidor)
    // capture o erro corretamente.
    //
    // Com o defer, qualquer exceção passa a acontecer dentro do Mono,
    // sendo propagada como sinal de erro reativo (onError).
    return Mono.defer(() -> {

      List<OrderItem> items = request.items().stream()
          .map(item -> OrderItem.of(
              item.productId(),
              item.productName(),
              item.quantity(),
              item.unitPrice()
          ))
          .toList();

      Order order = Order.create(
          new CustomerId(request.customerId()),
          items
      );

      return orderRepository.save(order)
          .flatMap(savedOrder -> {
            OrderCreatedEvent event = buildEvent(savedOrder);

            // publica o evento e retorna a response e se o evento falhar o pedido ja foi salvo
            return orderEventPublisher.publishOrderCreated(event)
                .doOnError(error ->
                    log.error("O pedido foi salvo mas a publicação "
                        + "do evento no topico falhou. orderId: {}", savedOrder.getId()))
                // para nao falhar o request se o kafka falhar
                .onErrorComplete()
                .thenReturn(OrderResponse.from(savedOrder));
          });
    });
  }

  private OrderCreatedEvent buildEvent(Order savedOrder) {

    List<OrderItemEvent> itemEvents = savedOrder.getItems().stream()
        .map(item -> new OrderItemEvent(
            item.getProductId(),
            item.getProductName(),
            item.getQuantity(),
            item.getUnitPrice()
        )).toList();

    return new OrderCreatedEvent(
        UUID.randomUUID().toString(),
        savedOrder.getId(),
        savedOrder.getCustomerId().value(),
        itemEvents,
        savedOrder.totalAmount(),
        LocalDateTime.now()
    );
  }
}
