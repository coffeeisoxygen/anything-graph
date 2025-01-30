package com.coffeecode.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphTest {

    private LocationGraph graph;
    private LocationNode nodeA;
    private LocationNode nodeB;
    private LocationEdge edge;

    @BeforeEach
    void setUp() {
        graph = new LocationGraph();
        nodeA = new LocationNode("A", 0.0, 0.0);
        nodeB = new LocationNode("B", 1.0, 1.0);
        edge = new LocationEdge(nodeA, nodeB, 10.0);
    }

    @Test
    void shouldGetNeighbors() {
        graph.addEdge(edge);
        assertThat(graph.getNeighbors(nodeA))
                .containsExactly(nodeB);
    }

    @Test
    void shouldGetEdgeWeight() {
        graph.addEdge(edge);
        assertThat(graph.getEdgeWeight(nodeA, nodeB))
                .contains(10.0);
    }

    @Test
    void shouldCheckEdgeExists() {
        graph.addEdge(edge);
        assertThat(graph.hasEdge(nodeA, nodeB)).isTrue();
        assertThat(graph.hasEdge(nodeB, nodeA)).isFalse();
    }

    @Test
    void shouldCountEdgesAndNodes() {
        graph.addEdge(edge);
        assertThat(graph.getEdgeCount()).isEqualTo(1);
        assertThat(graph.getNodeCount()).isEqualTo(2);
    }

    @Test
    void shouldClearGraph() {
        graph.addEdge(edge);
        graph.clear();
        assertThat(graph.getNodeCount()).isZero();
        assertThat(graph.getEdgeCount()).isZero();
    }
}
