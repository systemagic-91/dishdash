package com.dishdash.order.infrastructure.persistence;

import com.dishdash.order.domain.model.Order;
import com.dishdash.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

  private final SpringDataOrderRepository repository;
  private final OrderMapper mapper;

  @Override
  public Mono<Order> save(Order order) {

    return repository.save(mapper.toDocument(order))
        .map(mapper::toDomain);
  }

  @Override
  public Mono<Order> findById(String orderId) {

    return repository.findById(orderId)
        .map(mapper::toDomain);
  }

  @Override
  public Flux<Order> findAll() {

    return repository.findAll()
        .map(mapper::toDomain);
  }

  @Override
  public Mono<Void> deleteById(String orderId) {

    return repository.deleteById(orderId);
  }
}
