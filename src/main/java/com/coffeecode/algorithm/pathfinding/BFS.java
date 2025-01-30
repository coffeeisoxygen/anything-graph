package com.coffeecode.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import com.coffeecode.algorithm.GraphAlgorithm;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BFS implements GraphAlgorithm {

    @Override
    public List<LocationNode> execute(LocationGraph graph, LocationNode start, LocationNode end, Consumer<LocationNode> onVisit) {
        if (!graph.containsNode(start)) {
            throw new IllegalArgumentException("Start node not in graph");
        }

        Queue<LocationNode> queue = new LinkedList<>();
        Set<LocationNode> visited = new HashSet<>();
        Map<LocationNode, LocationNode> parentMap = new HashMap<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            LocationNode current = queue.poll();
            onVisit.accept(current); // Trigger visualization

            if (end != null && current.equals(end)) {
                return reconstructPath(parentMap, start, end);
            }

            for (LocationNode neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        return end == null ? new ArrayList<>(visited) : Collections.emptyList();
    }

    private List<LocationNode> reconstructPath(Map<LocationNode, LocationNode> parentMap, LocationNode start, LocationNode end) {
        List<LocationNode> path = new ArrayList<>();
        LocationNode current = end;

        while (current != null) {
            path.add(0, current);
            current = parentMap.get(current);
        }

        return path;
    }

    @Override
    public String getName() {
        return "Breadth-First Search";
    }
}
