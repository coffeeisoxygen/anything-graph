package com.coffeecode.event.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventManager {

    private final Map<Class<?>, Set<Consumer<?>>> subscribers;
    private final ExecutorService executorService;
    private volatile boolean isShutdown = false;

    public EventManager() {
        this.subscribers = new ConcurrentHashMap<>();
        this.executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName("EventManager-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
    }

    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> ConcurrentHashMap.newKeySet())
                .add(handler);
    }

    public <T> void unsubscribe(Class<T> eventType, Consumer<T> handler) {
        Set<Consumer<?>> handlers = subscribers.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        if (isShutdown) {
            log.warn("EventManager shutdown, rejecting event: {}", event);
            return;
        }

        Set<Consumer<?>> handlers = subscribers.get(event.getClass());
        if (handlers == null) {
            log.warn("No handlers found for event type: {}", event.getClass());
            return;
        }
        if (handlers != null && !handlers.isEmpty()) {
            CompletableFuture.runAsync(() -> {
                handlers.forEach(handler -> {
                    try {
                        ((Consumer<T>) handler).accept(event);
                    } catch (Exception e) {
                        log.error("Error handling event: {}", e.getMessage());
                    }
                });
            }, executorService).exceptionally(throwable -> {
                log.error("Event processing failed: {}", throwable.getMessage());
                return null;
            });
        }
    }

    public void shutdown() {
        isShutdown = true;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    log.error("EventManager failed to terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isShutdown() {
        return isShutdown;
    }

    // For testing purposes
    protected ExecutorService getExecutorService() {
        return executorService;
    }
}
