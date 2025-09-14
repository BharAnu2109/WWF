package com.wwf.application.controller;

import com.wwf.application.model.ConservationProject;
import com.wwf.application.model.ProjectStatus;
import com.wwf.application.service.ConservationProjectService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for conservation project management
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ConservationProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ConservationProjectController.class);

    private final ConservationProjectService projectService;

    @Autowired
    public ConservationProjectController(ConservationProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Create a new conservation project
     */
    @PostMapping
    public ResponseEntity<ConservationProject> createProject(@Valid @RequestBody ConservationProject project) {
        try {
            ConservationProject createdProject = projectService.createProject(project);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("Invalid project data: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error creating project", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all conservation projects
     */
    @GetMapping
    public ResponseEntity<List<ConservationProject>> getAllProjects() {
        try {
            List<ConservationProject> projects = projectService.getAllProjects();
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving all projects", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get project by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConservationProject> getProjectById(@PathVariable Long id) {
        try {
            Optional<ConservationProject> project = projectService.getProjectById(id);
            return project.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                         .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error("Error retrieving project with ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update project by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConservationProject> updateProject(@PathVariable Long id, 
                                                            @Valid @RequestBody ConservationProject project) {
        try {
            ConservationProject updatedProject = projectService.updateProject(id, project);
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Project not found with ID: {}", id);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating project with ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete project by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return new ResponseEntity<>("Project deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Project not found with ID: {}", id);
            return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting project with ID: {}", id, e);
            return new ResponseEntity<>("Error deleting project", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get projects by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ConservationProject>> getProjectsByStatus(@PathVariable ProjectStatus status) {
        try {
            List<ConservationProject> projects = projectService.findProjectsByStatus(status);
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving projects by status: {}", status, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get active projects
     */
    @GetMapping("/active")
    public ResponseEntity<List<ConservationProject>> getActiveProjects() {
        try {
            List<ConservationProject> projects = projectService.getActiveProjects();
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving active projects", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search projects by location
     */
    @GetMapping("/location")
    public ResponseEntity<List<ConservationProject>> getProjectsByLocation(@RequestParam String location) {
        try {
            List<ConservationProject> projects = projectService.findProjectsByLocation(location);
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error searching projects by location: {}", location, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get projects by species
     */
    @GetMapping("/species/{speciesId}")
    public ResponseEntity<List<ConservationProject>> getProjectsBySpecies(@PathVariable Long speciesId) {
        try {
            List<ConservationProject> projects = projectService.findProjectsBySpecies(speciesId);
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving projects by species ID: {}", speciesId, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get projects needing funding
     */
    @GetMapping("/funding-needed")
    public ResponseEntity<List<ConservationProject>> getProjectsNeedingFunding() {
        try {
            List<ConservationProject> projects = projectService.getProjectsNeedingFunding();
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving projects needing funding", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get projects by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<ConservationProject>> getProjectsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<ConservationProject> projects = projectService.findProjectsByDateRange(startDate, endDate);
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving projects by date range: {} to {}", startDate, endDate, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add funds to a project
     */
    @PostMapping("/{id}/add-funds")
    public ResponseEntity<ConservationProject> addFundsToProject(@PathVariable Long id, 
                                                                @RequestParam BigDecimal amount) {
        try {
            ConservationProject updatedProject = projectService.addFundsToProject(id, amount);
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Project not found with ID: {}", id);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error adding funds to project with ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get project statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<List<Object[]>> getProjectStatistics() {
        try {
            List<Object[]> statistics = projectService.getProjectStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving project statistics", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get financial summary
     */
    @GetMapping("/financial-summary")
    public ResponseEntity<ConservationProjectService.ProjectFinancialSummary> getFinancialSummary() {
        try {
            ConservationProjectService.ProjectFinancialSummary summary = projectService.getFinancialSummary();
            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving financial summary", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}