package com.wwf.application.controller;

import com.wwf.application.model.ConservationStatus;
import com.wwf.application.model.WildlifeSpecies;
import com.wwf.application.service.WildlifeSpeciesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for wildlife species management
 */
@RestController
@RequestMapping("/api/species")
@CrossOrigin(origins = "*")
public class WildlifeSpeciesController {

    private static final Logger logger = LoggerFactory.getLogger(WildlifeSpeciesController.class);

    private final WildlifeSpeciesService speciesService;

    @Autowired
    public WildlifeSpeciesController(WildlifeSpeciesService speciesService) {
        this.speciesService = speciesService;
    }

    /**
     * Create a new wildlife species
     */
    @PostMapping
    public ResponseEntity<WildlifeSpecies> createSpecies(@Valid @RequestBody WildlifeSpecies species) {
        try {
            WildlifeSpecies createdSpecies = speciesService.createSpecies(species);
            return new ResponseEntity<>(createdSpecies, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid species data: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error creating species", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all wildlife species
     */
    @GetMapping
    public ResponseEntity<List<WildlifeSpecies>> getAllSpecies() {
        try {
            List<WildlifeSpecies> species = speciesService.getAllSpecies();
            return new ResponseEntity<>(species, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving all species", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get species by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WildlifeSpecies> getSpeciesById(@PathVariable Long id) {
        try {
            Optional<WildlifeSpecies> species = speciesService.getSpeciesById(id);
            return species.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                         .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error("Error retrieving species with ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update species by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<WildlifeSpecies> updateSpecies(@PathVariable Long id, 
                                                       @Valid @RequestBody WildlifeSpecies species) {
        try {
            WildlifeSpecies updatedSpecies = speciesService.updateSpecies(id, species);
            return new ResponseEntity<>(updatedSpecies, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Species not found with ID: {}", id);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating species with ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete species by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpecies(@PathVariable Long id) {
        try {
            speciesService.deleteSpecies(id);
            return new ResponseEntity<>("Species deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Species not found with ID: {}", id);
            return new ResponseEntity<>("Species not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting species with ID: {}", id, e);
            return new ResponseEntity<>("Error deleting species", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search species by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<WildlifeSpecies>> searchSpeciesByName(@RequestParam String name) {
        try {
            List<WildlifeSpecies> species = speciesService.findSpeciesByName(name);
            return new ResponseEntity<>(species, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error searching species by name: {}", name, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get species by conservation status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WildlifeSpecies>> getSpeciesByStatus(@PathVariable ConservationStatus status) {
        try {
            List<WildlifeSpecies> species = speciesService.findSpeciesByConservationStatus(status);
            return new ResponseEntity<>(species, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving species by status: {}", status, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get endangered species
     */
    @GetMapping("/endangered")
    public ResponseEntity<List<WildlifeSpecies>> getEndangeredSpecies() {
        try {
            List<WildlifeSpecies> species = speciesService.getEndangeredSpecies();
            return new ResponseEntity<>(species, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving endangered species", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search species by habitat
     */
    @GetMapping("/habitat")
    public ResponseEntity<List<WildlifeSpecies>> getSpeciesByHabitat(@RequestParam String habitat) {
        try {
            List<WildlifeSpecies> species = speciesService.findSpeciesByHabitat(habitat);
            return new ResponseEntity<>(species, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error searching species by habitat: {}", habitat, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get species with low population
     */
    @GetMapping("/low-population")
    public ResponseEntity<List<WildlifeSpecies>> getSpeciesWithLowPopulation(@RequestParam Integer maxPopulation) {
        try {
            List<WildlifeSpecies> species = speciesService.findSpeciesWithLowPopulation(maxPopulation);
            return new ResponseEntity<>(species, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving species with low population", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get species statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<List<Object[]>> getSpeciesStatistics() {
        try {
            List<Object[]> statistics = speciesService.getSpeciesStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving species statistics", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}