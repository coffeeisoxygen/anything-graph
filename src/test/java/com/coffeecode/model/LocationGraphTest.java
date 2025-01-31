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
        nodeA = new LocationNode("A", 0, 0);
        nodeB = new LocationNode("B", 1, 1);
        nodeC = new LocationNode("C", 2, 2);
        edgeAB = new LocationEdge(nodeA, nodeB, 1.0);
        edgeBC = new LocationEdge(nodeB, nodeC, 2.0);
        testHelper = new TestHelper();

        // Subscribe using test helper
        graph.subscribe(GraphEvent.NodeAdded.class, testHelper::recordEvent);
        graph.subscribe(GraphEvent.EdgeAdded.class, testHelper::recordEvent);
        graph.subscribe(GraphEvent.NodeRemoved.class, testHelper::recordEvent);
        graph.subscribe(GraphEvent.EdgeRemoved.class, testHelper::recordEvent);
    }

    @Nested
    @DisplayName("Graph Structure")
    class GraphStructure {

        @Test
        @DisplayName("Should support traversal")
        void traversal() throws InterruptedException {
            // Build graph with event waits
            graph.addNode(nodeA);
            testHelper.waitForEvents();

            graph.addNode(nodeB);
            testHelper.waitForEvents();

            graph.addNode(nodeC);
            testHelper.waitForEvents();

            graph.addEdge(edgeAB);
            testHelper.waitForEvents();

            graph.addEdge(edgeBC);
            testHelper.waitForEvents();

            // Perform traversal
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

    @AfterEach
    void tearDown() throws InterruptedException {
        testHelper.waitForEvents();
        graph.clear();
        testHelper.waitForEvents();
        graph.shutdown();
        testHelper.clearEvents();
    }
}
