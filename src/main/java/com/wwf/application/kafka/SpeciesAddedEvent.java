package com.wwf.application.kafka;

import com.wwf.application.model.ConservationStatus;

/**
 * Event published when a new wildlife species is added to the system
 */
public class SpeciesAddedEvent extends BaseEvent {
    
    private Long speciesId;
    private String speciesName;
    private String scientificName;
    private ConservationStatus conservationStatus;
    private String habitat;
    private Integer populationEstimate;

    public SpeciesAddedEvent() {
        super("SPECIES_ADDED");
    }

    public SpeciesAddedEvent(Long speciesId, String speciesName, String scientificName, 
                           ConservationStatus conservationStatus) {
        this();
        this.speciesId = speciesId;
        this.speciesName = speciesName;
        this.scientificName = scientificName;
        this.conservationStatus = conservationStatus;
    }

    // Getters and Setters
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

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public ConservationStatus getConservationStatus() {
        return conservationStatus;
    }

    public void setConservationStatus(ConservationStatus conservationStatus) {
        this.conservationStatus = conservationStatus;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public Integer getPopulationEstimate() {
        return populationEstimate;
    }

    public void setPopulationEstimate(Integer populationEstimate) {
        this.populationEstimate = populationEstimate;
    }

    @Override
    public String toString() {
        return "SpeciesAddedEvent{" +
                "speciesId=" + speciesId +
                ", speciesName='" + speciesName + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", conservationStatus=" + conservationStatus +
                ", habitat='" + habitat + '\'' +
                ", populationEstimate=" + populationEstimate +
                "} " + super.toString();
    }
}