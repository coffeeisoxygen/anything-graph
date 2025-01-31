package com.coffeecode.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.coffeecode.algorithm.GraphAlgorithm;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DFS implements GraphAlgorithm {

    @Override
    public List<LocationNode> execute(LocationGraph graph, LocationNode start, LocationNode end, Consumer<LocationNode> onVisit) {
        if (!graph.containsNode(start)) {
            throw new IllegalArgumentException("Start node not in graph");
        }

        Set<LocationNode> visited = new HashSet<>();
        Map<LocationNode, LocationNode> parentMap = new HashMap<>();

        boolean found = dfs(graph, start, end, visited, parentMap, onVisit);

        if (end == null) {
            return new ArrayList<>(visited);
        }

        return found ? reconstructPath(parentMap, start, end) : Collections.emptyList();
    }

    private boolean dfs(LocationGraph graph, LocationNode current, LocationNode end,
            Set<LocationNode> visited, Map<LocationNode, LocationNode> parentMap,
            Consumer<LocationNode> onVisit) {
        visited.add(current);
        onVisit.accept(current);

        if (end != null && current.equals(end)) {
            return true;
        }

        for (LocationNode neighbor : graph.getNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                parentMap.put(neighbor, current);
                if (end != null && dfs(graph, neighbor, end, visited, parentMap, onVisit)) {
                    return true;
                } else if (end == null) {
                    dfs(graph, neighbor, null, visited, parentMap, onVisit);
                }
            }
        }

        return false;
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

    public String getName() {
        return "Depth-First Search";
    }
}
