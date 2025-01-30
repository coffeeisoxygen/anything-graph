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
import com.coffeecode.model.Graph;
import com.coffeecode.model.Node;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BFS implements GraphAlgorithm {

    @Override
    public List<Node> execute(Graph graph, Node start, Node end, Consumer<Node> onVisit) {
        if (!graph.containsNode(start)) {
            throw new IllegalArgumentException("Start node not in graph");
        }

        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        Map<Node, Node> parentMap = new HashMap<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            onVisit.accept(current); // Trigger visualization

            if (end != null && current.equals(end)) {
                return reconstructPath(parentMap, start, end);
            }

            for (Node neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        return end == null ? new ArrayList<>(visited) : Collections.emptyList();
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
        return "Breadth-First Search";
    }
}
