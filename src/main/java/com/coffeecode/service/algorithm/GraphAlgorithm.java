package com.coffeecode.service.algorithm;

import java.util.List;
import java.util.function.Consumer;

import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

public interface GraphAlgorithm {

    /**
     * Executes pathfinding algorithm
     *
     * @param graph The graph to traverse
     * @param start Starting node
     * @param end Target node
     * @param onVisit Callback for visualization
     * @return Path from start to end
     */
    List<LocationNode> execute(
            LocationGraph graph,
            LocationNode start,
            LocationNode end,
            Consumer<LocationNode> onVisit
    );

}
