package com.dishdash.payment.application.usecase;

import com.dishdash.payment.application.dto.PaymentResponse;
import com.dishdash.payment.domain.exception.PaymentNotFoundException;
import com.dishdash.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetPaymentUseCase {

  private final PaymentRepository paymentRepository;

  public Mono<PaymentResponse> findById(String id) {

    return paymentRepository.findById(id)
        .switchIfEmpty(Mono.error(new PaymentNotFoundException(id)))
        .map(PaymentResponse::from);

  }

  public Mono<PaymentResponse> findByOrderId(String orderId) {

    return paymentRepository.findByOrderId(orderId)
        .switchIfEmpty(Mono.error(new PaymentNotFoundException("para o pedido: " + orderId)))
        .map(PaymentResponse::from);
  }
}
