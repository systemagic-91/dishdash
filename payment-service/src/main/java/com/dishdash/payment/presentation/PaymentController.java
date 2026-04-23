package com.dishdash.payment.presentation;

import com.dishdash.payment.application.dto.PaymentResponse;
import com.dishdash.payment.application.dto.ProcessPaymentRequest;
import com.dishdash.payment.application.usecase.GetPaymentUseCase;
import com.dishdash.payment.application.usecase.ProcessPaymentUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final ProcessPaymentUseCase processPaymentUseCase;
  private final GetPaymentUseCase getPaymentUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<PaymentResponse> process(@Valid @RequestBody ProcessPaymentRequest request) {

    return processPaymentUseCase.execute(request);
  }

  @GetMapping("/{id}")
  public Mono<PaymentResponse> findById(@PathVariable String id) {

    return getPaymentUseCase.findById(id);
  }

  @GetMapping("/order/{orderId}")
  public Mono<PaymentResponse> findByOrderId(@PathVariable String orderId) {

    return getPaymentUseCase.findByOrderId(orderId);
  }
}
