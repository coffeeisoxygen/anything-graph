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
import com.coffeecode.model.Graph;
import com.coffeecode.model.Node;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DFS implements GraphAlgorithm {

    @Override
    public List<Node> execute(Graph graph, Node start, Node end, Consumer<Node> onVisit) {
        if (!graph.containsNode(start)) {
            throw new IllegalArgumentException("Start node not in graph");
        }

        Set<Node> visited = new HashSet<>();
        Map<Node, Node> parentMap = new HashMap<>();

        boolean found = dfs(graph, start, end, visited, parentMap, onVisit);

        if (end == null) {
            return new ArrayList<>(visited);
        }

        return found ? reconstructPath(parentMap, start, end) : Collections.emptyList();
    }

    private boolean dfs(Graph graph, Node current, Node end,
            Set<Node> visited, Map<Node, Node> parentMap,
            Consumer<Node> onVisit) {
        visited.add(current);
        onVisit.accept(current);

        if (end != null && current.equals(end)) {
            return true;
        }

        for (Node neighbor : graph.getNeighbors(current)) {
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

    private List<Node> reconstructPath(Map<Node, Node> parentMap, Node start, Node end) {
        List<Node> path = new ArrayList<>();
        Node current = end;

        while (current != null) {
            path.add(0, current);
            current = parentMap.get(current);
        }

        return path;
    }

    @Override
    public String getName() {
        return "Depth-First Search";
    }
}
