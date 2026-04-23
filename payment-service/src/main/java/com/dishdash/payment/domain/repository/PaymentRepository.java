package com.dishdash.payment.domain.repository;

import com.dishdash.payment.domain.model.Payment;
import reactor.core.publisher.Mono;

public interface PaymentRepository {

  Mono<Payment> save(Payment payment);

  Mono<Payment> findById(String paymentId);

  // para verificar se ja existe pagamento para um pedido
  // para evitar cobrar duas vezes o mesmo pedido
  Mono<Payment> findByOrderId(String orderId);
}
