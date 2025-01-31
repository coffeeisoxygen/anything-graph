package com.coffeecode.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.coffeecode.event.core.EventListener;
import com.coffeecode.event.core.EventManager;
import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.event.service.ServiceLocator;
import com.coffeecode.model.weight.EdgeWeightStrategy;
import com.coffeecode.model.weight.WeightStrategies;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe implementation of location-based graph. Supports event publishing
 * and custom weight strategies.
 */
@Getter
@Slf4j
public class LocationGraph {

    private final Map<LocationNode, Set<LocationEdge>> adjacencyList;
    private final EventManager eventManager;
    private final EdgeWeightStrategy weightStrategy;

    public LocationGraph() {
        this(WeightStrategies.HAVERSINE_DISTANCE);
    }

    public LocationGraph(EdgeWeightStrategy weightStrategy) {
        this.adjacencyList = new ConcurrentHashMap<>();
        this.eventManager = ServiceLocator.getInstance().getEventManager();
        this.weightStrategy = weightStrategy;
    }

    /**
     * Adds a node to the graph
     *
     * @return true if node was added, false if already exists
     */
    public boolean addNode(@NotNull @NonNull LocationNode node) {
        boolean added = adjacencyList.putIfAbsent(node,
                Collections.newSetFromMap(new ConcurrentHashMap<>())) == null;
        if (added) {
            eventManager.publish(new GraphEvent.NodeAdded(node));
            log.info("Node added successfully: {}", node);
        } else {
            log.warn("Failed to add node (already exists): {}", node);
        }
        return added;
    }

    /**
     * Adds edge using weight strategy
     */
    public boolean addEdge(@NonNull LocationNode source, @NonNull LocationNode destination) {
        double weight = weightStrategy.calculateWeight(source, destination);
        log.info("Adding edge from {} to {} with weight {}", source, destination, weight);
        return addEdge(new LocationEdge(source, destination, weight));
    }

    /**
     * Adds edge with custom weight
     */
    public boolean addEdge(@NotNull @NonNull LocationEdge edge) {
        addNode(edge.getSource());
        addNode(edge.getDestination());

        boolean added = adjacencyList.get(edge.getSource()).add(edge);
        if (added) {
            eventManager.publish(new GraphEvent.EdgeAdded(edge));
            log.info("Edge added successfully: {}", edge);
        } else {
            log.warn("Failed to add edge (already exists): {}", edge);
        }
        return added;
    }

    /**
     * Removes a node and all its edges
     *
     * @return true if node was removed, false if not found
     */
    public boolean removeNode(@NotNull @NonNull LocationNode node) {
        if (!adjacencyList.containsKey(node)) {
            log.warn("Failed to remove node (not found): {}", node);
            return false;
        }
        // Remove all edges containing this node
        adjacencyList.values().forEach(edges
                -> edges.removeIf(edge -> edge.getDestination().equals(node)));
        // Remove node and publish event
        Set<LocationEdge> removed = adjacencyList.remove(node);
        if (removed != null) {
            eventManager.publish(new GraphEvent.NodeRemoved(node));
            log.info("Node removed successfully: {}", node);
            return true;
        }
        log.warn("Failed to remove node (unexpected state): {}", node);
        return false;
    }

    /**
     * Removes an edge from the graph
     *
     * @return true if edge was removed, false if not found
     */
    public boolean removeEdge(@NotNull @NonNull LocationEdge edge) {
        Set<LocationEdge> edges = adjacencyList.get(edge.getSource());
        if (edges == null) {
            log.warn("Failed to remove edge (source node not found): {}", edge);
            return false;
        }
        boolean removed = edges.remove(edge);
        if (removed) {
            eventManager.publish(new GraphEvent.EdgeRemoved(edge));
            log.info("Edge removed successfully: {}", edge);
        } else {
            log.warn("Failed to remove edge (not found): {}", edge);
        }
        return removed;
    }

    /**
     * Gets all edges connected to a node
     */
    public Set<LocationEdge> getEdges(@NotNull @NonNull LocationNode node) {
        Set<LocationEdge> edges = Collections.unmodifiableSet(
                new HashSet<>(adjacencyList.getOrDefault(node, Set.of()))
        );
        log.info("Retrieved edges for node {}: {}", node, edges);
        return edges;
    }

    /**
     * Gets all nodes in the graph
     */
    public Set<LocationNode> getNodes() {
        Set<LocationNode> nodes = Collections.unmodifiableSet(
                new HashSet<>(adjacencyList.keySet())
        );
        log.info("Retrieved all nodes: {}", nodes);
        return nodes;
    }

    /**
     * Gets the total number of nodes
     */
    public int getNodeCount() {
        int nodeCount = adjacencyList.size();
        log.info("Total number of nodes: {}", nodeCount);
        return nodeCount;
    }

    /**
     * Gets the total number of edges
     */
    public int getEdgeCount() {
        int edgeCount = adjacencyList.values().stream()
                .mapToInt(Set::size)
                .sum();
        log.info("Total number of edges: {}", edgeCount);
        return edgeCount;
    }

    /**
     * Checks if node exists in graph
     */
    public boolean hasNode(@NotNull @NonNull LocationNode node) {
        boolean exists = adjacencyList.containsKey(node);
        log.info("Node {} exists: {}", node, exists);
        return exists;
    }

    /**
     * Checks if edge exists in graph
     */
    public boolean hasEdge(@NotNull @NonNull LocationEdge edge) {
        Set<LocationEdge> edges = adjacencyList.get(edge.getSource());
        boolean exists = edges != null && edges.contains(edge);
        log.info("Edge {} exists: {}", edge, exists);
        return exists;
    }

    /**
     * Clears all nodes and edges
     */
    /**
     * Clears all nodes and edges from the graph
     */
    public void clear() {
        adjacencyList.clear();
        eventManager.publish(GraphEvent.GraphCleared.INSTANCE);
        log.info("Graph cleared successfully");
    }

    /**
     * Subscribe to specific event type
     */
    public <T> void subscribe(Class<T> eventType, EventListener listener) {
        eventManager.subscribe(eventType, listener);
        log.debug("Subscribed {} to {}", listener, eventType.getSimpleName());
    }

    /**
     * Unsubscribe from specific event type
     */
    public <T> void unsubscribe(Class<T> eventType, EventListener listener) {
        eventManager.unsubscribe(eventType, listener);
        log.debug("Unsubscribed {} from {}", listener, eventType.getSimpleName());
    }

    /**
     * Shutdown event manager
     */
    public void shutdown() {
        eventManager.shutdown();
        log.info("Graph event manager shutdown");
    }

    // development Puproses only 
    // [ ] todo : Make A Good Coffee And Relaxed
    // - [ ] todo : Transaction , 
    // ? [ ] MARK Thread , Locks , Concurrency , Parallelism , Asynchronous , Synchronous , Blocking , Non-Blocking , Deadlocks , Starvation
    // ? [ ] MARK: - development Puproses only
    // ! [ ] MARK: - development Puproses only
    // print adjacency list
    /**
     * Print graph structure (for debugging)
     */
    public void printGraph() {
        log.info("Graph Structure:");
        adjacencyList.forEach((node, edges) -> {
            log.info("Node: {}", node);
            edges.forEach(edge -> log.info("  -> {}", edge));
        });
    }

    public void printAdjacencyList() {
        log.info("Adjacency List:");
        adjacencyList.forEach((node, edges) -> {
            log.info("Node: {}", node);
            edges.forEach(edge -> log.info("  -> {}", edge));
        });
        System.out.println("Adjacency List:");
        adjacencyList.forEach((node, edges) -> {
            System.out.println("Node: " + node);
            edges.forEach(edge -> System.out.println("  -> " + edge));
        });
    }

    // prints as adjacency matrix
    public void printAdjacencyMatrix() {
        StringBuilder matrix = new StringBuilder("Adjacency Matrix:\n");
        adjacencyList.forEach((node, edges) -> {
            matrix.append("Node: ").append(node).append("\n");
            adjacencyList.forEach((otherNode, otherEdges) -> {
                boolean connected = otherEdges.stream()
                        .anyMatch(edge -> edge.getDestination().equals(node));
                matrix.append("  -> ").append(otherNode).append(": ").append(connected).append("\n");
            });
        });
        log.info(matrix.toString());
        System.out.println(matrix.toString());
    }

    @Override
    public String toString() {
        return "LocationGraph{" + "nodes=" + getNodeCount() + ", edges=" + getEdgeCount() + '}';

    }

}
