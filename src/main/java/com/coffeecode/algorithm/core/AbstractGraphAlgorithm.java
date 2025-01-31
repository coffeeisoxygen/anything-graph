package com.coffeecode.algorithm.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import com.coffeecode.event.core.EventManager;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGraphAlgorithm implements GraphAlgorithm {

    protected final LocationGraph graph;
    protected final Queue<Step> steps;
    protected final Map<LocationNode, LocationNode> previous;
    protected final EventManager eventManager;
    protected LocationNode start;
    protected LocationNode end;
    protected volatile boolean isComplete;
    protected volatile boolean isPaused;

    @Value
    protected static class Step {

        LocationNode node;
        double progress;
    }

    protected AbstractGraphAlgorithm(LocationGraph graph, EventManager eventManager) {
        this.graph = graph;
        this.steps = new LinkedList<>();
        this.previous = new HashMap<>();
        this.eventManager = Objects.requireNonNull(eventManager, "EventManager cannot be null");
    }

    @Override
    public boolean hasNextStep() {
        return !steps.isEmpty() && !isComplete;
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public List<LocationNode> getCurrentPath() {
        return reconstructPath();
    }

    protected List<LocationNode> reconstructPath() {
        if (end == null) {
            return Collections.emptyList();
        }

        List<LocationNode> path = new ArrayList<>();
        LocationNode current = end;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }
        return path;
    }

    // Common algorithm functionality
    @Override
    public void validateInput(LocationGraph graph, LocationNode start, LocationNode end) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        Objects.requireNonNull(start, "Start node cannot be null");
        Objects.requireNonNull(end, "End node cannot be null");

        if (!graph.hasNode(start) || !graph.hasNode(end)) {
            throw new IllegalArgumentException("Start and end nodes must exist in graph");
        }
    }
}
