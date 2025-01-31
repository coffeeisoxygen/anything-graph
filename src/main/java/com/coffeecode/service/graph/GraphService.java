package com.coffeecode.service.graph;

import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;
import com.coffeecode.model.LocationEdge;
import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.event.core.GraphEvent.GraphEventType;
import com.coffeecode.event.listener.GraphEventListener;
import com.coffeecode.event.manager.EventManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class GraphService implements IGraphService {

    @Getter
    private final LocationGraph graph;
    private final Set<GraphEventListener> listeners;
    private final EventManager eventManager;

    public GraphService() {
        this.graph = new LocationGraph();
        this.listeners = new CopyOnWriteArraySet<>();
        this.eventManager = new EventManager();
    }

    @Override
    public void addNode(LocationNode node) {
        if (graph.addNode(node)) {
            publishEvent(new GraphEvent(node, GraphEvent.GraphEventType.NODE_ADDED));
            log.debug("Node added: {}", node);
        }
    }

    @Override
    public void removeNode(LocationNode node) {
        if (graph.removeNode(node)) {
            publishEvent(new GraphEvent(node, GraphEvent.GraphEventType.NODE_REMOVED));
            log.debug("Node removed: {}", node);
        }
    }

    @Override
    public void addEdge(LocationNode source, LocationNode target, double weight) {
        LocationEdge edge = new LocationEdge(source, target, weight);
        graph.addEdge(edge);
        eventManager.publish(new GraphEvent(edge, GraphEventType.EDGE_ADDED));
        log.debug("Edge added: {} -> {} (weight: {})", source, target, weight);
    }

    @Override
    public void removeEdge(LocationNode source, LocationNode target) {
        graph.getEdges(source).stream()
            .filter(e -> e.getDestination().equals(target))
            .findFirst()
            .ifPresent(edge -> {
                graph.removeEdge(edge);
                eventManager.publish(new GraphEvent(edge, GraphEventType.EDGE_REMOVED));
                log.debug("Edge removed: {} -> {}", source, target);
            });
    }

    @Override
    public LocationNode getNode(String id) {
        return graph.getNode(id);
    }

    @Override
    public boolean hasNode(LocationNode node) {
        return graph.hasNode(node);
    }

    @Override
    public boolean hasEdge(LocationNode source, LocationNode target) {
        return graph.hasEdge(source, target);
    }

    @Override
    public void addListener(GraphEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(GraphEventListener listener) {
        listeners.remove(listener);
    }

    private void publishEvent(GraphEvent event) {
        listeners.forEach(listener -> {
            try {
                listener.onGraphEvent(event);
            } catch (Exception e) {
                log.error("Error notifying listener", e);
            }
        });
    }
}
