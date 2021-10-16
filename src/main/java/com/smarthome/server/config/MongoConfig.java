package com.smarthome.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableMongoRepositories(basePackages = "com.smarthome.server.dao")
@Configuration
public class MongoConfig {
}
