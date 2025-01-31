package com.coffeecode.ui.service;

import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import com.coffeecode.service.GraphConverter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class MainFrameService {

    private final Graph visualGraph;
    private final LocationGraph locationGraph;

    public MainFrameService() {
        this.visualGraph = new SingleGraph("GraphStream");
        this.locationGraph = new LocationGraph();
        setupGraphStream();
    }

    private void setupGraphStream() {
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        visualGraph.setAttribute("ui.quality");
        visualGraph.setAttribute("ui.antialias");
        visualGraph.setAttribute("ui.stylesheet", """
            graph { padding: 50px; }
            node {
                size: 30px;
                fill-color: #666666;
                text-size: 14px;
                text-color: white;
                text-style: bold;
            }
            edge {
                size: 2px;
                fill-color: #999999;
                text-size: 14px;
            }
        """);
    }

    public boolean addNode(String id, double latitude, double longitude) {
        try {
            LocationNode node = new LocationNode(id, latitude, longitude);
            boolean added = locationGraph.addNode(node);
            if (added) {
                GraphConverter.updateGraphVisualization(locationGraph, visualGraph);
            }
            return added;
        } catch (IllegalArgumentException e) {
            log.error("Invalid node parameters: {}", e.getMessage());
            return false;
        }
    }

    public boolean removeNode(String id) {
        LocationNode node = findNodeById(id);
        if (node != null) {
            boolean removed = locationGraph.removeNode(node);
            if (removed) {
                GraphConverter.updateGraphVisualization(locationGraph, visualGraph);
            }
            return removed;
        }
        return false;
    }

    private LocationNode findNodeById(String id) {
        return locationGraph.getNodes().stream()
                .filter(node -> node.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void startAlgorithm() {
        // Implementation for starting the algorithm
    }

    public void pauseAlgorithm() {
        // Implementation for pausing the algorithm
    }

    public void stopAlgorithm() {
        // Implementation for stopping the algorithm
    }
}
