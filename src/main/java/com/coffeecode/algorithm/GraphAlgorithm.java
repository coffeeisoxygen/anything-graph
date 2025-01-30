package com.coffeecode.algorithm;

import java.util.List;
import java.util.function.Consumer;

import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

public interface GraphAlgorithm {

    /**
     * Executes the graph algorithm
     *
     * @param graph The graph to traverse
     * @param start Starting node
     * @param end Target node (optional, can be null for full traversal)
     * @param onVisit Callback for visualization updates
     * @return Path found by the algorithm
     */
    List<LocationNode> execute(LocationGraph graph, LocationNode start, LocationNode end, Consumer<LocationNode> onVisit);

    /**
     * Gets the algorithm's name for display
     *
     * @return Algorithm name
     */
    String getName();
}
