package com.wwf.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * World Wildlife Fund Application
 * A comprehensive Spring Boot application for wildlife conservation management
 * with Kafka messaging, Docker containerization, and Kubernetes deployment.
 */
@SpringBootApplication
@EnableKafka
public class WwfApplication {

    public static void main(String[] args) {
        SpringApplication.run(WwfApplication.class, args);
    }
}