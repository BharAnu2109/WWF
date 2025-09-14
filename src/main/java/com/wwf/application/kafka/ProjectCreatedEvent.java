package com.wwf.application.kafka;

import com.wwf.application.model.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Event published when a conservation project is created
 */
public class ProjectCreatedEvent extends BaseEvent {
    
    private Long projectId;
    private String projectName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private ProjectStatus status;
    private String location;
    private Long speciesId;
    private String speciesName;

    public ProjectCreatedEvent() {
        super("PROJECT_CREATED");
    }

    public ProjectCreatedEvent(Long projectId, String projectName, LocalDate startDate, BigDecimal budget) {
        this();
        this.projectId = projectId;
        this.projectName = projectName;
        this.startDate = startDate;
        this.budget = budget;
    }

    // Getters and Setters
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(Long speciesId) {
        this.speciesId = speciesId;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    @Override
    public String toString() {
        return "ProjectCreatedEvent{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", budget=" + budget +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", speciesId=" + speciesId +
                ", speciesName='" + speciesName + '\'' +
                "} " + super.toString();
    }
}