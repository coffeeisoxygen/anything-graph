package com.coffeecode.model;

import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(of = {"source", "destination"})
public class Edge {

    @NonNull
    private final Node source;

    @NonNull
    private final Node destination;

    @Positive
    private final double weight;

    public Edge(@NonNull Node source, @NonNull Node destination, @Positive double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Edge(@NonNull Node source, @NonNull Node destination) {
        this(source, destination, 1.0);
    }
}
