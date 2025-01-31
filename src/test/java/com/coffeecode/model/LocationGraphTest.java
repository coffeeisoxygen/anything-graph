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
    @DisplayName("Node Operations")
    class NodeOperations {

        @Test
        @DisplayName("Should add nodes successfully")
        void addNodes() {
            assertThat(graph.addNode(nodeA)).isTrue();
            assertThat(graph.addNode(nodeB)).isTrue();
            assertThat(graph.getNodeCount()).isEqualTo(2);
            assertThat(graph.getNodes()).containsExactlyInAnyOrder(nodeA, nodeB);
        }

        @Test
        @DisplayName("Should prevent duplicate nodes")
        void preventDuplicates() {
            graph.addNode(nodeA);
            assertThat(graph.addNode(nodeA)).isFalse();
            assertThat(graph.getNodeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should remove nodes correctly")
        void removeNodes() {
            graph.addNode(nodeA);
            graph.addNode(nodeB);

            assertThat(graph.removeNode(nodeA)).isTrue();
            assertThat(graph.hasNode(nodeA)).isFalse();
            assertThat(graph.getNodeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should handle invalid node removal")
        void handleInvalidRemoval() {
            assertThat(graph.removeNode(nodeA)).isFalse();
        }
    }

    @Nested
    @DisplayName("Edge Operations")
    class EdgeOperations {

        @BeforeEach
        void setupNodes() {
            graph.addNode(nodeA);
            graph.addNode(nodeB);
            graph.addNode(nodeC);
        }

        @Test
        @DisplayName("Should add edges successfully")
        void addEdges() {
            assertThat(graph.addEdge(edgeAB)).isTrue();
            assertThat(graph.addEdge(edgeBC)).isTrue();
            assertThat(graph.getEdgeCount()).isEqualTo(2);
            assertThat(graph.getEdges(nodeA)).contains(edgeAB);
        }

        @Test
        @DisplayName("Should prevent duplicate edges")
        void preventDuplicateEdges() {
            graph.addEdge(edgeAB);
            assertThat(graph.addEdge(edgeAB)).isFalse();
            assertThat(graph.getEdgeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should remove edges correctly")
        void removeEdges() {
            graph.addEdge(edgeAB);
            assertThat(graph.removeEdge(edgeAB)).isTrue();
            assertThat(graph.getEdgeCount()).isEqualTo(0);
            assertThat(graph.getEdges(nodeA)).isEmpty();
        }

        @Test
        @DisplayName("Should auto-add missing nodes when adding edges")
        void autoAddNodes() {
            LocationNode nodeD = new LocationNode("D", 3, 3);
            LocationEdge edgeCD = new LocationEdge(nodeC, nodeD, 1.0);

            graph.addEdge(edgeCD);
            assertThat(graph.hasNode(nodeD)).isTrue();
            assertThat(graph.hasEdge(edgeCD)).isTrue();
        }
    }

    @Nested
    @DisplayName("Graph Structure")
    class GraphStructure {

        @Test
        @DisplayName("Should handle clear operation")
        void clearGraph() throws InterruptedException {
            graph.addNode(nodeA);
            graph.addNode(nodeB);
            graph.addEdge(edgeAB);
            testHelper.waitForEvents();

            graph.clear();
            testHelper.waitForEvents();

            assertThat(graph.getNodeCount()).isZero();
            assertThat(graph.getEdgeCount()).isZero();
            assertThat(graph.getNodes()).isEmpty();
        }

        @Test
        @DisplayName("Should support traversal")
        void traversal() throws InterruptedException {
            // Build graph
            graph.addNode(nodeA);
            graph.addNode(nodeB);
            graph.addNode(nodeC);
            graph.addEdge(edgeAB);
            graph.addEdge(edgeBC);
            testHelper.waitForEvents();

            // BFS traversal
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
    @DisplayName("Event Publishing")
    class EventPublishing {

        @Test
        @DisplayName("Should publish node events")
        void nodeEvents() throws InterruptedException {
            graph.addNode(nodeA);
            testHelper.waitForEvents();

            assertThat(testHelper.getEvents())
                    .hasSize(1)
                    .hasOnlyElementsOfType(GraphEvent.NodeAdded.class);

            graph.removeNode(nodeA);
            testHelper.waitForEvents();

            assertThat(testHelper.getEvents())
                    .hasSize(2)
                    .element(1)
                    .isInstanceOf(GraphEvent.NodeRemoved.class);
        }

        @Test
        @DisplayName("Should publish edge events")
        void edgeEvents() throws InterruptedException {
            graph.addNode(nodeA);
            graph.addNode(nodeB);
            graph.addEdge(edgeAB);
            testHelper.waitForEvents();

            assertThat(testHelper.getEvents())
                    .hasSize(3)
                    .element(2)
                    .isInstanceOf(GraphEvent.EdgeAdded.class);
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
