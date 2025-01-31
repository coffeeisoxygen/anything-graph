package com.coffeecode.model;

import java.util.Objects;

/**
 * Immutable node representing a location with coordinates. Core data structure
 * for graph operations.
 */
public class LocationNode {

    private final String id;
    private final double latitude;
    private final double longitude;

    public LocationNode(String id, double latitude, double longitude) {
        this.id = validateId(id);
        this.latitude = validateLatitude(latitude);
        this.longitude = validateLongitude(longitude);
    }

    // Validation methods
    private String validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        return id;
    }

    private double validateLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        return latitude;
    }

    private double validateLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        return longitude;
    }

    // Getters
    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationNode)) {
            return false;
        }
        LocationNode that = (LocationNode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Node{id='%s', latitude=%.6f, longitude=%.6f}",
                id, latitude, longitude);
    }
}
