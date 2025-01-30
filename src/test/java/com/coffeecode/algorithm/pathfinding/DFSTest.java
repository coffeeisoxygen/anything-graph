package com.coffeecode.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.coffeecode.model.Edge;
import com.coffeecode.model.Graph;
import com.coffeecode.model.Node;

class DFSTest {

    private Graph graph;
    private DFS dfs;
    private Node nodeA, nodeB, nodeC, nodeD;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        dfs = new DFS();

        // Create a simple graph:
        //      A
        //     / \
        //    B   C
        //     \ /
        //      D
        nodeA = new Node("A");
        nodeB = new Node("B");
        nodeC = new Node("C");
        nodeD = new Node("D");

        graph.addEdge(new Edge(nodeA, nodeB));
        graph.addEdge(new Edge(nodeA, nodeC));
        graph.addEdge(new Edge(nodeB, nodeD));
        graph.addEdge(new Edge(nodeC, nodeD));
    }

    @Test
    void shouldFindPathToTarget() {
        List<Node> path = dfs.execute(graph, nodeA, nodeD, node -> {
        });

        assertThat(path).hasSize(3);
        assertThat(path.get(0)).isEqualTo(nodeA);
        assertThat(path.get(path.size() - 1)).isEqualTo(nodeD);
    }

    @Test
    void shouldPerformFullTraversal() {
        List<Node> visitedNodes = new ArrayList<>();
        dfs.execute(graph, nodeA, null, visitedNodes::add);

        assertThat(visitedNodes).hasSize(4);
        assertThat(visitedNodes).containsExactlyInAnyOrder(nodeA, nodeB, nodeC, nodeD);
    }

    @Test
    void shouldThrowExceptionForInvalidStartNode() {
        Node invalidNode = new Node("Invalid");

        assertThatThrownBy(()
                -> dfs.execute(graph, invalidNode, nodeD, node -> {
                }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start node not in graph");
    }

    @Test
    void shouldReturnEmptyListWhenNoPathExists() {
        Node nodeE = new Node("E");
        graph.addNode(nodeE); // Isolated node

        List<Node> path = dfs.execute(graph, nodeA, nodeE, node -> {
        });

        assertThat(path).isEmpty();
    }

    @Test
    void shouldHandleCyclicGraph() {
        graph.addEdge(new Edge(nodeD, nodeA)); // Create cycle
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
        Graph linearGraph = new Graph();
        Node n1 = new Node("1");
        Node n2 = new Node("2");
        Node n3 = new Node("3");
        linearGraph.addEdge(new Edge(n1, n2));
        linearGraph.addEdge(new Edge(n2, n3));

        List<Node> path = dfs.execute(linearGraph, n1, n3, node -> {
        });

        assertThat(path).containsExactly(n1, n2, n3);
    }

    @Test
    void shouldHandleBidirectionalEdges() {
        graph.addEdge(new Edge(nodeB, nodeA)); // Add reverse edge
        graph.addEdge(new Edge(nodeC, nodeA));

        List<String> visited = new ArrayList<>();
        dfs.execute(graph, nodeA, null, node -> visited.add(node.getId()));

        assertThat(visited).hasSize(4)
                .containsExactlyInAnyOrder("A", "B", "C", "D");
    }
}
