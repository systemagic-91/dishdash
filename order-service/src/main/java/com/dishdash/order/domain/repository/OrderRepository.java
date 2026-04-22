package com.dishdash.order.domain.repository;


import com.dishdash.order.domain.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {

  Mono<Order> save(Order order);

  Mono<Order> findById(String orderId);

  Flux<Order> findAll();

  Mono<Void> deleteById(String orderId);
}
