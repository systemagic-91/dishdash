package com.dishdash.payment.infrastructure.persistence;

import com.dishdash.payment.domain.model.Payment;
import com.dishdash.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

  private final SpringDataPaymentRepository repository;
  private final PaymentMapper mapper;

  @Override
  public Mono<Payment> save(Payment payment) {

    return repository.save(mapper.toDocument(payment))
        .map(mapper::toDomain);
  }

  @Override
  public Mono<Payment> findById(String paymentId) {

    return repository.findById(paymentId)
        .map(mapper::toDomain);
  }

  @Override
  public Mono<Payment> findByOrderId(String orderId) {

    return repository.findByOrderId(orderId)
        .map(mapper::toDomain);
  }
}
