package com.coffeecode.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

class BFSTest {

    private LocationGraph graph;
    private BFS bfs;
    private LocationNode nodeA, nodeB, nodeC;

    @BeforeEach
    void setUp() {
        graph = new LocationGraph();
        bfs = new BFS();

        nodeA = new LocationNode("A");
        nodeB = new LocationNode("B");
        nodeC = new LocationNode("C");

        graph.addEdge(new LocationEdge(nodeA, nodeB));
        graph.addEdge(new LocationEdge(nodeB, nodeC));
    }

    @Test
    void shouldFindPathBetweenNodes() {
        List<LocationNode> path = bfs.execute(graph, nodeA, nodeC, node -> {
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
        LocationNode invalidNode = new LocationNode("Invalid");

        assertThatThrownBy(()
                -> bfs.execute(graph, invalidNode, nodeC, node -> {
                }))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleCyclicGraph() {
        graph.addEdge(new LocationEdge(nodeC, nodeA)); // Create cycle A -> B -> C -> A
        List<String> visited = new ArrayList<>();

        bfs.execute(graph, nodeA, null, node -> visited.add(node.getId()));

        assertThat(visited).hasSize(3)
                .containsExactly("A", "B", "C");
    }

    @Test
    void shouldHandleEmptyGraph() {
        LocationGraph emptyGraph = new LocationGraph();
        LocationNode start = new LocationNode("Start");
        emptyGraph.addNode(start);

        List<LocationNode> result = bfs.execute(emptyGraph, start, null, node -> {
        });

        assertThat(result).containsOnly(start);
    }

    @Test
    void shouldFollowBreadthFirstOrder() {
        // Create diamond shape: A -> B,C -> D
        LocationNode nodeD = new LocationNode("D");
        graph.addEdge(new LocationEdge(nodeB, nodeD));
        graph.addEdge(new LocationEdge(nodeC, nodeD));
        graph.addEdge(new LocationEdge(nodeA, nodeC));

        List<String> visited = new ArrayList<>();
        bfs.execute(graph, nodeA, null, node -> visited.add(node.getId()));

        // Should visit level by level
        assertThat(visited).containsExactly("A", "B", "C", "D");
    }

    @Test
    void shouldHandleDisconnectedGraph() {
        LocationNode nodeD = new LocationNode("D");
        LocationNode nodeE = new LocationNode("E");
        graph.addEdge(new LocationEdge(nodeD, nodeE));

        List<LocationNode> result = bfs.execute(graph, nodeA, null, node -> {
        });

        assertThat(result).containsExactly(nodeA, nodeB, nodeC);
    }
}
