package com.coffeecode.ui.map;

import java.util.ArrayList;
import java.util.List;

import com.coffeecode.model.LocationNode;
import com.coffeecode.ui.listener.NodeChangeListener;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class MapPanelService {

    private final List<LocationNode> nodes = new ArrayList<>();
    private final List<NodeChangeListener> listeners = new ArrayList<>();

    public void addNode(LocationNode node) {
        nodes.add(node);
        notifyNodeAdded(node);
        log.debug("Node added: {}", node);
    }

    public void removeNode(LocationNode node) {
        if (nodes.remove(node)) {
            notifyNodeRemoved(node);
            log.debug("Node removed: {}", node);
        }
    }

    private void notifyNodeAdded(LocationNode node) {
        listeners.forEach(listener -> listener.onNodeAdded(node));
    }

    private void notifyNodeRemoved(LocationNode node) {
        listeners.forEach(listener -> listener.onNodeRemoved(node));
    }

    public void addListener(NodeChangeListener listener) {
        listeners.add(listener);
    }
}
