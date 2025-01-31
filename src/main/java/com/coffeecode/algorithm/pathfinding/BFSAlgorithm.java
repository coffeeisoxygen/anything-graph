package com.coffeecode.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import com.coffeecode.algorithm.core.AbstractGraphAlgorithm;
import com.coffeecode.event.core.EventManager;
import com.coffeecode.event.state.AlgorithmState;
import com.coffeecode.event.state.GraphState;

import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Slf4j
public class BFSAlgorithm extends AbstractGraphAlgorithm {

    private final Queue<Step> steps;
    private final Map<LocationNode, LocationNode> previous;
    private final Map<LocationNode, Double> distances;

    public BFSAlgorithm(LocationGraph graph, EventManager eventManager) {
        super(graph, eventManager);
        this.steps = new LinkedList<>();
        this.previous = new HashMap<>();
        this.distances = new HashMap<>();
    }

    private final Set<LocationNode> visited = new HashSet<>();
    private final Queue<LocationNode> queue = new LinkedList<>();

    @Override
    public String getName() {
        return "Breadth-First Search";
    }

    @Override
    public List<LocationNode> execute(LocationGraph graph, LocationNode start,
            LocationNode end, GraphState graphState) {
        validateInput(graph, start, end);
        AlgorithmState algoState = new AlgorithmState(eventManager);

        graphState.clear(); // Reset visualization
        algoState.reset();  // Reset algorithm state

        queue.offer(start);
        graphState.markProcessing(start);
        graphState.updateDistance(start, 0.0);

        while (!queue.isEmpty() && !algoState.isPaused()) {
            LocationNode current = queue.poll();
            graphState.markVisited(current);

            double progress = graphState.getVisitedNodes().size()
                    / (double) graph.getNodeCount();

            if (current.equals(end)) {
                log.debug("Found path to target node {}", end);
                return reconstructPath();
            }

            for (LocationNode neighbor : graph.getDestinations(current)) {
                if (!graphState.isVisited(neighbor) && !graphState.isProcessing(neighbor)) {
                    queue.offer(neighbor);
                    graphState.markProcessing(neighbor);
                    graphState.updateParent(neighbor, current);
                    graphState.updateDistance(neighbor,
                            graphState.getDistance(current) + 1);

                    // Mark edge as processing
                    graph.getEdge(current, neighbor).ifPresent(edge
                            -> graphState.markProcessing(edge));
                }
            }

            // Update progress
            graphState.updateState(current, progress);
            algoState.updateProgress(progress); // Algorithm progress
            graphState.step();
        }

        return Collections.emptyList();
    }

    @Override
    public void executeStep() {
        if (isPaused || isComplete || queue.isEmpty()) {
            return;
        }

        LocationNode current = queue.poll();
        visited.add(current);
        double progress = visited.size() / (double) graph.getNodeCount();

        if (current.equals(end)) {
            isComplete = true;
            log.debug("Path found to target node: {}", end);
            return;
        }

        // Process neighbors
        for (LocationNode neighbor : graph.getDestinations(current)) {
            if (!visited.contains(neighbor) && !queue.contains(neighbor)) {
                queue.offer(neighbor);
                previous.put(neighbor, current);
                // Calculate actual distance
                double currentDist = distances.getOrDefault(current, 0.0);
                graph.getEdge(current, neighbor).ifPresent(edge
                        -> distances.put(neighbor, currentDist + edge.getWeight())
                );
            }
        }

        steps.offer(new Step(current, progress));
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public List<LocationNode> getCurrentPath() {
        return reconstructPath();
    }

    @Override
    public double getCurrentDistance() {
        return end != null ? distances.getOrDefault(end, Double.POSITIVE_INFINITY) : 0.0;
    }

    @Override
    public void reset() {
        visited.clear();
        queue.clear();
        previous.clear();
        distances.clear();
        steps.clear();
        isComplete = false;
        isPaused = false;
    }

    // Helper method to validate input
    private void validateInput(LocationGraph graph, LocationNode start, LocationNode end) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        Objects.requireNonNull(start, "Start node cannot be null");
        Objects.requireNonNull(end, "End node cannot be null");

        if (!graph.getNodes().contains(start)) {
            throw new IllegalArgumentException("Start node not in graph");
        }
        if (!graph.getNodes().contains(end)) {
            throw new IllegalArgumentException("End node not in graph");
        }
    }
}
