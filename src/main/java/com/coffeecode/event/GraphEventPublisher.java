package com.coffeecode.event;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GraphEventPublisher {

    private final Set<GraphEventListener> listeners = new CopyOnWriteArraySet<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void subscribe(GraphEventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(GraphEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(GraphEvent event) {
        executorService.submit(()
                -> listeners.forEach(listener -> listener.onGraphEvent(event))
        );
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
