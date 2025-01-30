package com.coffeecode.model;

import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(of = {"source", "destination"})
public class LocationEdge {

    @NonNull
    private final LocationNode source;

    @NonNull
    private final LocationNode destination;

    @Positive
    private final double weight;

    public LocationEdge(@NonNull LocationNode source, @NonNull LocationNode destination, @Positive double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public LocationEdge(@NonNull LocationNode source, @NonNull LocationNode destination) {
        this(source, destination, 1.0);
    }
}
