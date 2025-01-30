package com.coffeecode.graph.base;

public interface GraphQuery<T> {

    // Query operations
    boolean containsNode(T node);

    boolean containsEdge(T source, T target);

    double getEdgeWeight(T source, T target);

    Iterable<T> getNodes();

    Iterable<T> getNeighbors(T node);

}
