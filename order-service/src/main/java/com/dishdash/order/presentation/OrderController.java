package com.dishdash.order.presentation;

import com.dishdash.order.application.dto.CreateOrderRequest;
import com.dishdash.order.application.dto.OrderResponse;
import com.dishdash.order.application.usecase.CancelOrderUseCase;
import com.dishdash.order.application.usecase.CreateOrderUseCase;
import com.dishdash.order.application.usecase.GetOrderUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final CreateOrderUseCase createOrderUseCase;
  private final GetOrderUseCase getOrderUseCase;
  private final CancelOrderUseCase cancelOrderUseCase;


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {

    return createOrderUseCase.execute(request);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<OrderResponse> findById(@PathVariable String id) {

    return getOrderUseCase.findById(id);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Flux<OrderResponse> findAll() {

    return getOrderUseCase.findAll();
  }

  // cancelar é uma mudanca de estado, o pedido continua existindo
  // por isso o @PatchMapping e nao o @DeleteMapping
  @PatchMapping("/{id}/cancel")
  @ResponseStatus(HttpStatus.OK)
  public Mono<OrderResponse> cancel(@PathVariable String id) {

    return cancelOrderUseCase.execute(id);
  }
}
