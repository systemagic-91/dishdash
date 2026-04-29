package com.dishdash.payment.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(
    basePackages = "com.dishdash.payment.infrastructure.persistence"
)
public class MongoConfig {

}
