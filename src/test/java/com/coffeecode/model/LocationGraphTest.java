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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.coffeecode.event.core.GraphEvent;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LocationGraphTest {
    private static final int EVENT_WAIT_MS = 100;
    private LocationGraph graph;
    private LocationNode nodeA, nodeB, nodeC;
    private LocationEdge edgeAB, edgeBC;
    private List<Object> receivedEvents;

    /**
     * Helper method to wait for events to be processed
     */
    private void waitForEvents() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(EVENT_WAIT_MS);
    }

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

    @Test
    @Order(1)
    @DisplayName("Basic node operations")
    void shouldHandleBasicNodeOperations() throws InterruptedException {
        assertThat(graph.addNode(nodeA)).isTrue();
        waitForEvents();

        assertThat(graph.hasNode(nodeA)).isTrue();
        assertThat(graph.getNodeCount()).isEqualTo(1);
        assertThat(graph.getNodes()).contains(nodeA);

        assertThat(graph.addNode(nodeA)).isFalse(); // Duplicate
        assertThat(graph.removeNode(nodeA)).isTrue();
        waitForEvents();

        assertThat(graph.hasNode(nodeA)).isFalse();
        assertThat(receivedEvents).hasSize(2); // Added + Removed events
    }
    @Test
    @Order(2)
    @DisplayName("Basic edge operations")
    void shouldHandleBasicEdgeOperations() throws InterruptedException {
        assertThat(graph.addNode(nodeA)).isTrue();
        assertThat(graph.addNode(nodeB)).isTrue();
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
    @Order(3)
    @DisplayName("Event publishing")
    void shouldPublishEvents() {
        graph.addNode(nodeA);
        graph.addEdge(edgeAB);
        graph.removeNode(nodeA);

        assertThat(receivedEvents)
                .hasSize(4) // NodeAdded, NodeAdded(B), EdgeAdded, NodeRemoved
                .hasOnlyElementsOfTypes(
                        GraphEvent.NodeAdded.class,
                        GraphEvent.EdgeAdded.class,
                        GraphEvent.NodeRemoved.class
                );
    }

    @Test
    @Order(4)
    @DisplayName("Memory usage tracking")
    void shouldTrackMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Add many nodes and edges
        for (int i = 0; i < 10000; i++) {
            LocationNode node = new LocationNode("Node" + i, i % 90, i % 180);
            graph.addNode(node);
            if (i > 0) {
                LocationNode prev = new LocationNode("Node" + (i - 1), (i - 1) % 90, (i - 1) % 180);
                graph.addEdge(new LocationEdge(prev, node, 1.0));
            }
        }

        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;

        System.out.printf("Memory used: %.2f MB%n", memoryUsed / (1024.0 * 1024.0));
        assertThat(memoryUsed).isPositive();
    }

    @Test
    @Order(5)
    @DisplayName("Combined node and edge operations")
    void shouldHandleCombinedOperations() {
        // Build small graph
        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addEdge(edgeAB);
        graph.addEdge(edgeBC);

        // Verify structure
        assertThat(graph.getNodeCount()).isEqualTo(3);
        assertThat(graph.getEdgeCount()).isEqualTo(2);

        // Remove middle node
        graph.removeNode(nodeB);

        // Verify cascade deletion
        assertThat(graph.getNodeCount()).isEqualTo(2);
        assertThat(graph.getEdgeCount()).isEqualTo(0);
        assertThat(graph.getEdges(nodeA)).isEmpty();
        assertThat(graph.getEdges(nodeC)).isEmpty();
    }

    @Test
    @Order(6)
    @DisplayName("Graph traversal")
    void shouldSupportTraversal() {
        // Build graph
        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addEdge(edgeAB);
        graph.addEdge(edgeBC);

        // Get all reachable nodes from A
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

    @Test
    @Order(7)
    @DisplayName("Edge cases and error handling")
    void shouldHandleEdgeCases() {
        assertThat(graph.removeNode(new LocationNode("invalid", 0, 0))).isFalse();
        assertThat(graph.removeEdge(new LocationEdge(new LocationNode("invalid", 0, 0), new LocationNode("invalid", 0, 0), 0.0))).isFalse();
        assertThat(graph.getEdges(new LocationNode("invalid", 0, 0))).isEmpty();
        assertThat(graph.hasNode(new LocationNode("invalid", 0, 0))).isFalse();

        LocationNode invalidNode = new LocationNode("invalid", 0, 0);
        assertThat(graph.getEdges(invalidNode)).isEmpty();
        assertThat(graph.removeNode(invalidNode)).isFalse();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        waitForEvents(); // Wait for any pending events
        graph.clear();
        waitForEvents(); // Wait for clear events
        graph.shutdown();
        receivedEvents.clear();
        waitForEvents(); // Final wait to ensure complete shutdown
    }
}
