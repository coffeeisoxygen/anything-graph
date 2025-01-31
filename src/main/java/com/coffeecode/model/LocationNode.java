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
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
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
