package com.wwf.application.service;

import com.wwf.application.kafka.DonationProcessedEvent;
import com.wwf.application.kafka.ProjectCreatedEvent;
import com.wwf.application.kafka.SpeciesAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Service to handle incoming Kafka events
 */
@Service
@Profile("!test")  // Exclude from test profile
public class EventListenerService {

    private static final Logger logger = LoggerFactory.getLogger(EventListenerService.class);

    /**
     * Handle species added events
     */
    @KafkaListener(topics = "wwf.species.events", groupId = "wwf-species-group")
    public void handleSpeciesAddedEvent(@Payload SpeciesAddedEvent event,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                       @Header(KafkaHeaders.OFFSET) long offset,
                                       Acknowledgment acknowledgment) {
        try {
            logger.info("Received SpeciesAddedEvent: {} from topic: {}, partition: {}, offset: {}", 
                event, topic, partition, offset);
            
            // Process the event - could trigger notifications, analytics, etc.
            processSpeciesAddedEvent(event);
            
            // Acknowledge message processing
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing SpeciesAddedEvent: {}", event, e);
            // In production, you might want to send to a dead letter queue
        }
    }

    /**
     * Handle project created events
     */
    @KafkaListener(topics = "wwf.project.events", groupId = "wwf-project-group")
    public void handleProjectCreatedEvent(@Payload ProjectCreatedEvent event,
                                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                         @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                         @Header(KafkaHeaders.OFFSET) long offset,
                                         Acknowledgment acknowledgment) {
        try {
            logger.info("Received ProjectCreatedEvent: {} from topic: {}, partition: {}, offset: {}", 
                event, topic, partition, offset);
            
            // Process the event - could trigger notifications, analytics, etc.
            processProjectCreatedEvent(event);
            
            // Acknowledge message processing
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing ProjectCreatedEvent: {}", event, e);
            // In production, you might want to send to a dead letter queue
        }
    }

    /**
     * Handle donation processed events
     */
    @KafkaListener(topics = "wwf.donation.events", groupId = "wwf-donation-group")
    public void handleDonationProcessedEvent(@Payload DonationProcessedEvent event,
                                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                           @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                           @Header(KafkaHeaders.OFFSET) long offset,
                                           Acknowledgment acknowledgment) {
        try {
            logger.info("Received DonationProcessedEvent: {} from topic: {}, partition: {}, offset: {}", 
                event, topic, partition, offset);
            
            // Process the event - could trigger notifications, analytics, etc.
            processDonationProcessedEvent(event);
            
            // Acknowledge message processing
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing DonationProcessedEvent: {}", event, e);
            // In production, you might want to send to a dead letter queue
        }
    }

    /**
     * Process species added event
     */
    private void processSpeciesAddedEvent(SpeciesAddedEvent event) {
        // Example processing - could send notifications, update analytics, etc.
        logger.info("Processing species added: {} - Conservation Status: {}", 
            event.getSpeciesName(), event.getConservationStatus());
        
        // If species is endangered, could trigger alerts
        if (isEndangered(event.getConservationStatus().name())) {
            logger.warn("ALERT: Endangered species added - {}", event.getSpeciesName());
            // Could send email alerts, push notifications, etc.
        }
    }

    /**
     * Process project created event
     */
    private void processProjectCreatedEvent(ProjectCreatedEvent event) {
        // Example processing - could send notifications, update analytics, etc.
        logger.info("Processing project created: {} - Budget: {}", 
            event.getProjectName(), event.getBudget());
        
        // Could notify stakeholders, update dashboards, etc.
        if (event.getSpeciesName() != null) {
            logger.info("Project {} created for species: {}", 
                event.getProjectName(), event.getSpeciesName());
        }
    }

    /**
     * Process donation processed event
     */
    private void processDonationProcessedEvent(DonationProcessedEvent event) {
        // Example processing - could send notifications, update analytics, etc.
        logger.info("Processing donation: {} - Amount: {} - Status: {}", 
            event.getDonationId(), event.getAmount(), event.getStatus());
        
        switch (event.getStatus()) {
            case COMPLETED:
                logger.info("Thank you message should be sent to: {}", event.getDonorEmail());
                // Could send thank you emails, update donor profiles, etc.
                break;
            case FAILED:
                logger.warn("Failed donation notification should be sent to: {}", event.getDonorEmail());
                // Could send failure notifications, retry mechanisms, etc.
                break;
            case REFUNDED:
                logger.info("Refund confirmation should be sent to: {}", event.getDonorEmail());
                // Could send refund confirmations, update accounting, etc.
                break;
            default:
                logger.info("Donation status update: {}", event.getStatus());
        }
    }

    /**
     * Helper method to check if conservation status indicates endangered species
     */
    private boolean isEndangered(String status) {
        return "CRITICALLY_ENDANGERED".equals(status) || 
               "ENDANGERED".equals(status) || 
               "VULNERABLE".equals(status);
    }
}