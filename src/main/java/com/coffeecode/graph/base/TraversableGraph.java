package com.coffeecode.graph.base;

/**
 * Extends Graph with traversal state management for algorithms
 *
 * @param <T> Type of data stored in graph nodes
 */
public interface TraversableGraph<T> extends Graph<T> {

    void resetTraversalState();

    boolean isVisited(T node);

    void setVisited(T node, boolean visited);

    T getParent(T node);

    void setParent(T node, T parent);

    double getCost(T node);

    void setCost(T node, double cost);
}
