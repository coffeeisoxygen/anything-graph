package com.coffeecode.ui.map.service;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jxmapviewer.JXMapViewer;
import com.coffeecode.model.LocationNode;
import com.coffeecode.ui.map.listener.NodeChangeListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapService implements IMapService {

    private final Set<LocationNode> nodes = new CopyOnWriteArraySet<>();
    private final Set<NodeChangeListener> listeners = new CopyOnWriteArraySet<>();
    private LocationNode startNode;
    private LocationNode endNode;

    @Override
    public void addNode(LocationNode node) {
        nodes.add(node);
        notifyListeners(listener -> listener.onNodeAdded(node));
        log.debug("Node added: {}", node);
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
    }

    private void notifyListeners(ListenerAction action) {
        listeners.forEach(action::apply);
    }

    @FunctionalInterface
    private interface ListenerAction {

        void apply(NodeChangeListener listener);
    }
}
