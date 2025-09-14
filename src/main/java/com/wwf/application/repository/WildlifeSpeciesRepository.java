package com.wwf.application.repository;

import com.wwf.application.model.ConservationStatus;
import com.wwf.application.model.WildlifeSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WildlifeSpecies entity
 */
@Repository
public interface WildlifeSpeciesRepository extends JpaRepository<WildlifeSpecies, Long> {

    /**
     * Find species by scientific name (case insensitive)
     */
    Optional<WildlifeSpecies> findByScientificNameIgnoreCase(String scientificName);

    /**
     * Find species by name containing text (case insensitive)
     */
    List<WildlifeSpecies> findByNameContainingIgnoreCase(String name);

    /**
     * Find species by conservation status
     */
    List<WildlifeSpecies> findByConservationStatus(ConservationStatus status);

    /**
     * Find endangered species (critically endangered, endangered, vulnerable)
     */
    @Query("SELECT w FROM WildlifeSpecies w WHERE w.conservationStatus IN " +
           "('CRITICALLY_ENDANGERED', 'ENDANGERED', 'VULNERABLE')")
    List<WildlifeSpecies> findEndangeredSpecies();

    /**
     * Find species by habitat containing text (case insensitive)
     */
    List<WildlifeSpecies> findByHabitatContainingIgnoreCase(String habitat);

    /**
     * Find species with population estimate less than specified value
     */
    @Query("SELECT w FROM WildlifeSpecies w WHERE w.populationEstimate < :maxPopulation")
    List<WildlifeSpecies> findSpeciesWithLowPopulation(@Param("maxPopulation") Integer maxPopulation);

    /**
     * Count species by conservation status
     */
    @Query("SELECT w.conservationStatus, COUNT(w) FROM WildlifeSpecies w GROUP BY w.conservationStatus")
    List<Object[]> countSpeciesByConservationStatus();

    /**
     * Check if species exists by scientific name
     */
    boolean existsByScientificNameIgnoreCase(String scientificName);
}