package com.coffeecode.event.manager;

import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.event.core.AlgorithmEvent;
import com.coffeecode.event.listener.GraphEventListener;
import com.coffeecode.event.listener.AlgorithmEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class EventManager {

    private final Set<GraphEventListener> graphListeners = new CopyOnWriteArraySet<>();
    private final Set<AlgorithmEventListener> algorithmListeners = new CopyOnWriteArraySet<>();

    public void subscribe(GraphEventListener listener) {
        graphListeners.add(listener);
    }

    public void subscribe(AlgorithmEventListener listener) {
        algorithmListeners.add(listener);
    }

    public void unsubscribe(GraphEventListener listener) {
        graphListeners.remove(listener);
    }

    public void unsubscribe(AlgorithmEventListener listener) {
        algorithmListeners.remove(listener);
    }

    public void publish(GraphEvent event) {
        graphListeners.forEach(listener -> {
            try {
                listener.onGraphEvent(event);
            } catch (Exception e) {
                log.error("Error publishing graph event", e);
            }
        });
    }

    public void publish(AlgorithmEvent event) {
        algorithmListeners.forEach(listener -> {
            try {
                listener.onAlgorithmEvent(event);
            } catch (Exception e) {
                log.error("Error publishing algorithm event", e);
            }
        });
    }
}
