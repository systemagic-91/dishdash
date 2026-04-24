package com.dishdash.order.application.usecase;

import com.dishdash.order.application.dto.CreateOrderRequest;
import com.dishdash.order.application.dto.OrderResponse;
import com.dishdash.order.domain.model.CustomerId;
import com.dishdash.order.domain.model.Order;
import com.dishdash.order.domain.model.OrderItem;
import com.dishdash.order.domain.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

  private final OrderRepository orderRepository;

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
          .map(OrderResponse::from);
    });
  }
}
