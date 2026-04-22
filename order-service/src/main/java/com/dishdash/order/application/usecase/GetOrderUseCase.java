package com.dishdash.order.application.usecase;

import com.dishdash.order.application.dto.OrderResponse;
import com.dishdash.order.domain.exception.OrderNotFoundException;
import com.dishdash.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetOrderUseCase {

  private final OrderRepository orderRepository;

  public Mono<OrderResponse> findById(String id) {

    return orderRepository.findById(id)
        // transforma "nao encontrado" em um erro de dominio
        // sem isso, o controller receberia um mono vazio sem saber o q fazer
        .switchIfEmpty(Mono.error(new OrderNotFoundException(id)))
        .map(OrderResponse::from);
  }

  public Flux<OrderResponse> findAll() {

    return orderRepository.findAll()
        .map(OrderResponse::from);
  }
}
