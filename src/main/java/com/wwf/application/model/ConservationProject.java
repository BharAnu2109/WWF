package com.wwf.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Represents a conservation project for wildlife protection
 */
@Entity
@Table(name = "conservation_projects")
public class ConservationProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project name is required")
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 2000)
    @Column(name = "description")
    private String description;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull(message = "Budget is required")
    @PositiveOrZero(message = "Budget must be positive or zero")
    @Column(name = "budget", nullable = false, precision = 15, scale = 2)
    private BigDecimal budget;

    @PositiveOrZero(message = "Funds raised must be positive or zero")
    @Column(name = "funds_raised", precision = 15, scale = 2)
    private BigDecimal fundsRaised = BigDecimal.ZERO;

    @NotNull(message = "Project status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;

    @Size(max = 255)
    @Column(name = "location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    private WildlifeSpecies species;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public ConservationProject() {
        this.createdAt = LocalDateTime.now();
        this.status = ProjectStatus.PLANNING;
        this.fundsRaised = BigDecimal.ZERO;
    }

    public ConservationProject(String name, LocalDate startDate, BigDecimal budget) {
        this();
        this.name = name;
        this.startDate = startDate;
        this.budget = budget;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getFundsRaised() {
        return fundsRaised;
    }

    public void setFundsRaised(BigDecimal fundsRaised) {
        this.fundsRaised = fundsRaised;
        this.updatedAt = LocalDateTime.now();
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public WildlifeSpecies getSpecies() {
        return species;
    }

    public void setSpecies(WildlifeSpecies species) {
        this.species = species;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate the funding progress as a percentage
     * @return funding progress percentage (0-100)
     */
    public double getFundingProgress() {
        if (budget == null || budget.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return fundsRaised.divide(budget, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    @Override
    public String toString() {
        return "ConservationProject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", budget=" + budget +
                ", fundsRaised=" + fundsRaised +
                ", status=" + status +
                ", location='" + location + '\'' +
                '}';
    }
}