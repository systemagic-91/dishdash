package com.dishdash.order.application.usecase;

import com.dishdash.order.application.dto.OrderResponse;
import com.dishdash.order.domain.exception.OrderNotFoundException;
import com.dishdash.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

  private final OrderRepository orderRepository;

  public Mono<OrderResponse> execute(String id) {

    return orderRepository.findById(id)
        .switchIfEmpty(Mono.error(new OrderNotFoundException(id)))
        .flatMap(order -> {
          order.cancel();
          return orderRepository.save(order);
        })
        .map(OrderResponse::from);
  }
}
