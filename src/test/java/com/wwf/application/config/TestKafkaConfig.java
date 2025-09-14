package com.wwf.application.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

/**
 * Test configuration to disable Kafka during tests
 */
@TestConfiguration
@Profile("test")
public class TestKafkaConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate() {
        // Return a mock KafkaTemplate for tests
        return mock(KafkaTemplate.class);
    }
}