package com.coffeecode.algorithm.core;

import java.util.List;

import com.coffeecode.event.state.GraphState;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

public interface GraphAlgorithm {

    String getName();

    // Add step-by-step support
    boolean hasNextStep();

    void executeStep();

    // Add pause/resume
    void pause();

    void resume();

    // Get current state
    boolean isComplete();

    List<LocationNode> getCurrentPath();

    double getCurrentDistance();

    // Existing methods
    List<LocationNode> execute(
            LocationGraph graph,
            LocationNode start,
            LocationNode end,
            GraphState state
    );

    default void validateInput(LocationGraph graph, LocationNode start, LocationNode end) {
        if (!graph.hasNode(start)) {
            throw new IllegalArgumentException("Start node not in graph");
        }
        if (end != null && !graph.hasNode(end)) {
            throw new IllegalArgumentException("End node not in graph");
        }
    }
}
