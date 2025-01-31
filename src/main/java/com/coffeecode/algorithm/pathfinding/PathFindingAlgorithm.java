package com.coffeecode.algorithm.pathfinding;

import com.coffeecode.algorithm.core.GraphAlgorithm;

public interface PathFindingAlgorithm extends GraphAlgorithm {

    boolean isComplete(); // Returns true if algorithm visits all nodes

    boolean isGuaranteedShortest(); // Returns true if finds shortest path
}
