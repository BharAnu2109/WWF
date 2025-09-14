package com.wwf.application.repository;

import com.wwf.application.model.Donation;
import com.wwf.application.model.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Donation entity
 */
@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    /**
     * Find donations by status
     */
    List<Donation> findByStatus(DonationStatus status);

    /**
     * Find donations by donor email
     */
    List<Donation> findByDonorEmailIgnoreCase(String donorEmail);

    /**
     * Find donations by project ID
     */
    List<Donation> findByProjectId(Long projectId);

    /**
     * Find donations by donor name containing text (case insensitive)
     */
    List<Donation> findByDonorNameContainingIgnoreCase(String donorName);

    /**
     * Find donations created within date range
     */
    @Query("SELECT d FROM Donation d WHERE d.createdAt BETWEEN :startDate AND :endDate")
    List<Donation> findDonationsCreatedBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find large donations (amount >= specified value)
     */
    @Query("SELECT d FROM Donation d WHERE d.amount >= :minAmount")
    List<Donation> findLargeDonations(@Param("minAmount") BigDecimal minAmount);

    /**
     * Calculate total donations by status
     */
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.status = :status")
    BigDecimal calculateTotalDonationsByStatus(@Param("status") DonationStatus status);

    /**
     * Calculate total donations for a project
     */
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.project.id = :projectId AND d.status = 'COMPLETED'")
    BigDecimal calculateTotalDonationsForProject(@Param("projectId") Long projectId);

    /**
     * Find top donors by total donation amount
     */
    @Query("SELECT d.donorEmail, SUM(d.amount) as total FROM Donation d " +
           "WHERE d.status = 'COMPLETED' GROUP BY d.donorEmail ORDER BY total DESC")
    List<Object[]> findTopDonors();

    /**
     * Count donations by status
     */
    @Query("SELECT d.status, COUNT(d) FROM Donation d GROUP BY d.status")
    List<Object[]> countDonationsByStatus();

    /**
     * Find recent donations (last N days)
     */
    @Query("SELECT d FROM Donation d WHERE d.createdAt >= :cutoffDate ORDER BY d.createdAt DESC")
    List<Donation> findRecentDonations(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find pending donations older than specified time
     */
    @Query("SELECT d FROM Donation d WHERE d.status = 'PENDING' AND d.createdAt < :cutoffDate")
    List<Donation> findStalePendingDonations(@Param("cutoffDate") LocalDateTime cutoffDate);
}