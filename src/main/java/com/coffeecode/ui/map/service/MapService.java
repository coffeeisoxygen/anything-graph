package com.coffeecode.ui.map.service;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jxmapviewer.JXMapViewer;

import com.coffeecode.event.EventManager;
import com.coffeecode.event.NodeEvent;
import com.coffeecode.event.NodeEventType;
import com.coffeecode.listener.NodeChangeListener;
import com.coffeecode.model.LocationNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapService implements IMapService {

    private final Set<LocationNode> nodes = new CopyOnWriteArraySet<>();
    private final Set<NodeChangeListener> listeners = new CopyOnWriteArraySet<>();
    private final EventManager eventManager = new EventManager();
    private LocationNode startNode;
    private LocationNode endNode;

    @Override
    public void addNode(LocationNode node) {
        if (nodes.add(node)) {
            notifyListeners(listener -> listener.onNodeAdded(node));
            eventManager.publish(new NodeEvent(this, node, NodeEventType.ADDED));
            log.debug("Node added: {}", node);
        }
    }

    @Override
    public void removeNode(LocationNode node) {
        if (nodes.remove(node)) {
            notifyListeners(listener -> listener.onNodeRemoved(node));
            log.debug("Node removed: {}", node);
        }
    }

    @Override
    public void updateStartNode(LocationNode node) {
        startNode = node;
        notifyListeners(listener -> listener.onStartNodeChanged(node));
        eventManager.publish(new NodeEvent(this, node, NodeEventType.START_SET));
    }

    @Override
    public void updateEndNode(LocationNode node) {
        endNode = node;
        notifyListeners(listener -> listener.onEndNodeChanged(node));
    }

    @Override
    public void updateWaypoints(JXMapViewer mapViewer) {
        // Implementation will be added with WaypointRenderer
    }

    @Override
    public void addListener(NodeChangeListener listener) {
        listeners.add(listener);
        Arrays.stream(NodeEventType.values())
                .forEach(type -> eventManager.subscribe(type, listener));
    }

    private void notifyListeners(ListenerAction action) {
        listeners.forEach(action::apply);
    }

    @FunctionalInterface
    private interface ListenerAction {

        void apply(NodeChangeListener listener);
    }
}
