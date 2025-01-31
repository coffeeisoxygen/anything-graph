package com.coffeecode.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import com.coffeecode.event.state.GraphState;
import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DFS implements PathFindingAlgorithm {

    @Override
    public List<LocationNode> execute(LocationGraph graph,
            LocationNode start,
            LocationNode end,
            GraphState state) {
        validateInput(graph, start, end);
        state.clear();

        Stack<LocationNode> stack = new Stack<>();
        stack.push(start);
        state.markProcessing(start);

        while (!stack.isEmpty()) {
            LocationNode current = stack.pop();
            state.markVisited(current);

            if (current.equals(end)) {
                return reconstructPath(state, start, end);
            }

            for (LocationEdge edge : graph.getEdges(current)) {
                LocationNode neighbor = edge.getDestination();
                if (!state.isVisited(neighbor) && !state.isProcessing(neighbor)) {
                    stack.push(neighbor);
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
        return "Depth-First Search";
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public boolean isGuaranteedShortest() {
        return false;
    }
}
