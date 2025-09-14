package com.wwf.application.service;

import com.wwf.application.kafka.DonationProcessedEvent;
import com.wwf.application.model.ConservationProject;
import com.wwf.application.model.Donation;
import com.wwf.application.model.DonationStatus;
import com.wwf.application.repository.ConservationProjectRepository;
import com.wwf.application.repository.DonationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing donations
 */
@Service
@Transactional
public class DonationService {

    private static final Logger logger = LoggerFactory.getLogger(DonationService.class);
    private static final String DONATION_TOPIC = "wwf.donation.events";

    private final DonationRepository repository;
    private final ConservationProjectRepository projectRepository;
    private final ConservationProjectService projectService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public DonationService(DonationRepository repository,
                          ConservationProjectRepository projectRepository,
                          ConservationProjectService projectService,
                          KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Create a new donation
     */
    public Donation createDonation(Donation donation) {
        logger.info("Creating new donation from donor: {}", donation.getDonorName());
        
        // Validate project if provided
        if (donation.getProject() != null && donation.getProject().getId() != null) {
            ConservationProject project = projectRepository.findById(donation.getProject().getId())
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + 
                    donation.getProject().getId()));
            donation.setProject(project);
        }
        
        Donation savedDonation = repository.save(donation);
        logger.info("Successfully created donation with ID: {}", savedDonation.getId());
        
        return savedDonation;
    }

    /**
     * Process a donation (simulate payment processing)
     */
    public Donation processDonation(Long donationId) {
        logger.info("Processing donation with ID: {}", donationId);
        
        Donation donation = repository.findById(donationId)
            .orElseThrow(() -> new RuntimeException("Donation not found with ID: " + donationId));
        
        if (donation.getStatus() != DonationStatus.PENDING) {
            throw new IllegalStateException("Donation is not in PENDING status");
        }
        
        // Set status to processing
        donation.setStatus(DonationStatus.PROCESSING);
        repository.save(donation);
        
        try {
            // Simulate payment processing delay
            Thread.sleep(1000);
            
            // Simulate payment success (90% success rate for demo)
            boolean paymentSuccess = Math.random() < 0.9;
            
            if (paymentSuccess) {
                // Process successful payment
                donation.setStatus(DonationStatus.COMPLETED);
                donation.setTransactionId(UUID.randomUUID().toString());
                
                // Add funds to project if specified
                if (donation.getProject() != null) {
                    projectService.addFundsToProject(donation.getProject().getId(), donation.getAmount());
                }
                
                logger.info("Successfully processed donation ID: {}", donationId);
                
            } else {
                // Process failed payment
                donation.setStatus(DonationStatus.FAILED);
                logger.warn("Payment failed for donation ID: {}", donationId);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            donation.setStatus(DonationStatus.FAILED);
            logger.error("Processing interrupted for donation ID: {}", donationId, e);
        } catch (Exception e) {
            donation.setStatus(DonationStatus.FAILED);
            logger.error("Error processing donation ID: {}", donationId, e);
        }
        
        Donation savedDonation = repository.save(donation);
        
        // Publish event
        publishDonationProcessedEvent(savedDonation);
        
        return savedDonation;
    }

    /**
     * Get donation by ID
     */
    @Transactional(readOnly = true)
    public Optional<Donation> getDonationById(Long id) {
        return repository.findById(id);
    }

    /**
     * Get all donations
     */
    @Transactional(readOnly = true)
    public List<Donation> getAllDonations() {
        return repository.findAll();
    }

    /**
     * Find donations by status
     */
    @Transactional(readOnly = true)
    public List<Donation> findDonationsByStatus(DonationStatus status) {
        return repository.findByStatus(status);
    }

    /**
     * Find donations by donor email
     */
    @Transactional(readOnly = true)
    public List<Donation> findDonationsByDonorEmail(String email) {
        return repository.findByDonorEmailIgnoreCase(email);
    }

    /**
     * Find donations by project
     */
    @Transactional(readOnly = true)
    public List<Donation> findDonationsByProject(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    /**
     * Find recent donations
     */
    @Transactional(readOnly = true)
    public List<Donation> getRecentDonations(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return repository.findRecentDonations(cutoff);
    }

    /**
     * Find large donations
     */
    @Transactional(readOnly = true)
    public List<Donation> findLargeDonations(BigDecimal minAmount) {
        return repository.findLargeDonations(minAmount);
    }

    /**
     * Get donation statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getDonationStatistics() {
        return repository.countDonationsByStatus();
    }

    /**
     * Get top donors
     */
    @Transactional(readOnly = true)
    public List<Object[]> getTopDonors() {
        return repository.findTopDonors();
    }

    /**
     * Calculate total donations by status
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalDonationsByStatus(DonationStatus status) {
        BigDecimal total = repository.calculateTotalDonationsByStatus(status);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Calculate total donations for a project
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalDonationsForProject(Long projectId) {
        BigDecimal total = repository.calculateTotalDonationsForProject(projectId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Refund a donation
     */
    public Donation refundDonation(Long donationId) {
        logger.info("Refunding donation with ID: {}", donationId);
        
        Donation donation = repository.findById(donationId)
            .orElseThrow(() -> new RuntimeException("Donation not found with ID: " + donationId));
        
        if (donation.getStatus() != DonationStatus.COMPLETED) {
            throw new IllegalStateException("Only completed donations can be refunded");
        }
        
        donation.setStatus(DonationStatus.REFUNDED);
        
        // Remove funds from project if applicable
        if (donation.getProject() != null) {
            ConservationProject project = donation.getProject();
            BigDecimal currentFunds = project.getFundsRaised() != null ? 
                project.getFundsRaised() : BigDecimal.ZERO;
            BigDecimal newFunds = currentFunds.subtract(donation.getAmount());
            if (newFunds.compareTo(BigDecimal.ZERO) < 0) {
                newFunds = BigDecimal.ZERO;
            }
            project.setFundsRaised(newFunds);
            projectRepository.save(project);
        }
        
        Donation savedDonation = repository.save(donation);
        
        // Publish event
        publishDonationProcessedEvent(savedDonation);
        
        logger.info("Successfully refunded donation ID: {}", donationId);
        return savedDonation;
    }

    /**
     * Clean up stale pending donations
     */
    @Transactional
    public int cleanupStaleDonations(int hoursOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(hoursOld);
        List<Donation> staleDonations = repository.findStalePendingDonations(cutoff);
        
        for (Donation donation : staleDonations) {
            donation.setStatus(DonationStatus.FAILED);
            repository.save(donation);
        }
        
        logger.info("Cleaned up {} stale pending donations", staleDonations.size());
        return staleDonations.size();
    }

    /**
     * Publish donation processed event to Kafka
     */
    private void publishDonationProcessedEvent(Donation donation) {
        try {
            DonationProcessedEvent event = new DonationProcessedEvent(
                donation.getId(),
                donation.getProject() != null ? donation.getProject().getId() : null,
                donation.getAmount(),
                donation.getDonorName(),
                donation.getStatus()
            );
            event.setDonorEmail(donation.getDonorEmail());
            event.setTransactionId(donation.getTransactionId());
            
            if (donation.getProject() != null) {
                event.setProjectName(donation.getProject().getName());
            }
            
            kafkaTemplate.send(DONATION_TOPIC, event);
            logger.info("Published DonationProcessedEvent for donation ID: {}", donation.getId());
            
        } catch (Exception e) {
            logger.error("Failed to publish DonationProcessedEvent for donation ID: {}", 
                donation.getId(), e);
        }
    }
}