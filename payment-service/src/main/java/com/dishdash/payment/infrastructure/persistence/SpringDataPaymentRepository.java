package com.dishdash.payment.infrastructure.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SpringDataPaymentRepository
    extends ReactiveMongoRepository<PaymentDocument, String> {

  Mono<PaymentDocument> findByOrderId(String orderId);
}
