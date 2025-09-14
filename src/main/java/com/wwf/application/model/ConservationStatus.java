package com.wwf.application.model;

/**
 * Conservation status levels according to IUCN Red List categories
 */
public enum ConservationStatus {
    EXTINCT("Extinct"),
    EXTINCT_IN_WILD("Extinct in the Wild"),
    CRITICALLY_ENDANGERED("Critically Endangered"),
    ENDANGERED("Endangered"),
    VULNERABLE("Vulnerable"),
    NEAR_THREATENED("Near Threatened"),
    LEAST_CONCERN("Least Concern"),
    DATA_DEFICIENT("Data Deficient"),
    NOT_EVALUATED("Not Evaluated");

    private final String displayName;

    ConservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}