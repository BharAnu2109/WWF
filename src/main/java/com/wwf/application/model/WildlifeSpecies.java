package com.wwf.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wildlife species in the WWF conservation database
 */
@Entity
@Table(name = "wildlife_species")
public class WildlifeSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Species name is required")
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Scientific name is required")
    @Size(max = 255)
    @Column(name = "scientific_name", nullable = false, unique = true)
    private String scientificName;

    @NotNull(message = "Conservation status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "conservation_status", nullable = false)
    private ConservationStatus conservationStatus;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "habitat")
    private String habitat;

    @Column(name = "population_estimate")
    private Integer populationEstimate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "species", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConservationProject> conservationProjects = new ArrayList<>();

    // Constructors
    public WildlifeSpecies() {
        this.createdAt = LocalDateTime.now();
    }

    public WildlifeSpecies(String name, String scientificName, ConservationStatus conservationStatus) {
        this();
        this.name = name;
        this.scientificName = scientificName;
        this.conservationStatus = conservationStatus;
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

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
        this.updatedAt = LocalDateTime.now();
    }

    public ConservationStatus getConservationStatus() {
        return conservationStatus;
    }

    public void setConservationStatus(ConservationStatus conservationStatus) {
        this.conservationStatus = conservationStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getPopulationEstimate() {
        return populationEstimate;
    }

    public void setPopulationEstimate(Integer populationEstimate) {
        this.populationEstimate = populationEstimate;
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

    public List<ConservationProject> getConservationProjects() {
        return conservationProjects;
    }

    public void setConservationProjects(List<ConservationProject> conservationProjects) {
        this.conservationProjects = conservationProjects;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "WildlifeSpecies{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", conservationStatus=" + conservationStatus +
                ", habitat='" + habitat + '\'' +
                ", populationEstimate=" + populationEstimate +
                '}';
    }
}