package com.coffeecode.graph.impl;

import com.coffeecode.graph.base.TraversableGraph;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
public class AdjacencyMatrixGraph<T> implements TraversableGraph<T> {

    private static final double NO_EDGE = Double.POSITIVE_INFINITY;
    private final List<T> nodes;  // Store nodes in order
    private double[][] matrix;    // Adjacency matrix
    private final Map<T, Integer> nodeIndices; // Map nodes to matrix indices
    private final Map<T, NodeState<T>> nodeStates; // Store traversal state
    @Getter
    private final boolean directed;
    @Getter
    private final boolean weighted;

    private static class NodeState<T> {

        private boolean visited;
        private T parent;
        private double cost;
    }

    public AdjacencyMatrixGraph(boolean directed, boolean weighted) {
        this.nodes = new ArrayList<>();
        this.matrix = new double[0][0];
        this.nodeIndices = new HashMap<>();
        this.nodeStates = new HashMap<>();
        this.directed = directed;
        this.weighted = weighted;
    }

    @Override
    public boolean addNode(T node) {
        if (node == null || containsNode(node)) {
            return false;
        }

        // Expand matrix
        int newSize = nodes.size() + 1;
        double[][] newMatrix = new double[newSize][newSize];

        // Copy existing matrix
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }

        // Initialize new row and column
        for (int i = 0; i < newSize; i++) {
            newMatrix[i][newSize - 1] = NO_EDGE;
            newMatrix[newSize - 1][i] = NO_EDGE;
        }

        matrix = newMatrix;
        nodeIndices.put(node, nodes.size());
        nodes.add(node);
        nodeStates.put(node, new NodeState<>());

        log.debug("Added node: {}", node);
        return true;
    }

    @Override
    public boolean addEdge(T source, T target, double weight) {
        if (!containsNode(source) || !containsNode(target)) {
            return false;
        }

        int sourceIndex = nodeIndices.get(source);
        int targetIndex = nodeIndices.get(target);

        matrix[sourceIndex][targetIndex] = weight;
        if (!directed) {
            matrix[targetIndex][sourceIndex] = weight;
        }

        log.debug("Added edge: {} -> {} (weight: {})", source, target, weight);
        return true;
    }

    @Override
    public boolean removeNode(T node) {
        if (!containsNode(node)) {
            return false;
        }

        int index = nodeIndices.get(node);
        int size = nodes.size();

        // Create new matrix excluding the node
        double[][] newMatrix = new double[size - 1][size - 1];
        for (int i = 0, newI = 0; i < size; i++) {
            if (i == index) {
                continue;
            }
            for (int j = 0, newJ = 0; j < size; j++) {
                if (j == index) {
                    continue;
                }
                newMatrix[newI][newJ] = matrix[i][j];
                newJ++;
            }
            newI++;
        }

        // Update indices for remaining nodes
        nodes.remove(index);
        nodeStates.remove(node);
        nodeIndices.clear();
        for (int i = 0; i < nodes.size(); i++) {
            nodeIndices.put(nodes.get(i), i);
        }

        matrix = newMatrix;
        log.debug("Removed node: {}", node);
        return true;
    }

    @Override
    public boolean removeEdge(T source, T target) {
        if (!containsEdge(source, target)) {
            return false;
        }

        int sourceIndex = nodeIndices.get(source);
        int targetIndex = nodeIndices.get(target);

        matrix[sourceIndex][targetIndex] = NO_EDGE;
        if (!directed) {
            matrix[targetIndex][sourceIndex] = NO_EDGE;
        }

        log.debug("Removed edge: {} -> {}", source, target);
        return true;
    }

    @Override
    public boolean containsNode(T node) {
        return nodeIndices.containsKey(node);
    }

    @Override
    public boolean containsEdge(T source, T target) {
        if (!containsNode(source) || !containsNode(target)) {
            return false;
        }
        int sourceIndex = nodeIndices.get(source);
        int targetIndex = nodeIndices.get(target);
        return matrix[sourceIndex][targetIndex] != NO_EDGE;
    }

    @Override
    public double getEdgeWeight(T source, T target) {
        if (!containsNode(source) || !containsNode(target)) {
            return NO_EDGE;
        }
        return matrix[nodeIndices.get(source)][nodeIndices.get(target)];
    }

    @Override
    public Iterable<T> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    @Override
    public Iterable<T> getNeighbors(T node) {
        if (!containsNode(node)) {
            return Collections.emptyList();
        }

        List<T> neighbors = new ArrayList<>();
        int nodeIndex = nodeIndices.get(node);

        for (int i = 0; i < nodes.size(); i++) {
            if (matrix[nodeIndex][i] != NO_EDGE) {
                neighbors.add(nodes.get(i));
            }
        }

        return Collections.unmodifiableList(neighbors);
    }

    @Override
    public void clear() {
        nodes.clear();
        nodeIndices.clear();
        nodeStates.clear();
        matrix = new double[0][0];
        log.debug("Graph cleared");
    }

    @Override
    public int getNodeCount() {
        return nodes.size();
    }

    @Override
    public int getEdgeCount() {
        int count = 0;
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (matrix[i][j] != NO_EDGE) {
                    count++;
                }
            }
        }
        return directed ? count : count / 2;
    }

    // TraversableGraph implementation
    @Override
    public void resetTraversalState() {
        nodeStates.values().forEach(state -> {
            state.visited = false;
            state.parent = null;
            state.cost = Double.POSITIVE_INFINITY;
        });
        log.debug("Reset traversal state");
    }

    @Override
    public boolean isVisited(T node) {
        return nodeStates.containsKey(node) && nodeStates.get(node).visited;
    }

    @Override
    public void setVisited(T node, boolean visited) {
        if (containsNode(node)) {
            nodeStates.get(node).visited = visited;
        }
    }

    @Override
    public T getParent(T node) {
        return nodeStates.containsKey(node) ? nodeStates.get(node).parent : null;
    }

    @Override
    public void setParent(T node, T parent) {
        if (containsNode(node)) {
            nodeStates.get(node).parent = parent;
        }
    }

    @Override
    public double getCost(T node) {
        return nodeStates.containsKey(node) ? nodeStates.get(node).cost : Double.POSITIVE_INFINITY;
    }

    @Override
    public void setCost(T node, double cost) {
        if (containsNode(node)) {
            nodeStates.get(node).cost = cost;
        }
    }
}
