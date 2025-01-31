package com.coffeecode.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.test.util.TestHelper;

@DisplayName("LocationGraph Test Suite")
class LocationGraphTest {
    private LocationGraph graph;
    private LocationNode nodeA, nodeB, nodeC;
    private LocationEdge edgeAB, edgeBC;
    private TestHelper testHelper;

    @BeforeEach
    void setUp() {
        graph = new LocationGraph();
        testHelper = new TestHelper(graph);
        
        nodeA = new LocationNode("A", 0, 0);
        nodeB = new LocationNode("B", 1, 1);
        nodeC = new LocationNode("C", 2, 2);
        edgeAB = new LocationEdge(nodeA, nodeB, 1.0);
        edgeBC = new LocationEdge(nodeB, nodeC, 2.0);
    }

    @Nested
    @DisplayName("Node Operations")
    class NodeOperations {
        @Test
        @DisplayName("Should handle basic node operations")
        void basicOperations() throws InterruptedException {
            assertThat(graph.addNode(nodeA)).isTrue();
            testHelper.waitForEvents();
            
            assertThat(graph.hasNode(nodeA)).isTrue();
            assertThat(graph.getNodeCount()).isEqualTo(1);
            assertThat(graph.getNodes()).contains(nodeA);
            
            assertThat(testHelper.getEvents())
                .hasSize(1)
                .hasOnlyElementsOfType(GraphEvent.NodeAdded.class);

            assertThat(graph.addNode(nodeA)).isFalse(); // Duplicate
            testHelper.waitForEvents();
            
            assertThat(graph.removeNode(nodeA)).isTrue();
            testHelper.waitForEvents();
            
            assertThat(graph.hasNode(nodeA)).isFalse();
            assertThat(testHelper.getEvents())
                .hasSize(2)
                .element(1)
                .isInstanceOf(GraphEvent.NodeRemoved.class);
        }

        @Test
        @DisplayName("Should handle invalid nodes")
        void invalidNodes() {
            LocationNode invalidNode = new LocationNode("invalid", 0, 0);
            assertThat(graph.removeNode(invalidNode)).isFalse();
            assertThat(graph.hasNode(invalidNode)).isFalse();
            assertThat(graph.getEdges(invalidNode)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Operations") 
    class EdgeOperations {
        @BeforeEach
        void setupNodes() throws InterruptedException {
            graph.addNode(nodeA);
            graph.addNode(nodeB);
            testHelper.waitForEvents();
        }

        @Test
        @DisplayName("Should handle basic edge operations")
        void basicOperations() throws InterruptedException {
            assertThat(graph.addEdge(edgeAB)).isTrue();
            testHelper.waitForEvents();

            assertThat(graph.getEdges(nodeA)).contains(edgeAB);
            assertThat(graph.getEdgeCount()).isEqualTo(1);

            assertThat(graph.addEdge(edgeAB)).isFalse(); // Duplicate
            assertThat(graph.removeEdge(edgeAB)).isTrue();
            testHelper.waitForEvents();

            assertThat(graph.getEdges(nodeA)).isEmpty();
        }

        @Test
        @DisplayName("Should handle invalid edges")
        void invalidEdges() {
            LocationEdge invalidEdge = new LocationEdge(
                    new LocationNode("invalid1", 0, 0),
                    new LocationNode("invalid2", 0, 0),
                    1.0
            );
            assertThat(graph.removeEdge(invalidEdge)).isFalse();
            assertThat(graph.getEdges(invalidEdge.getSource())).isEmpty();
        }
    }

    @Nested
    @DisplayName("Graph Structure")
    class GraphStructure {

        @Test
        @DisplayName("Should handle combined operations")
        void combinedOperations() {
            graph.addNode(nodeA);
            graph.addNode(nodeB);
            graph.addNode(nodeC);
            graph.addEdge(edgeAB);
            graph.addEdge(edgeBC);

            assertThat(graph.getNodeCount()).isEqualTo(3);
            assertThat(graph.getEdgeCount()).isEqualTo(2);

            graph.removeNode(nodeB); // Remove middle node

            assertThat(graph.getNodeCount()).isEqualTo(2);
            assertThat(graph.getEdgeCount()).isEqualTo(0);
            assertThat(graph.getEdges(nodeA)).isEmpty();
            assertThat(graph.getEdges(nodeC)).isEmpty();
        }

        @Test
        @DisplayName("Should support traversal")
        void traversal() {
            // Build graph
            graph.addNode(nodeA);
            graph.addNode(nodeB);
            graph.addNode(nodeC);
            graph.addEdge(edgeAB);
            graph.addEdge(edgeBC);

            Set<LocationNode> reachable = new HashSet<>();
            Queue<LocationNode> queue = new LinkedList<>();
            queue.add(nodeA);

            while (!queue.isEmpty()) {
                LocationNode current = queue.poll();
                reachable.add(current);
                graph.getEdges(current).forEach(edge -> {
                    LocationNode next = edge.getDestination();
                    if (!reachable.contains(next)) {
                        queue.add(next);
                    }
                });
            }

            assertThat(reachable)
                    .containsExactlyInAnyOrder(nodeA, nodeB, nodeC)
                    .hasSize(3);
        }
    }

    @Nested
    @DisplayName("Event System")
    class EventSystem {

        @Test
        @DisplayName("Should publish events correctly")
        void eventPublishing() {
            graph.addNode(nodeA);
            graph.addEdge(edgeAB);
            graph.removeNode(nodeA);

            assertThat(testHelper.getEvents())
                    .hasSize(4)
                    .hasOnlyElementsOfTypes(
                            GraphEvent.NodeAdded.class,
                            GraphEvent.EdgeAdded.class,
                            GraphEvent.NodeRemoved.class
                    );
        }
    }

    @Nested
    @DisplayName("Performance")
    class Performance {

        @Test
        @DisplayName("Should handle large graphs efficiently")
        void memoryUsage() {
            Runtime runtime = Runtime.getRuntime();
            long initialMemory = runtime.totalMemory() - runtime.freeMemory();

            for (int i = 0; i < 100; i++) {
                LocationNode node = new LocationNode("Node" + i, i % 90, i % 180);
                graph.addNode(node);
                if (i > 0) {
                    LocationNode prev = new LocationNode(
                            "Node" + (i - 1),
                            (i - 1) % 90,
                            (i - 1) % 180
                    );
                    graph.addEdge(new LocationEdge(prev, node, 1.0));
                }
            }

            long memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) - initialMemory;
            System.out.printf("Memory used: %.2f MB%n", memoryUsed / (1024.0 * 1024.0));
            assertThat(memoryUsed).isPositive();
        }
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        testHelper.waitForEvents();
        graph.clear();
        testHelper.waitForEvents();
        graph.shutdown();
        testHelper.clearEvents();
    }
}
