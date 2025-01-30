package com.coffeecode.graph.base;

/**
 * Main Graph interface combining core operations, queries, and state management
 *
 * @param <T> Type of data stored in graph nodes
 */
public interface Graph<T> extends GraphCore<T>, GraphQuery<T>, GraphState<T> {
    // Additional methods specific to full graph functionality can be added here
}
