package com.coffeecode.graph.impl;

import com.coffeecode.graph.base.TraversableGraph;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
public class AdjacencyListGraph<T> implements TraversableGraph<T> {

    // Store node data with adjacency information
    private final Map<T, NodeData<T>> nodes;

    // Graph properties
    @Getter
    private final boolean directed;
    @Getter
    private final boolean weighted;

    public AdjacencyListGraph(boolean directed, boolean weighted) {
        this.nodes = new HashMap<>();
        this.directed = directed;
        this.weighted = weighted;
    }

    @Override
    public boolean addNode(T node) {
        if (node == null || containsNode(node)) {
            return false;
        }
        nodes.put(node, NodeData.<T>builder().data(node).build());
        log.debug("Added node: {}", node);
        return true;
    }

    @Override
    public boolean addEdge(T source, T target, double weight) {
        if (!containsNode(source) || !containsNode(target)) {
            return false;
        }

        NodeData<T> sourceData = nodes.get(source);
        sourceData.getNeighbors().put(target, weight);

        if (!directed) {
            NodeData<T> targetData = nodes.get(target);
            targetData.getNeighbors().put(source, weight);
        }

        log.debug("Added edge: {} -> {} (weight: {})", source, target, weight);
        return true;
    }

    @Override
    public boolean removeNode(T node) {
        if (!containsNode(node)) {
            return false;
        }

        // Remove all edges pointing to this node
        nodes.values().forEach(data
                -> data.getNeighbors().remove(node));

        nodes.remove(node);
        log.debug("Removed node: {}", node);
        return true;
    }

    @Override
    public boolean removeEdge(T source, T target) {
        if (!containsEdge(source, target)) {
            return false;
        }

        nodes.get(source).getNeighbors().remove(target);
        if (!directed) {
            nodes.get(target).getNeighbors().remove(source);
        }

        log.debug("Removed edge: {} -> {}", source, target);
        return true;
    }

    @Override
    public boolean containsNode(T node) {
        return nodes.containsKey(node);
    }

    @Override
    public boolean containsEdge(T source, T target) {
        return containsNode(source)
                && nodes.get(source).getNeighbors().containsKey(target);
    }

    @Override
    public double getEdgeWeight(T source, T target) {
        if (!containsEdge(source, target)) {
            return Double.POSITIVE_INFINITY;
        }
        return nodes.get(source).getNeighbors().get(target);
    }

    @Override
    public Iterable<T> getNodes() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    @Override
    public Iterable<T> getNeighbors(T node) {
        if (!containsNode(node)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableSet(
                nodes.get(node).getNeighbors().keySet());
    }

    @Override
    public void clear() {
        nodes.clear();
        log.debug("Graph cleared");
    }

    @Override
    public int getNodeCount() {
        return nodes.size();
    }

    @Override
    public int getEdgeCount() {
        return nodes.values().stream()
                .mapToInt(data -> data.getNeighbors().size())
                .sum() / (directed ? 1 : 2);
    }

    // TraversableGraph implementation
    @Override
    public void resetTraversalState() {
        nodes.values().forEach(data -> {
            data.setVisited(false);
            data.setParent(null);
            data.setCost(Double.POSITIVE_INFINITY);
        });
        log.debug("Reset traversal state");
    }

    @Override
    public boolean isVisited(T node) {
        return containsNode(node) && nodes.get(node).isVisited();
    }

    @Override
    public void setVisited(T node, boolean visited) {
        if (containsNode(node)) {
            nodes.get(node).setVisited(visited);
        }
    }

    @Override
    public T getParent(T node) {
        return containsNode(node) ? nodes.get(node).getParent() : null;
    }

    @Override
    public void setParent(T node, T parent) {
        if (containsNode(node)) {
            nodes.get(node).setParent(parent);
        }
    }

    @Override
    public double getCost(T node) {
        return containsNode(node) ? nodes.get(node).getCost() : Double.POSITIVE_INFINITY;
    }

    @Override
    public void setCost(T node, double cost) {
        if (containsNode(node)) {
            nodes.get(node).setCost(cost);
        }
    }
}
