package com.coffeecode.event.publisher;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.coffeecode.event.core.GraphStateEvent;
import com.coffeecode.event.listener.GraphStateEventListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GraphStateEventPublisher {
    private final Set<GraphStateEventListener> listeners = new CopyOnWriteArraySet<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void subscribe(GraphStateEventListener listener) {
        listeners.add(listener);
        log.debug("New listener subscribed: {}", listener);
    }

    public void unsubscribe(GraphStateEventListener listener) {
        listeners.remove(listener);
        log.debug("Listener unsubscribed: {}", listener);
    }

    public void publish(GraphStateEvent event) {
        executorService.submit(() 
            -> listeners.forEach(listener -> listener.onGraphStateEvent(event))
        );
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
