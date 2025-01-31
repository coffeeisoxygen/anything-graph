package com.coffeecode.ui.service;

import com.coffeecode.core.GraphResult;
import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;
import com.coffeecode.model.weight.EdgeWeightStrategy;
import com.coffeecode.model.weight.WeightStrategies;
import com.coffeecode.ui.config.GraphStreamConfig;
import com.coffeecode.ui.panelgraph.GraphConverter;
import com.coffeecode.ui.panelgraph.GraphPanel;

import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceGEXF.GEXFConstants.EdgeType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainFrameService {

    private final LocationGraph locationGraph;
    private final Graph visualGraph;
    private final GraphPanel graphPanel;

    public MainFrameService() {
        this.locationGraph = new LocationGraph();
        this.visualGraph = new SingleGraph("visualization");
        // Configure GraphStream before panel creation
        GraphStreamConfig.configureGraph(visualGraph);
        this.graphPanel = new GraphPanel(visualGraph);
        subscribeToGraphEvents();
    }

    private void subscribeToGraphEvents() {
        // Single event handler for all graph changes
        locationGraph.subscribe(GraphEvent.NodeAdded.class, event -> {
            log.debug("Node added event received: {}", event.getNode());
            updateVisualization();
        });
        locationGraph.subscribe(GraphEvent.EdgeAdded.class, event -> {
            log.debug("Edge added event received: {}", event.getEdge());
            updateVisualization();
        });
        locationGraph.subscribe(GraphEvent.NodeRemoved.class, event -> {
            log.debug("Node removed event received: {}", event.getNode());
            updateVisualization();
        });
        locationGraph.subscribe(GraphEvent.EdgeRemoved.class, event -> {
            log.debug("Edge removed event received: {}", event.getEdge());
            updateVisualization();
        });
    }

    // Single node operation
    public GraphResult<Boolean> addNode(String id, double latitude, double longitude) {
        try {
            LocationNode node = new LocationNode(id, latitude, longitude);
            boolean added = locationGraph.addNode(node);
            return added
                    ? GraphResult.success(true)
                    : GraphResult.failure("Node already exists");
        } catch (IllegalArgumentException e) {
            log.error("Failed to add node: {}", e.getMessage());
            return GraphResult.failure(e.getMessage());
        }
    }

    // Batch operation
    public GraphResult<Integer> addNodes(List<LocationNode> nodes) {
        try {
            int added = 0;
            for (LocationNode node : nodes) {
                if (locationGraph.addNode(node)) {
                    added++;
                }
            }
            return added > 0
                    ? GraphResult.success(added)
                    : GraphResult.failure("No nodes were added");
        } catch (Exception e) {
            log.error("Batch add failed: {}", e.getMessage());
            return GraphResult.failure(e.getMessage());
        }
    }

    public GraphResult<Integer> getNodeCount() {
        try {
            return GraphResult.success(locationGraph.getNodeCount());
        } catch (Exception e) {
            log.error("Failed to get node count: {}", e.getMessage());
            return GraphResult.failure(e.getMessage());
        }
    }

    public GraphResult<Boolean> clearGraph() {
        try {
            locationGraph.clear();
            return GraphResult.success(true);
        } catch (Exception e) {
            log.error("Failed to clear graph: {}", e.getMessage());
            return GraphResult.failure(e.getMessage());
        }
    }

    public GraphResult<Boolean> removeNode(String id) {
        try {
            LocationNode node = findNodeById(id);
            if (node == null) {
                return GraphResult.failure("Node not found");
            }

            boolean removed = locationGraph.removeNode(node);
            return removed
                    ? GraphResult.success(true)
                    : GraphResult.failure("Failed to remove node");
        } catch (Exception e) {
            log.error("Error removing node: {}", e.getMessage());
            return GraphResult.failure(e.getMessage());
        }
    }

    private LocationNode findNodeById(String id) {
        return locationGraph.getNodes().stream()
                .filter(node -> node.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public GraphResult<Boolean> addEdge(String sourceId, String targetId, EdgeType type) {
        try {
            LocationNode source = findNodeById(sourceId);
            LocationNode target = findNodeById(targetId);

            if (source == null || target == null) {
                return GraphResult.failure("Node not found");
            }

            EdgeWeightStrategy strategy = switch (type) {
                case HAVERSINE ->
                    WeightStrategies.HAVERSINE_DISTANCE;
                case EUCLIDEAN ->
                    WeightStrategies.EUCLIDEAN_DISTANCE;
                case UNIT ->
                    WeightStrategies.UNIT_WEIGHT;
            };

            double weight = strategy.calculateWeight(source, target);
            boolean added = locationGraph.addEdge(new LocationEdge(source, target, weight));

            return added
                    ? GraphResult.success(true)
                    : GraphResult.failure("Edge already exists");

        } catch (Exception e) {
            log.error("Failed to add edge: {}", e.getMessage());
            return GraphResult.failure(e.getMessage());
        }
    }

    public double calculateEdgeWeight(String sourceId, String targetId, EdgeType type) {
        LocationNode source = findNodeById(sourceId);
        LocationNode target = findNodeById(targetId);

        if (source == null || target == null) {
            throw new IllegalArgumentException("Node not found");
        }

        EdgeWeightStrategy strategy = switch (type) {
            case HAVERSINE ->
                WeightStrategies.HAVERSINE_DISTANCE;
            case EUCLIDEAN ->
                WeightStrategies.EUCLIDEAN_DISTANCE;
            case UNIT ->
                WeightStrategies.UNIT_WEIGHT;
        };

        return strategy.calculateWeight(source, target);
    }

    private void updateVisualization() {
        try {
            GraphConverter.updateGraphVisualization(locationGraph, visualGraph);
            graphPanel.refresh();
            log.debug("Graph visualization updated successfully");
        } catch (Exception e) {
            log.error("Failed to update visualization: {}", e.getMessage());
        }
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }

    public void startAlgorithm() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startAlgorithm'");
    }

    public void pauseAlgorithm() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pauseAlgorithm'");
    }

    public void stopAlgorithm() {
        //TODO
    }

}
