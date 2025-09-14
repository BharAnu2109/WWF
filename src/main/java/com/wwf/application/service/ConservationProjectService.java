package com.wwf.application.service;

import com.wwf.application.kafka.ProjectCreatedEvent;
import com.wwf.application.model.ConservationProject;
import com.wwf.application.model.ProjectStatus;
import com.wwf.application.model.WildlifeSpecies;
import com.wwf.application.repository.ConservationProjectRepository;
import com.wwf.application.repository.WildlifeSpeciesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing conservation projects
 */
@Service
@Transactional
public class ConservationProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ConservationProjectService.class);
    private static final String PROJECT_TOPIC = "wwf.project.events";

    private final ConservationProjectRepository repository;
    private final WildlifeSpeciesRepository speciesRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public ConservationProjectService(ConservationProjectRepository repository,
                                    WildlifeSpeciesRepository speciesRepository,
                                    KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.speciesRepository = speciesRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Create a new conservation project
     */
    public ConservationProject createProject(ConservationProject project) {
        logger.info("Creating new project: {}", project.getName());
        
        // Validate species if provided
        if (project.getSpecies() != null && project.getSpecies().getId() != null) {
            WildlifeSpecies species = speciesRepository.findById(project.getSpecies().getId())
                .orElseThrow(() -> new RuntimeException("Species not found with ID: " + 
                    project.getSpecies().getId()));
            project.setSpecies(species);
        }
        
        ConservationProject savedProject = repository.save(project);
        
        // Publish event
        publishProjectCreatedEvent(savedProject);
        
        logger.info("Successfully created project with ID: {}", savedProject.getId());
        return savedProject;
    }

    /**
     * Update an existing conservation project
     */
    public ConservationProject updateProject(Long id, ConservationProject updatedProject) {
        logger.info("Updating project with ID: {}", id);
        
        ConservationProject existingProject = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
        
        // Update fields
        existingProject.setName(updatedProject.getName());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setStartDate(updatedProject.getStartDate());
        existingProject.setEndDate(updatedProject.getEndDate());
        existingProject.setBudget(updatedProject.getBudget());
        existingProject.setStatus(updatedProject.getStatus());
        existingProject.setLocation(updatedProject.getLocation());
        
        // Update species if provided
        if (updatedProject.getSpecies() != null && updatedProject.getSpecies().getId() != null) {
            WildlifeSpecies species = speciesRepository.findById(updatedProject.getSpecies().getId())
                .orElseThrow(() -> new RuntimeException("Species not found with ID: " + 
                    updatedProject.getSpecies().getId()));
            existingProject.setSpecies(species);
        }
        
        ConservationProject savedProject = repository.save(existingProject);
        logger.info("Successfully updated project with ID: {}", savedProject.getId());
        
        return savedProject;
    }

    /**
     * Get project by ID
     */
    @Transactional(readOnly = true)
    public Optional<ConservationProject> getProjectById(Long id) {
        return repository.findById(id);
    }

    /**
     * Get all projects
     */
    @Transactional(readOnly = true)
    public List<ConservationProject> getAllProjects() {
        return repository.findAll();
    }

    /**
     * Find projects by status
     */
    @Transactional(readOnly = true)
    public List<ConservationProject> findProjectsByStatus(ProjectStatus status) {
        return repository.findByStatus(status);
    }

    /**
     * Get active projects
     */
    @Transactional(readOnly = true)
    public List<ConservationProject> getActiveProjects() {
        return repository.findActiveProjects();
    }

    /**
     * Find projects by location
     */
    @Transactional(readOnly = true)
    public List<ConservationProject> findProjectsByLocation(String location) {
        return repository.findByLocationContainingIgnoreCase(location);
    }

    /**
     * Find projects by species
     */
    @Transactional(readOnly = true)
    public List<ConservationProject> findProjectsBySpecies(Long speciesId) {
        return repository.findBySpeciesId(speciesId);
    }

    /**
     * Find projects needing funding
     */
    @Transactional(readOnly = true)
    public List<ConservationProject> getProjectsNeedingFunding() {
        return repository.findProjectsNeedingFunding();
    }

    /**
     * Find projects by date range
     */
    @Transactional(readOnly = true)
    public List<ConservationProject> findProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findProjectsStartingBetween(startDate, endDate);
    }

    /**
     * Add funds to a project
     */
    public ConservationProject addFundsToProject(Long projectId, BigDecimal amount) {
        logger.info("Adding funds {} to project ID: {}", amount, projectId);
        
        ConservationProject project = repository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        
        BigDecimal currentFunds = project.getFundsRaised() != null ? 
            project.getFundsRaised() : BigDecimal.ZERO;
        project.setFundsRaised(currentFunds.add(amount));
        
        ConservationProject savedProject = repository.save(project);
        logger.info("Successfully added funds to project ID: {}", projectId);
        
        return savedProject;
    }

    /**
     * Delete project by ID
     */
    public void deleteProject(Long id) {
        logger.info("Deleting project with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new RuntimeException("Project not found with ID: " + id);
        }
        
        repository.deleteById(id);
        logger.info("Successfully deleted project with ID: {}", id);
    }

    /**
     * Get project statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getProjectStatistics() {
        return repository.countProjectsByStatus();
    }

    /**
     * Get total budget and funds raised
     */
    @Transactional(readOnly = true)
    public ProjectFinancialSummary getFinancialSummary() {
        BigDecimal totalBudget = repository.calculateTotalBudget();
        BigDecimal totalFundsRaised = repository.calculateTotalFundsRaised();
        
        return new ProjectFinancialSummary(
            totalBudget != null ? totalBudget : BigDecimal.ZERO,
            totalFundsRaised != null ? totalFundsRaised : BigDecimal.ZERO
        );
    }

    /**
     * Publish project created event to Kafka
     */
    private void publishProjectCreatedEvent(ConservationProject project) {
        try {
            ProjectCreatedEvent event = new ProjectCreatedEvent(
                project.getId(),
                project.getName(),
                project.getStartDate(),
                project.getBudget()
            );
            event.setDescription(project.getDescription());
            event.setEndDate(project.getEndDate());
            event.setStatus(project.getStatus());
            event.setLocation(project.getLocation());
            
            if (project.getSpecies() != null) {
                event.setSpeciesId(project.getSpecies().getId());
                event.setSpeciesName(project.getSpecies().getName());
            }
            
            kafkaTemplate.send(PROJECT_TOPIC, event);
            logger.info("Published ProjectCreatedEvent for project ID: {}", project.getId());
            
        } catch (Exception e) {
            logger.error("Failed to publish ProjectCreatedEvent for project ID: {}", 
                project.getId(), e);
        }
    }

    /**
     * Inner class for financial summary
     */
    public static class ProjectFinancialSummary {
        private final BigDecimal totalBudget;
        private final BigDecimal totalFundsRaised;

        public ProjectFinancialSummary(BigDecimal totalBudget, BigDecimal totalFundsRaised) {
            this.totalBudget = totalBudget;
            this.totalFundsRaised = totalFundsRaised;
        }

        public BigDecimal getTotalBudget() {
            return totalBudget;
        }

        public BigDecimal getTotalFundsRaised() {
            return totalFundsRaised;
        }

        public double getFundingPercentage() {
            if (totalBudget == null || totalBudget.compareTo(BigDecimal.ZERO) == 0) {
                return 0.0;
            }
            return totalFundsRaised.divide(totalBudget, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
        }
    }
}