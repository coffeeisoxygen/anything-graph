package com.coffeecode.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

class DFSTest {

    private LocationGraph graph;
    private DFS dfs;
    private LocationNode nodeA, nodeB, nodeC, nodeD;

    @BeforeEach
    void setUp() {
        graph = new LocationGraph();
        dfs = new DFS();

        // Create a simple graph:
        //      A
        //     / \
        //    B   C
        //     \ /
        //      D
        nodeA = new LocationNode("A");
        nodeB = new LocationNode("B");
        nodeC = new LocationNode("C");
        nodeD = new LocationNode("D");

        graph.addEdge(new LocationEdge(nodeA, nodeB));
        graph.addEdge(new LocationEdge(nodeA, nodeC));
        graph.addEdge(new LocationEdge(nodeB, nodeD));
        graph.addEdge(new LocationEdge(nodeC, nodeD));
    }

    @Test
    void shouldFindPathToTarget() {
        List<LocationNode> path = dfs.execute(graph, nodeA, nodeD, node -> {
        });

        assertThat(path).hasSize(3);
        assertThat(path.get(0)).isEqualTo(nodeA);
        assertThat(path.get(path.size() - 1)).isEqualTo(nodeD);
    }

    @Test
    void shouldPerformFullTraversal() {
        List<LocationNode> visitedNodes = new ArrayList<>();
        dfs.execute(graph, nodeA, null, visitedNodes::add);

        assertThat(visitedNodes).hasSize(4);
        assertThat(visitedNodes).containsExactlyInAnyOrder(nodeA, nodeB, nodeC, nodeD);
    }

    @Test
    void shouldThrowExceptionForInvalidStartNode() {
        LocationNode invalidNode = new LocationNode("Invalid");

        assertThatThrownBy(()
                -> dfs.execute(graph, invalidNode, nodeD, node -> {
                }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start node not in graph");
    }

    @Test
    void shouldReturnEmptyListWhenNoPathExists() {
        LocationNode nodeE = new LocationNode("E");
        graph.addNode(nodeE); // Isolated node

        List<LocationNode> path = dfs.execute(graph, nodeA, nodeE, node -> {
        });

        assertThat(path).isEmpty();
    }

    @Test
    void shouldHandleCyclicGraph() {
        graph.addEdge(new LocationEdge(nodeD, nodeA)); // Create cycle
        List<String> visited = new ArrayList<>();

        dfs.execute(graph, nodeA, null, node -> visited.add(node.getId()));

        assertThat(visited).hasSize(4)
                .containsExactlyInAnyOrder("A", "B", "C", "D");
    }

    @Test
    void shouldFollowDepthFirstOrder() {
        List<String> visited = new ArrayList<>();
        dfs.execute(graph, nodeA, null, node -> visited.add(node.getId()));

        // Should follow one path to completion before backtracking
        assertThat(visited).containsExactly("A", "B", "D", "C");
    }

    @Test
    void shouldHandleLinearGraph() {
        LocationGraph linearGraph = new LocationGraph();
        LocationNode n1 = new LocationNode("1");
        LocationNode n2 = new LocationNode("2");
        LocationNode n3 = new LocationNode("3");
        linearGraph.addEdge(new LocationEdge(n1, n2));
        linearGraph.addEdge(new LocationEdge(n2, n3));

        List<LocationNode> path = dfs.execute(linearGraph, n1, n3, node -> {
        });

        assertThat(path).containsExactly(n1, n2, n3);
    }

    @Test
    void shouldHandleBidirectionalEdges() {
        graph.addEdge(new LocationEdge(nodeB, nodeA)); // Add reverse edge
        graph.addEdge(new LocationEdge(nodeC, nodeA));

        List<String> visited = new ArrayList<>();
        dfs.execute(graph, nodeA, null, node -> visited.add(node.getId()));

        assertThat(visited).hasSize(4)
                .containsExactlyInAnyOrder("A", "B", "C", "D");
    }
}
