package com.wwf.application.service;

import com.wwf.application.kafka.SpeciesAddedEvent;
import com.wwf.application.model.ConservationStatus;
import com.wwf.application.model.WildlifeSpecies;
import com.wwf.application.repository.WildlifeSpeciesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing wildlife species
 */
@Service
@Transactional
public class WildlifeSpeciesService {

    private static final Logger logger = LoggerFactory.getLogger(WildlifeSpeciesService.class);
    private static final String SPECIES_TOPIC = "wwf.species.events";

    private final WildlifeSpeciesRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public WildlifeSpeciesService(WildlifeSpeciesRepository repository, 
                                 KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Create a new wildlife species
     */
    public WildlifeSpecies createSpecies(WildlifeSpecies species) {
        logger.info("Creating new species: {}", species.getName());
        
        // Check if species already exists
        if (repository.existsByScientificNameIgnoreCase(species.getScientificName())) {
            throw new IllegalArgumentException("Species with scientific name '" + 
                species.getScientificName() + "' already exists");
        }
        
        WildlifeSpecies savedSpecies = repository.save(species);
        
        // Publish event
        publishSpeciesAddedEvent(savedSpecies);
        
        logger.info("Successfully created species with ID: {}", savedSpecies.getId());
        return savedSpecies;
    }

    /**
     * Update an existing wildlife species
     */
    public WildlifeSpecies updateSpecies(Long id, WildlifeSpecies updatedSpecies) {
        logger.info("Updating species with ID: {}", id);
        
        WildlifeSpecies existingSpecies = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Species not found with ID: " + id));
        
        // Update fields
        existingSpecies.setName(updatedSpecies.getName());
        existingSpecies.setConservationStatus(updatedSpecies.getConservationStatus());
        existingSpecies.setDescription(updatedSpecies.getDescription());
        existingSpecies.setHabitat(updatedSpecies.getHabitat());
        existingSpecies.setPopulationEstimate(updatedSpecies.getPopulationEstimate());
        
        WildlifeSpecies savedSpecies = repository.save(existingSpecies);
        logger.info("Successfully updated species with ID: {}", savedSpecies.getId());
        
        return savedSpecies;
    }

    /**
     * Get species by ID
     */
    @Transactional(readOnly = true)
    public Optional<WildlifeSpecies> getSpeciesById(Long id) {
        return repository.findById(id);
    }

    /**
     * Get all species
     */
    @Transactional(readOnly = true)
    public List<WildlifeSpecies> getAllSpecies() {
        return repository.findAll();
    }

    /**
     * Find species by name
     */
    @Transactional(readOnly = true)
    public List<WildlifeSpecies> findSpeciesByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Find species by conservation status
     */
    @Transactional(readOnly = true)
    public List<WildlifeSpecies> findSpeciesByConservationStatus(ConservationStatus status) {
        return repository.findByConservationStatus(status);
    }

    /**
     * Get endangered species
     */
    @Transactional(readOnly = true)
    public List<WildlifeSpecies> getEndangeredSpecies() {
        return repository.findEndangeredSpecies();
    }

    /**
     * Find species by habitat
     */
    @Transactional(readOnly = true)
    public List<WildlifeSpecies> findSpeciesByHabitat(String habitat) {
        return repository.findByHabitatContainingIgnoreCase(habitat);
    }

    /**
     * Find species with low population
     */
    @Transactional(readOnly = true)
    public List<WildlifeSpecies> findSpeciesWithLowPopulation(Integer maxPopulation) {
        return repository.findSpeciesWithLowPopulation(maxPopulation);
    }

    /**
     * Delete species by ID
     */
    public void deleteSpecies(Long id) {
        logger.info("Deleting species with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new RuntimeException("Species not found with ID: " + id);
        }
        
        repository.deleteById(id);
        logger.info("Successfully deleted species with ID: {}", id);
    }

    /**
     * Get species statistics by conservation status
     */
    @Transactional(readOnly = true)
    public List<Object[]> getSpeciesStatistics() {
        return repository.countSpeciesByConservationStatus();
    }

    /**
     * Publish species added event to Kafka
     */
    private void publishSpeciesAddedEvent(WildlifeSpecies species) {
        try {
            SpeciesAddedEvent event = new SpeciesAddedEvent(
                species.getId(),
                species.getName(),
                species.getScientificName(),
                species.getConservationStatus()
            );
            event.setHabitat(species.getHabitat());
            event.setPopulationEstimate(species.getPopulationEstimate());
            
            kafkaTemplate.send(SPECIES_TOPIC, event);
            logger.info("Published SpeciesAddedEvent for species ID: {}", species.getId());
            
        } catch (Exception e) {
            logger.error("Failed to publish SpeciesAddedEvent for species ID: {}", 
                species.getId(), e);
        }
    }
}