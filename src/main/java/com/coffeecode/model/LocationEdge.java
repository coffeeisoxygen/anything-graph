package com.coffeecode.model;

import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents a weighted edge between two LocationNodes. Immutable value object
 * representing connection between nodes. Weight calculation is handled by
 * LocationGraph.
 */
@Getter
@EqualsAndHashCode(of = {"source", "destination"})
public class LocationEdge {

    @NonNull
    private final LocationNode source;

    @NonNull
    private final LocationNode destination;

    @Positive
    private final double weight;

    /**
     * Creates edge with custom weight
     *
     * @param source Source node
     * @param destination Destination node
     * @param weight Edge weight (must be positive)
     * @throws IllegalArgumentException if weight <= 0
     */
    public LocationEdge(@NonNull LocationNode source,
            @NonNull LocationNode destination,
            @Positive double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("Edge{%s -> %s, weight=%.2f}",
                source.getId(), destination.getId(), weight);
    }
}
