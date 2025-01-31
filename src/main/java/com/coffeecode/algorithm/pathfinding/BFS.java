package com.coffeecode.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.coffeecode.event.state.GraphState;
import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BFS implements PathFindingAlgorithm {

    @Override
    public List<LocationNode> execute(LocationGraph graph,
            LocationNode start,
            LocationNode end,
            GraphState state) {
        validateInput(graph, start, end);
        state.clear();

        Queue<LocationNode> queue = new LinkedList<>();
        queue.offer(start);
        state.markProcessing(start);

        while (!queue.isEmpty()) {
            LocationNode current = queue.poll();
            state.markVisited(current);

            if (current.equals(end)) {
                return reconstructPath(state, start, end);
            }

            for (LocationEdge edge : graph.getEdges(current)) {
                LocationNode neighbor = edge.getDestination();
                if (!state.isVisited(neighbor) && !state.isProcessing(neighbor)) {
                    queue.offer(neighbor);
                    state.updateParent(neighbor, current);
                    state.markProcessing(neighbor);
                    state.markProcessing(edge);
                }
            }
        }

        return Collections.emptyList();
    }

    private List<LocationNode> reconstructPath(GraphState state,
            LocationNode start,
            LocationNode end) {
        List<LocationNode> path = new ArrayList<>();
        LocationNode current = end;

        while (current != null) {
            path.add(0, current);
            current = state.getParentNode(current);
        }

        return path;
    }

    @Override
    public String getName() {
        return "Breadth-First Search";
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public boolean isGuaranteedShortest() {
        return true; // BFS guarantees shortest path in unweighted graphs
    }
}
