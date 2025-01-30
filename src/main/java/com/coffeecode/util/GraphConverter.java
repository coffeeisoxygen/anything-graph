package com.coffeecode.util;

import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;
import org.graphstream.graph.Graph;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GraphConverter {

    private GraphConverter() {
        // Utility class
    }

    /**
     * Converts a LocationGraph to a GraphStream Graph
     */
    public static void updateGraphVisualization(LocationGraph source, Graph target) {
        log.debug("Converting LocationGraph to GraphStream visualization");

        try {
            // Clear existing graph
            target.clear();

            // Add nodes
            for (LocationNode node : source.getNodes()) {
                org.graphstream.graph.Node gsNode = target.addNode(node.getId());
                gsNode.setAttribute("ui.label", node.getId());
                gsNode.setAttribute("x", node.getLongitude());
                gsNode.setAttribute("y", node.getLatitude());
            }

            // Add edges
            for (LocationNode node : source.getNodes()) {
                for (LocationEdge edge : source.getEdges(node)) {
                    String edgeId = edge.getSource().getId() + "-" + edge.getDestination().getId();
                    target.addEdge(edgeId,
                            edge.getSource().getId(),
                            edge.getDestination().getId(),
                            true);
                }
            }

            log.debug("Graph conversion completed: {} nodes, {} edges",
                    target.getNodeCount(), target.getEdgeCount());

        } catch (Exception e) {
            log.error("Error converting graph", e);
            throw new GraphConversionException("Failed to convert graph", e);
        }
    }
}
