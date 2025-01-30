package com.coffeecode.graph.base;

public interface GraphCore<T> {

    // Core operations
    boolean addNode(T node);

    boolean removeNode(T node);

    boolean addEdge(T source, T target, double weight);

    boolean removeEdge(T source, T target);


}
