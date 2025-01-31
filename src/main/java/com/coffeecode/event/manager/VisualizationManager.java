package com.coffeecode.event.manager;

import com.coffeecode.event.core.VisualizationEvent;
import com.coffeecode.event.listener.VisualizationEventListener;
import com.coffeecode.model.LocationNode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class VisualizationManager {

    private final Set<VisualizationEventListener> listeners = new CopyOnWriteArraySet<>();
    private final EventManager eventManager;

    public VisualizationManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void addListener(VisualizationEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(VisualizationEventListener listener) {
        listeners.remove(listener);
    }

    public void visualizeStep(LocationNode node) {
        publish(new VisualizationEvent(node, VisualizationEvent.VisualizationType.PROCESSING));
    }

    public void visualizePath(List<LocationNode> path) {
        path.forEach(node
                -> publish(new VisualizationEvent(node, VisualizationEvent.VisualizationType.PATH_FOUND))
        );
    }

    private void publish(VisualizationEvent event) {
        listeners.forEach(listener -> {
            try {
                listener.onVisualizationEvent(event);
            } catch (Exception e) {
                log.error("Error publishing visualization event", e);
            }
        });
    }
}
