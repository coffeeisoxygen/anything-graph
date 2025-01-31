package com.coffeecode.ui.service;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;
import com.coffeecode.model.validation.GraphResult;
import com.coffeecode.ui.panelgraph.GraphConverter;
import com.coffeecode.ui.panelgraph.GraphPanel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class MainFrameService {

    private final Graph visualGraph;
    private final LocationGraph locationGraph;
    private final GraphPanel graphPanel;

    public MainFrameService() {
        this.visualGraph = new SingleGraph("visualization");
        this.locationGraph = new LocationGraph();
        this.graphPanel = new GraphPanel(visualGraph);
        setupGraphStream();
        subscribeToGraphEvents();
    }

    private void subscribeToGraphEvents() {
        locationGraph.subscribe(GraphEvent.NodeAdded.class,
                event -> updateVisualization());
        locationGraph.subscribe(GraphEvent.EdgeAdded.class,
                event -> updateVisualization());
        locationGraph.subscribe(GraphEvent.NodeRemoved.class,
                event -> updateVisualization());
        locationGraph.subscribe(GraphEvent.EdgeRemoved.class,
                event -> updateVisualization());
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
                size: 50px;
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

    private void updateVisualization() {
        GraphConverter.updateGraphVisualization(locationGraph, visualGraph);
        graphPanel.refresh();
        log.debug("Graph visualization updated");
    }

    public GraphResult<Boolean> addNode(String id, double latitude, double longitude) {
        try {
            LocationNode node = new LocationNode(id, latitude, longitude);
            boolean added = locationGraph.addNode(node);
            if (added) {
                updateVisualization();
                return GraphResult.success(true);
            }
            return GraphResult.failure("Node already exists");
        } catch (IllegalArgumentException e) {
            log.error("Failed to add node: {}", e.getMessage());
            return GraphResult.failure(e.getMessage());
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
