package com.coffeecode.event.core;

import java.util.List;

import com.coffeecode.model.LocationNode;

import lombok.Value;

public sealed interface AlgorithmEvent {

    @Value
    class Started implements AlgorithmEvent {

        String algorithmName;
        LocationNode start;
        LocationNode end;
    }

    @Value
    class StepCompleted implements AlgorithmEvent {

        LocationNode current;
        double progress;
    }

    @Value
    class Completed implements AlgorithmEvent {

        List<LocationNode> path;
        double totalDistance;
    }

    @Value
    class Failed implements AlgorithmEvent {

        String error;
    }
}
