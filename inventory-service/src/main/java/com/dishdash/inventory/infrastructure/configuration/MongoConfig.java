package com.dishdash.inventory.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(
    basePackages = "com.dishdash.inventory.infrastructure.persistence"
)
public class MongoConfig {

}
