package com.coffeecode.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.coffeecode.event.core.GraphEvent;

@DisplayName("LocationGraph Test Suite")
class LocationGraphTest {

    private static final int EVENT_WAIT_MS = 100;
    private LocationGraph graph;
    private LocationNode nodeA, nodeB, nodeC;
    private LocationEdge edgeAB, edgeBC;
    private List<Object> receivedEvents;

    @BeforeEach
    void setUp() {
        graph = new LocationGraph();
        nodeA = new LocationNode("A", 0, 0);
        nodeB = new LocationNode("B", 1, 1);
        nodeC = new LocationNode("C", 2, 2);
        edgeAB = new LocationEdge(nodeA, nodeB, 1.0);
        edgeBC = new LocationEdge(nodeB, nodeC, 2.0);
        receivedEvents = Collections.synchronizedList(new ArrayList<>());

        // Subscribe to events
        graph.subscribe(GraphEvent.NodeAdded.class, event -> receivedEvents.add(event));
        graph.subscribe(GraphEvent.EdgeAdded.class, event -> receivedEvents.add(event));
        graph.subscribe(GraphEvent.NodeRemoved.class, event -> receivedEvents.add(event));
        graph.subscribe(GraphEvent.EdgeRemoved.class, event -> receivedEvents.add(event));
    }

    @Nested
    @DisplayName("Node Operations")
    class NodeOperations {

        @Test
        @DisplayName("Should handle basic node operations")
        void basicOperations() throws InterruptedException {
            assertThat(graph.addNode(nodeA)).isTrue();
            waitForEvents();

            assertThat(graph.hasNode(nodeA)).isTrue();
            assertThat(graph.getNodeCount()).isEqualTo(1);
            assertThat(graph.getNodes()).contains(nodeA);

            assertThat(graph.addNode(nodeA)).isFalse(); // Duplicate
            assertThat(graph.removeNode(nodeA)).isTrue();
            waitForEvents();

            assertThat(graph.hasNode(nodeA)).isFalse();
            assertThat(receivedEvents).hasSize(2);
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

        @Test
        @DisplayName("Should handle basic edge operations")
        void basicOperations() throws InterruptedException {
            graph.addNode(nodeA);
            graph.addNode(nodeB);
            waitForEvents();

            assertThat(graph.addEdge(edgeAB)).isTrue();
            waitForEvents();

            assertThat(graph.getEdges(nodeA)).contains(edgeAB);
            assertThat(graph.getEdgeCount()).isEqualTo(1);

            assertThat(graph.addEdge(edgeAB)).isFalse(); // Duplicate
            assertThat(graph.removeEdge(edgeAB)).isTrue();
            waitForEvents();

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

            assertThat(receivedEvents)
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

            for (int i = 0; i < 10000; i++) {
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

    private void waitForEvents() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(EVENT_WAIT_MS);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        waitForEvents();
        graph.clear();
        waitForEvents();
        graph.shutdown();
        receivedEvents.clear();
        waitForEvents();
    }
}
