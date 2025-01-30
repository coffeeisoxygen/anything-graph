package com.coffeecode.graph.impl;

import lombok.Data;
import lombok.Builder;
import java.util.*;

@Data
@Builder
public class NodeData<T> {

    private final T data;
    @Builder.Default
    private final Map<T, Double> neighbors = new HashMap<>();
    private boolean visited;
    private T parent;
    private double cost;
}
