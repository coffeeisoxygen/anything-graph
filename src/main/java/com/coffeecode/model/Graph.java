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
public class Graph {

    private final Map<Node, Set<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(@NotNull @NonNull Node node) {
        adjacencyList.putIfAbsent(node, new HashSet<>());
    }

    public void addEdge(@NotNull @NonNull Edge edge) {
        addNode(edge.getSource());
        addNode(edge.getDestination());
        adjacencyList.get(edge.getSource()).add(edge);
    }

    public Set<Edge> getEdges(@NotNull @NonNull Node node) {
        return adjacencyList.getOrDefault(node, new HashSet<>());
    }

    public Set<Node> getNodes() {
        return new HashSet<>(adjacencyList.keySet());
    }

    public boolean containsNode(@NotNull @NonNull Node node) {
        return adjacencyList.containsKey(node);
    }

    public void removeNode(@NotNull @NonNull Node node) {
        // Remove all edges containing this node
        adjacencyList.values().forEach(edges
                -> edges.removeIf(edge
                        -> edge.getDestination().equals(node)));
        // Remove the node and its edges
        adjacencyList.remove(node);
    }

    public void removeEdge(@NotNull @NonNull Edge edge) {
        Set<Edge> edges = adjacencyList.get(edge.getSource());
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
    public Set<Node> getNeighbors(@NotNull @NonNull Node node) {
        Set<Node> neighbors = new HashSet<>();
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
    public Optional<Double> getEdgeWeight(@NotNull @NonNull Node source,
            @NotNull @NonNull Node destination) {
        return getEdges(source).stream()
                .filter(e -> e.getDestination().equals(destination))
                .map(Edge::getWeight)
                .findFirst();
    }

    /**
     * Checks if the graph has an edge between two nodes
     *
     * @param source Source node
     * @param destination Destination node
     * @return true if edge exists
     */
    public boolean hasEdge(@NotNull @NonNull Node source,
            @NotNull @NonNull Node destination) {
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
