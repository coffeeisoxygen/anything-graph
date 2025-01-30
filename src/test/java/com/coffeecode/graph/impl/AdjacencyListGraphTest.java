package com.coffeecode.graph.impl;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Random;

import lombok.Data;

class AdjacencyListGraphTest {

    private AdjacencyListGraph<String> directedGraph;
    private AdjacencyListGraph<String> undirectedGraph;

    @BeforeEach
    void setUp() {
        directedGraph = new AdjacencyListGraph<>(true, true);
        undirectedGraph = new AdjacencyListGraph<>(false, true);
    }

    @Nested
    class NodeOperations {

        @Test
        void whenAddingNode_thenNodeExists() {
            assertThat(directedGraph.addNode("A")).isTrue();
            assertThat(directedGraph.containsNode("A")).isTrue();
        }

        @Test
        void whenAddingDuplicateNode_thenReturnsFalse() {
            directedGraph.addNode("A");
            assertThat(directedGraph.addNode("A")).isFalse();
        }

        @Test
        void whenRemovingNode_thenNodeAndEdgesRemoved() {
            directedGraph.addNode("A");
            directedGraph.addNode("B");
            directedGraph.addEdge("A", "B", 1.0);

            assertThat(directedGraph.removeNode("A")).isTrue();
            assertThat(directedGraph.containsNode("A")).isFalse();
            assertThat(directedGraph.containsEdge("A", "B")).isFalse();
        }
    }

    @Nested
    class EdgeOperations {

        @Test
        void whenAddingEdge_thenEdgeExists() {
            directedGraph.addNode("A");
            directedGraph.addNode("B");

            assertThat(directedGraph.addEdge("A", "B", 1.0)).isTrue();
            assertThat(directedGraph.containsEdge("A", "B")).isTrue();
        }

        @Test
        void whenAddingEdgeInUndirectedGraph_thenBothDirectionsExist() {
            undirectedGraph.addNode("A");
            undirectedGraph.addNode("B");
            undirectedGraph.addEdge("A", "B", 1.0);

            assertThat(undirectedGraph.containsEdge("A", "B")).isTrue();
            assertThat(undirectedGraph.containsEdge("B", "A")).isTrue();
        }

        @Test
        void whenRemovingEdge_thenEdgeDoesNotExist() {
            directedGraph.addNode("A");
            directedGraph.addNode("B");
            directedGraph.addEdge("A", "B", 1.0);

            assertThat(directedGraph.removeEdge("A", "B")).isTrue();
            assertThat(directedGraph.containsEdge("A", "B")).isFalse();
        }
    }

    @Nested
    class TraversalState {

        @Test
        void whenResettingTraversalState_thenAllNodesUnvisited() {
            directedGraph.addNode("A");
            directedGraph.setVisited("A", true);
            directedGraph.resetTraversalState();

            assertThat(directedGraph.isVisited("A")).isFalse();
        }

        @Test
        void whenSettingParent_thenParentIsSet() {
            directedGraph.addNode("A");
            directedGraph.addNode("B");
            directedGraph.setParent("B", "A");

            assertThat(directedGraph.getParent("B")).isEqualTo("A");
        }
    }

    @Nested
    class GraphProperties {

        @Test
        void whenCountingEdges_thenReturnsCorrectCount() {
            undirectedGraph.addNode("A");
            undirectedGraph.addNode("B");
            undirectedGraph.addNode("C");
            undirectedGraph.addEdge("A", "B", 1.0);
            undirectedGraph.addEdge("B", "C", 1.0);

            assertThat(undirectedGraph.getEdgeCount()).isEqualTo(2);
        }

        @Test
        void whenGettingNeighbors_thenReturnsCorrectNodes() {
            directedGraph.addNode("A");
            directedGraph.addNode("B");
            directedGraph.addNode("C");
            directedGraph.addEdge("A", "B", 1.0);
            directedGraph.addEdge("A", "C", 1.0);

            assertThat(directedGraph.getNeighbors("A"))
                    .containsExactlyInAnyOrder("B", "C");
        }
    }

    @Nested
    class CustomObjectOperations {

        @Data
        private static class TestNode {

            private final String id;
            private final String label;
        }

        private AdjacencyListGraph<TestNode> graph;
        private TestNode nodeA;
        private TestNode nodeB;

        @BeforeEach
        void setUp() {
            graph = new AdjacencyListGraph<>(false, true);
            nodeA = new TestNode("1", "A");
            nodeB = new TestNode("2", "B");
        }

        @Test
        void whenAddingCustomNodes_thenNodesExist() {
            assertThat(graph.addNode(nodeA)).isTrue();
            assertThat(graph.containsNode(nodeA)).isTrue();
        }

        @Test
        void whenAddingEdgesBetweenCustomNodes_thenEdgesExist() {
            graph.addNode(nodeA);
            graph.addNode(nodeB);
            graph.addEdge(nodeA, nodeB, 1.0);

            assertThat(graph.containsEdge(nodeA, nodeB)).isTrue();
            assertThat(graph.getEdgeWeight(nodeA, nodeB)).isEqualTo(1.0);
        }
    }

    @Nested
    class PerformanceTests {

        private AdjacencyListGraph<Integer> graph;
        private static final int LARGE_SIZE = 1000;

        @BeforeEach
        void setUp() {
            graph = new AdjacencyListGraph<>(false, true);
        }

        @Test
        @DisplayName("Performance: Adding many nodes")
        void performanceAddingNodes() {
            long startTime = System.nanoTime();

            for (int i = 0; i < LARGE_SIZE; i++) {
                graph.addNode(i);
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds

            assertThat(duration)
                    .as("Adding %d nodes took %d ms", LARGE_SIZE, duration)
                    .isLessThan(1000); // Should take less than 1 second
            assertThat(graph.getNodeCount()).isEqualTo(LARGE_SIZE);
        }

        @Test
        @DisplayName("Performance: Adding many edges")
        void performanceAddingEdges() {
            // Prepare nodes
            for (int i = 0; i < LARGE_SIZE; i++) {
                graph.addNode(i);
            }

            long startTime = System.nanoTime();

            // Connect each node to 10 random neighbors
            Random random = new Random(42); // Fixed seed for reproducibility
            for (int i = 0; i < LARGE_SIZE; i++) {
                for (int j = 0; j < 10; j++) {
                    int target = random.nextInt(LARGE_SIZE);
                    if (i != target) {
                        graph.addEdge(i, target, 1.0);
                    }
                }
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;

            assertThat(duration)
                    .as("Adding edges took %d ms", duration)
                    .isLessThan(2000); // Should take less than 2 seconds
        }

        @Test
        @DisplayName("Performance: Memory usage")
        void performanceMemoryUsage() {
            Runtime runtime = Runtime.getRuntime();
            runtime.gc(); // Request garbage collection
            long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();

            // Create large graph
            for (int i = 0; i < LARGE_SIZE; i++) {
                graph.addNode(i);
                for (int j = 0; j < 10; j++) {
                    if (i > j) {
                        graph.addEdge(i, j, 1.0);
                    }
                }
            }

            runtime.gc();
            long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = (usedMemoryAfter - usedMemoryBefore) / 1024; // KB

            assertThat(memoryUsed)
                    .as("Memory used: %d KB", memoryUsed)
                    .isLessThan(10 * 1024); // Should use less than 10MB
        }
    }
}
