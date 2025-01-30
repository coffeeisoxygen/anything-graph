package com.coffeecode.graph.core;

import lombok.Data;
import lombok.Getter;
import java.util.*;

import com.coffeecode.graph.base.TraversableGraph;

/**
 * Abstract base implementation of Graph interface Provides common functionality
 * for different graph implementations
 */
public abstract class AbstractGraph<T> implements TraversableGraph<T> {

    @Getter
    protected final boolean directed;
    @Getter
    protected final boolean weighted;
    protected final Map<T, NodeState<T>> nodeStates;

    protected AbstractGraph(boolean directed, boolean weighted) {
        this.directed = directed;
        this.weighted = weighted;
        this.nodeStates = new HashMap<>();
    }

    @Data
    protected static class NodeState<T> {

        private boolean visited;
        private T parent;
        private double cost;
    }

    @Override
    public void resetTraversalState() {
        nodeStates.values().forEach(state -> {
            state.setVisited(false);
            state.setParent(null);
            state.setCost(Double.POSITIVE_INFINITY);
        });
    }

    // Common implementations for TraversableGraph methods
    @Override
    public boolean isVisited(T node) {
        return nodeStates.containsKey(node) && nodeStates.get(node).isVisited();
    }

    @Override
    public void setVisited(T node, boolean visited) {
        nodeStates.computeIfAbsent(node, k -> new NodeState<>()).setVisited(visited);
    }

    @Override
    public T getParent(T node) {
        return nodeStates.containsKey(node) ? nodeStates.get(node).getParent() : null;
    }

    @Override
    public void setParent(T node, T parent) {
        nodeStates.computeIfAbsent(node, k -> new NodeState<>()).setParent(parent);
    }

    @Override
    public double getCost(T node) {
        return nodeStates.containsKey(node) ? nodeStates.get(node).getCost() : Double.POSITIVE_INFINITY;
    }

    @Override
    public void setCost(T node, double cost) {
        nodeStates.computeIfAbsent(node, k -> new NodeState<>()).setCost(cost);
    }
}
