package com.coffeecode.event.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventManager {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<Class<?>, Set<EventListener>> listeners = new ConcurrentHashMap<>();

    public void subscribe(Class<?> eventType, EventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArraySet<>())
                .add(listener);
    }

    public void unsubscribe(Class<?> eventType, EventListener listener) {
        listeners.getOrDefault(eventType, Set.of()).remove(listener);
    }

    public void publish(Object event) {
        Set<EventListener> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            executor.submit(() -> {
                for (EventListener listener : eventListeners) {
                    try {
                        listener.onEvent(event);
                    } catch (Exception e) {
                        log.error("Error handling event {}", event, e);
                    }
                }
            });
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
