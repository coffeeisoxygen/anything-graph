package com.coffeecode.graph.base;

public interface GraphState<T> {

    // State operations
    void clear();

    boolean isDirected();

    boolean isWeighted();

    int getNodeCount();

    int getEdgeCount();

}
