package com.coffeecode.event.state;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.coffeecode.event.core.EventManager;
import com.coffeecode.event.core.GraphStateEvent;
import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationNode;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe implementation of graph algorithm state. Tracks
 * visited/processing nodes and edges for visualization.
 */
@Getter
@Slf4j
public class GraphState {

    private final Set<LocationNode> visitedNodes;
    private final Set<LocationNode> processingNodes;
    private final Set<LocationEdge> visitedEdges;
    private final Set<LocationEdge> processingEdges;
    private final Map<LocationNode, Double> distances;
    private final Map<LocationNode, LocationNode> parent;
    private final EventManager eventManager;

    public GraphState(EventManager eventManager) {
        this.visitedNodes = new CopyOnWriteArraySet<>();
        this.processingNodes = new CopyOnWriteArraySet<>();
        this.visitedEdges = new CopyOnWriteArraySet<>();
        this.processingEdges = new CopyOnWriteArraySet<>();
        this.distances = new ConcurrentHashMap<>();
        this.parent = new ConcurrentHashMap<>();
        this.eventManager = eventManager;
    }

    public void markVisited(@NotNull @NonNull LocationNode node) {
        processingNodes.remove(node);
        visitedNodes.add(node);
        eventManager.publish(new GraphStateEvent.NodeVisited(node));
        log.debug("Node marked as visited: {}", node);
    }

    public void markProcessing(@NotNull @NonNull LocationNode node) {
        processingNodes.add(node);
        eventManager.publish(new GraphStateEvent.NodeProcessing(node));
        log.debug("Node marked as processing: {}", node);
    }

    public void markVisited(@NotNull @NonNull LocationEdge edge) {
        processingEdges.remove(edge);
        visitedEdges.add(edge);
        eventManager.publish(new GraphStateEvent.EdgeVisited(edge));
        log.debug("Edge marked as visited: {}", edge);
    }

    public void markProcessing(@NotNull @NonNull LocationEdge edge) {
        processingEdges.add(edge);
        eventManager.publish(new GraphStateEvent.EdgeProcessing(edge));
        log.debug("Edge marked as processing: {}", edge);
    }

    public void updateDistance(@NotNull @NonNull LocationNode node, double distance) {
        distances.put(node, distance);
        eventManager.publish(new GraphStateEvent.DistanceUpdated(node, distance));
        log.debug("Distance updated for node {}: {}", node, distance);
    }

    public void updateParent(@NotNull @NonNull LocationNode node,
            @NotNull @NonNull LocationNode parent) {
        this.parent.put(node, parent);
        log.debug("Parent updated for node {}: {}", node, parent);
    }

    public void clear() {
        visitedNodes.clear();
        processingNodes.clear();
        visitedEdges.clear();
        processingEdges.clear();
        distances.clear();
        parent.clear();
        eventManager.publish(GraphStateEvent.StateCleared.INSTANCE);
        log.info("Graph state cleared");
    }

    public Set<LocationNode> getVisitedNodes() {
        return Collections.unmodifiableSet(visitedNodes);
    }

    public Set<LocationNode> getProcessingNodes() {
        return Collections.unmodifiableSet(processingNodes);
    }

    public Set<LocationEdge> getVisitedEdges() {
        return Collections.unmodifiableSet(visitedEdges);
    }

    public Set<LocationEdge> getProcessingEdges() {
        return Collections.unmodifiableSet(processingEdges);
    }

    public Map<LocationNode, Double> getDistances() {
        return Collections.unmodifiableMap(distances);
    }

    public Map<LocationNode, LocationNode> getParent() {
        return Collections.unmodifiableMap(parent);
    }

    /**
     * Checks if node has been processed
     */
    public boolean isVisited(@NotNull @NonNull LocationNode node) {
        return visitedNodes.contains(node);
    }

    /**
     * Checks if node is currently being processed
     */
    public boolean isProcessing(@NotNull @NonNull LocationNode node) {
        return processingNodes.contains(node);
    }

    /**
     * Gets current distance to node, or Double.POSITIVE_INFINITY if not set
     */
    public double getDistance(@NotNull @NonNull LocationNode node) {
        return distances.getOrDefault(node, Double.POSITIVE_INFINITY);
    }

    /**
     * Gets parent node in path, or null if not set
     */
    public LocationNode getParentNode(@NotNull @NonNull LocationNode node) {
        return parent.get(node);
    }

    public boolean isPaused() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isPaused'");
    }

    public void updateState(LocationNode current, double progress) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateState'");
    }

    public void step() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'step'");
    }
}
