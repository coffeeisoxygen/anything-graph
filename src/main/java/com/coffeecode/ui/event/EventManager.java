package com.coffeecode.ui.event;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.coffeecode.ui.listener.NodeChangeListener;

@Slf4j
public class EventManager {

    private final Map<NodeEventType, List<NodeChangeListener>> listeners = new EnumMap<>(NodeEventType.class);

    public void subscribe(NodeEventType type, NodeChangeListener listener) {
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(listener);
        log.debug("Listener added for event type: {}", type);
    }

    public void publish(NodeEvent event) {
        List<NodeChangeListener> typeListeners = listeners.get(event.getType());
        if (typeListeners != null) {
            typeListeners.forEach(listener -> {
                try {
                    listener.onNodeEvent(event);
                } catch (Exception e) {
                    log.error("Error notifying listener", e);
                }
            });
        }
    }
}
