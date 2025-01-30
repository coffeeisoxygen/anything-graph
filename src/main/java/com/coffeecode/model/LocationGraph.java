package com.coffeecode.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class LocationGraph {

    private final Map<LocationNode, Set<LocationEdge>> adjacencyList;

    public LocationGraph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(@NotNull @NonNull LocationNode node) {
        adjacencyList.putIfAbsent(node, new HashSet<>());
    }

    public void addEdge(@NotNull @NonNull LocationEdge edge) {
        addNode(edge.getSource());
        addNode(edge.getDestination());
        adjacencyList.get(edge.getSource()).add(edge);
    }

    public Set<LocationEdge> getEdges(@NotNull @NonNull LocationNode node) {
        return adjacencyList.getOrDefault(node, new HashSet<>());
    }

    public Set<LocationNode> getNodes() {
        return new HashSet<>(adjacencyList.keySet());
    }

    public boolean containsNode(@NotNull @NonNull LocationNode node) {
        return adjacencyList.containsKey(node);
    }

    public void removeNode(@NotNull @NonNull LocationNode node) {
        // Remove all edges containing this node
        adjacencyList.values().forEach(edges
                -> edges.removeIf(edge
                        -> edge.getDestination().equals(node)));
        // Remove the node and its edges
        adjacencyList.remove(node);
    }

    public void removeEdge(@NotNull @NonNull LocationEdge edge) {
        Set<LocationEdge> edges = adjacencyList.get(edge.getSource());
        if (edges != null) {
            edges.remove(edge);
        }
    }

    /**
     * Gets all neighboring nodes for a given node
     *
     * @param node The source node
     * @return Set of adjacent nodes
     */
    public Set<LocationNode> getNeighbors(@NotNull @NonNull LocationNode node) {
        Set<LocationNode> neighbors = new HashSet<>();
        getEdges(node).forEach(edge -> neighbors.add(edge.getDestination()));
        return neighbors;
    }

    /**
     * Gets the edge weight between two nodes if it exists
     *
     * @param source Source node
     * @param destination Destination node
     * @return Optional containing the edge weight if exists
     */
    public Optional<Double> getEdgeWeight(@NotNull @NonNull LocationNode source,
            @NotNull @NonNull LocationNode destination) {
        return getEdges(source).stream()
                .filter(e -> e.getDestination().equals(destination))
                .map(LocationEdge::getWeight)
                .findFirst();
    }

    /**
     * Checks if the graph has an edge between two nodes
     *
     * @param source Source node
     * @param destination Destination node
     * @return true if edge exists
     */
    public boolean hasEdge(@NotNull @NonNull LocationNode source,
            @NotNull @NonNull LocationNode destination) {
        return getEdges(source).stream()
                .anyMatch(e -> e.getDestination().equals(destination));
    }

    /**
     * Gets the total number of edges in the graph
     *
     * @return edge count
     */
    public int getEdgeCount() {
        return adjacencyList.values().stream()
                .mapToInt(Set::size)
                .sum();
    }

    /**
     * Gets the total number of nodes in the graph
     *
     * @return node count
     */
    public int getNodeCount() {
        return adjacencyList.size();
    }

    /**
     * Clears all nodes and edges from the graph
     */
    public void clear() {
        adjacencyList.clear();
    }
}
