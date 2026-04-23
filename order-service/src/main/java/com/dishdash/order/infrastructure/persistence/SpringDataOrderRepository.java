package com.dishdash.order.infrastructure.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SpringDataOrderRepository extends ReactiveMongoRepository<OrderDocument, String> {

}
