package com.wwf.application.repository;

import com.wwf.application.model.ConservationProject;
import com.wwf.application.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for ConservationProject entity
 */
@Repository
public interface ConservationProjectRepository extends JpaRepository<ConservationProject, Long> {

    /**
     * Find projects by status
     */
    List<ConservationProject> findByStatus(ProjectStatus status);

    /**
     * Find projects by location containing text (case insensitive)
     */
    List<ConservationProject> findByLocationContainingIgnoreCase(String location);

    /**
     * Find projects by species ID
     */
    List<ConservationProject> findBySpeciesId(Long speciesId);

    /**
     * Find active projects (status = ACTIVE)
     */
    @Query("SELECT p FROM ConservationProject p WHERE p.status = 'ACTIVE'")
    List<ConservationProject> findActiveProjects();

    /**
     * Find projects starting within date range
     */
    @Query("SELECT p FROM ConservationProject p WHERE p.startDate BETWEEN :startDate AND :endDate")
    List<ConservationProject> findProjectsStartingBetween(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    /**
     * Find projects needing funding (funds raised < budget)
     */
    @Query("SELECT p FROM ConservationProject p WHERE p.fundsRaised < p.budget AND p.status IN ('PLANNING', 'ACTIVE')")
    List<ConservationProject> findProjectsNeedingFunding();

    /**
     * Find projects by funding progress percentage
     */
    @Query("SELECT p FROM ConservationProject p WHERE (p.fundsRaised / p.budget * 100) >= :minPercentage")
    List<ConservationProject> findProjectsByFundingProgress(@Param("minPercentage") double minPercentage);

    /**
     * Calculate total budget for all projects
     */
    @Query("SELECT SUM(p.budget) FROM ConservationProject p")
    BigDecimal calculateTotalBudget();

    /**
     * Calculate total funds raised for all projects
     */
    @Query("SELECT SUM(p.fundsRaised) FROM ConservationProject p")
    BigDecimal calculateTotalFundsRaised();

    /**
     * Find projects by name containing text (case insensitive)
     */
    List<ConservationProject> findByNameContainingIgnoreCase(String name);

    /**
     * Count projects by status
     */
    @Query("SELECT p.status, COUNT(p) FROM ConservationProject p GROUP BY p.status")
    List<Object[]> countProjectsByStatus();
}