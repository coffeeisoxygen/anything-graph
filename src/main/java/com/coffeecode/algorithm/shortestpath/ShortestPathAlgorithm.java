package com.coffeecode.algorithm.shortestpath;

import java.util.List;

import com.coffeecode.algorithm.core.GraphAlgorithm;
import com.coffeecode.model.LocationNode;

public interface ShortestPathAlgorithm extends GraphAlgorithm {

    double getPathCost(List<LocationNode> path);

    boolean supportsNegativeWeights();
}
