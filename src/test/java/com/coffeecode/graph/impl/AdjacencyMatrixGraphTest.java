package com.coffeecode.graph.impl;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;
import java.util.Random;

class AdjacencyMatrixGraphTest {

    private AdjacencyMatrixGraph<String> graph;

    @BeforeEach
    void setUp() {
        // Create directed and weighted graph for tests
        graph = new AdjacencyMatrixGraph<>(true, true);
    }

    @Nested
    class BasicOperations {

        @Test
        void whenAddingNode_thenNodeExists() {
            assertThat(graph.addNode("A")).isTrue();
            assertThat(graph.containsNode("A")).isTrue();
            assertThat(graph.getNodeCount()).isEqualTo(1);
        }

        @Test
        void whenAddingDuplicateNode_thenReturnsFalse() {
            graph.addNode("A");
            assertThat(graph.addNode("A")).isFalse();
        }

        @Test
        void whenAddingNullNode_thenReturnsFalse() {
            assertThat(graph.addNode(null)).isFalse();
        }

        @Test
        void whenAddingEdge_thenEdgeExists() {
            graph.addNode("A");
            graph.addNode("B");

            assertThat(graph.addEdge("A", "B", 1.0)).isTrue();
            assertThat(graph.containsEdge("A", "B")).isTrue();
            assertThat(graph.getEdgeWeight("A", "B")).isEqualTo(1.0);
        }

        @Test
        void whenRemovingNode_thenEdgesAreRemoved() {
            graph.addNode("A");
            graph.addNode("B");
            graph.addEdge("A", "B", 1.0);

            assertThat(graph.removeNode("A")).isTrue();
            assertThat(graph.containsNode("A")).isFalse();
            assertThat(graph.containsEdge("A", "B")).isFalse();
        }
    }

    @Nested
    class MatrixSpecificTests {

        @Test
        void matrixSizeMatchesNodeCount() {
            graph.addNode("A");
            graph.addNode("B");
            graph.addNode("C");

            assertThat(graph.getNodeCount()).isEqualTo(3);
            // Test internal matrix size through edge operations
            assertThat(graph.addEdge("A", "B", 1.0)).isTrue();
            assertThat(graph.addEdge("B", "C", 1.0)).isTrue();
            assertThat(graph.addEdge("C", "A", 1.0)).isTrue();
        }

        @Test
        void edgeWeightsAreCorrectlyStored() {
            graph.addNode("A");
            graph.addNode("B");

            graph.addEdge("A", "B", 2.5);
            assertThat(graph.getEdgeWeight("A", "B")).isEqualTo(2.5);
        }
    }

    @Nested
    class EdgeOperations {

        @BeforeEach
        void setUpEdgeTest() {
            graph.addNode("A");
            graph.addNode("B");
        }

        @Test
        void whenRemovingEdge_thenEdgeNoLongerExists() {
            graph.addEdge("A", "B", 1.0);

            assertThat(graph.removeEdge("A", "B")).isTrue();
            assertThat(graph.containsEdge("A", "B")).isFalse();
        }

        @Test
        void whenAddingEdgeInUndirectedGraph_thenBothDirectionsExist() {
            AdjacencyMatrixGraph<String> undirectedGraph
                    = new AdjacencyMatrixGraph<>(false, true);

            undirectedGraph.addNode("A");
            undirectedGraph.addNode("B");
            undirectedGraph.addEdge("A", "B", 1.0);

            assertThat(undirectedGraph.containsEdge("A", "B")).isTrue();
            assertThat(undirectedGraph.containsEdge("B", "A")).isTrue();
            assertThat(undirectedGraph.getEdgeWeight("A", "B"))
                    .isEqualTo(undirectedGraph.getEdgeWeight("B", "A"));
        }
    }

    @Nested
    class RemovalOperations {

        @BeforeEach
        void setUpRemovalTest() {
            graph.addNode("A");
            graph.addNode("B");
            graph.addEdge("A", "B", 1.0);
        }

        @Test
        void whenRemovingNode_thenNodeAndEdgesAreRemoved() {
            assertThat(graph.removeNode("A")).isTrue();
            assertThat(graph.containsNode("A")).isFalse();
            assertThat(graph.containsEdge("A", "B")).isFalse();
        }

        @Test
        void whenRemovingEdge_thenEdgeNoLongerExists() {
            assertThat(graph.removeEdge("A", "B")).isTrue();
            assertThat(graph.containsEdge("A", "B")).isFalse();
        }
    }

    @Nested
    class TraversalOperations {

        @Test
        void whenResettingTraversalState_thenAllStatesAreReset() {
            graph.addNode("A");
            graph.setVisited("A", true);
            graph.setCost("A", 10.0);
            graph.setParent("A", "B");

            graph.resetTraversalState();

            assertThat(graph.isVisited("A")).isFalse();
            assertThat(graph.getCost("A")).isEqualTo(Double.POSITIVE_INFINITY);
            assertThat(graph.getParent("A")).isNull();
        }
    }

    @Nested
    class TraversalState {

        @BeforeEach
        void setUpTraversalTest() {
            graph.addNode("A");
            graph.addNode("B");
            graph.addEdge("A", "B", 1.0);
        }

        @Test
        void whenResettingTraversalState_thenAllStatesAreReset() {
            // Set up some traversal state
            graph.setVisited("A", true);
            graph.setCost("A", 10.0);
            graph.setParent("A", "B");

            // Reset state
            graph.resetTraversalState();

            // Verify reset
            assertThat(graph.isVisited("A")).isFalse();
            assertThat(graph.getCost("A")).isEqualTo(Double.POSITIVE_INFINITY);
            assertThat(graph.getParent("A")).isNull();
        }
    }

    @Nested
    class PerformanceTests {

        private static final int LARGE_SIZE = 100;

        @Test
        @DisplayName("Matrix operations should handle large graphs efficiently")
        void performanceTest() {
            // Add nodes
            for (int i = 0; i < LARGE_SIZE; i++) {
                graph.addNode(String.valueOf(i));
            }

            // Add some edges
            Random random = new Random(42);
            for (int i = 0; i < LARGE_SIZE; i++) {
                for (int j = 0; j < 5; j++) { // 5 edges per node
                    int target = random.nextInt(LARGE_SIZE);
                    if (i != target) {
                        graph.addEdge(String.valueOf(i),
                                String.valueOf(target),
                                random.nextDouble());
                    }
                }
            }

            assertThat(graph.getNodeCount()).isEqualTo(LARGE_SIZE);
            assertThat(graph.getEdgeCount()).isPositive();
        }
    }
}
