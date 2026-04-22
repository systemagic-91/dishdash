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

    List<OrderItem> items = request.items().stream()
        .map(item -> OrderItem.of(
            item.productId(),
            item.productName(),
            item.quantity(),
            item.unitPrice()
        ))
        .toList();

    Order order = Order.create(new CustomerId(request.customerId()), items);

    return orderRepository.save(order)
        .map(OrderResponse::from);
  }
}
