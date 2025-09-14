package com.wwf.application.controller;

import com.wwf.application.model.Donation;
import com.wwf.application.model.DonationStatus;
import com.wwf.application.service.DonationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for donation management
 */
@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    private static final Logger logger = LoggerFactory.getLogger(DonationController.class);

    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    /**
     * Create a new donation
     */
    @PostMapping
    public ResponseEntity<Donation> createDonation(@Valid @RequestBody Donation donation) {
        try {
            Donation createdDonation = donationService.createDonation(donation);
            return new ResponseEntity<>(createdDonation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("Invalid donation data: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error creating donation", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Process a donation
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<Donation> processDonation(@PathVariable Long id) {
        try {
            Donation processedDonation = donationService.processDonation(id);
            return new ResponseEntity<>(processedDonation, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error processing donation: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error processing donation with ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all donations
     */
    @GetMapping
    public ResponseEntity<List<Donation>> getAllDonations() {
        try {
            List<Donation> donations = donationService.getAllDonations();
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving all donations", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get donation by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Donation> getDonationById(@PathVariable Long id) {
        try {
            Optional<Donation> donation = donationService.getDonationById(id);
            return donation.map(d -> new ResponseEntity<>(d, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error("Error retrieving donation with ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get donations by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Donation>> getDonationsByStatus(@PathVariable DonationStatus status) {
        try {
            List<Donation> donations = donationService.findDonationsByStatus(status);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving donations by status: {}", status, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get donations by donor email
     */
    @GetMapping("/donor")
    public ResponseEntity<List<Donation>> getDonationsByDonorEmail(@RequestParam String email) {
        try {
            List<Donation> donations = donationService.findDonationsByDonorEmail(email);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving donations by donor email: {}", email, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get donations by project
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Donation>> getDonationsByProject(@PathVariable Long projectId) {
        try {
            List<Donation> donations = donationService.findDonationsByProject(projectId);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving donations by project ID: {}", projectId, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get recent donations
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Donation>> getRecentDonations(@RequestParam(defaultValue = "30") int days) {
        try {
            List<Donation> donations = donationService.getRecentDonations(days);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving recent donations", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get large donations
     */
    @GetMapping("/large")
    public ResponseEntity<List<Donation>> getLargeDonations(@RequestParam BigDecimal minAmount) {
        try {
            List<Donation> donations = donationService.findLargeDonations(minAmount);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving large donations", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Refund a donation
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<Donation> refundDonation(@PathVariable Long id) {
        try {
            Donation refundedDonation = donationService.refundDonation(id);
            return new ResponseEntity<>(refundedDonation, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error refunding donation: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error refunding donation with ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get donation statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<List<Object[]>> getDonationStatistics() {
        try {
            List<Object[]> statistics = donationService.getDonationStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving donation statistics", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get top donors
     */
    @GetMapping("/top-donors")
    public ResponseEntity<List<Object[]>> getTopDonors() {
        try {
            List<Object[]> topDonors = donationService.getTopDonors();
            return new ResponseEntity<>(topDonors, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving top donors", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get total donations by status
     */
    @GetMapping("/total/{status}")
    public ResponseEntity<BigDecimal> getTotalDonationsByStatus(@PathVariable DonationStatus status) {
        try {
            BigDecimal total = donationService.getTotalDonationsByStatus(status);
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving total donations by status: {}", status, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get total donations for a project
     */
    @GetMapping("/project/{projectId}/total")
    public ResponseEntity<BigDecimal> getTotalDonationsForProject(@PathVariable Long projectId) {
        try {
            BigDecimal total = donationService.getTotalDonationsForProject(projectId);
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving total donations for project ID: {}", projectId, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Clean up stale pending donations
     */
    @PostMapping("/cleanup-stale")
    public ResponseEntity<String> cleanupStaleDonations(@RequestParam(defaultValue = "24") int hoursOld) {
        try {
            int cleanedUp = donationService.cleanupStaleDonations(hoursOld);
            return new ResponseEntity<>("Cleaned up " + cleanedUp + " stale donations", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error cleaning up stale donations", e);
            return new ResponseEntity<>("Error cleaning up stale donations", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}