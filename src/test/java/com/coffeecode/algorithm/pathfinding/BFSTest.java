package com.coffeecode.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.coffeecode.model.Edge;
import com.coffeecode.model.Graph;
import com.coffeecode.model.Node;

class BFSTest {

    private Graph graph;
    private BFS bfs;
    private Node nodeA, nodeB, nodeC;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        bfs = new BFS();

        nodeA = new Node("A");
        nodeB = new Node("B");
        nodeC = new Node("C");

        graph.addEdge(new Edge(nodeA, nodeB));
        graph.addEdge(new Edge(nodeB, nodeC));
    }

    @Test
    void shouldFindPathBetweenNodes() {
        List<Node> path = bfs.execute(graph, nodeA, nodeC, node -> {
        });

        assertThat(path)
                .containsExactly(nodeA, nodeB, nodeC);
    }

    @Test
    void shouldVisitNodesInBreadthFirstOrder() {
        AtomicInteger visitCount = new AtomicInteger();

        bfs.execute(graph, nodeA, null, node
                -> visitCount.incrementAndGet());

        assertThat(visitCount.get()).isEqualTo(3);
    }

    @Test
    void shouldThrowExceptionForInvalidStartNode() {
        Node invalidNode = new Node("Invalid");

        assertThatThrownBy(()
                -> bfs.execute(graph, invalidNode, nodeC, node -> {
                }))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleCyclicGraph() {
        graph.addEdge(new Edge(nodeC, nodeA)); // Create cycle A -> B -> C -> A
        List<String> visited = new ArrayList<>();

        bfs.execute(graph, nodeA, null, node -> visited.add(node.getId()));

        assertThat(visited).hasSize(3)
                .containsExactly("A", "B", "C");
    }

    @Test
    void shouldHandleEmptyGraph() {
        Graph emptyGraph = new Graph();
        Node start = new Node("Start");
        emptyGraph.addNode(start);

        List<Node> result = bfs.execute(emptyGraph, start, null, node -> {
        });

        assertThat(result).containsOnly(start);
    }

    @Test
    void shouldFollowBreadthFirstOrder() {
        // Create diamond shape: A -> B,C -> D
        Node nodeD = new Node("D");
        graph.addEdge(new Edge(nodeB, nodeD));
        graph.addEdge(new Edge(nodeC, nodeD));
        graph.addEdge(new Edge(nodeA, nodeC));

        List<String> visited = new ArrayList<>();
        bfs.execute(graph, nodeA, null, node -> visited.add(node.getId()));

        // Should visit level by level
        assertThat(visited).containsExactly("A", "B", "C", "D");
    }

    @Test
    void shouldHandleDisconnectedGraph() {
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        graph.addEdge(new Edge(nodeD, nodeE));

        List<Node> result = bfs.execute(graph, nodeA, null, node -> {
        });

        assertThat(result).containsExactly(nodeA, nodeB, nodeC);
    }
}
