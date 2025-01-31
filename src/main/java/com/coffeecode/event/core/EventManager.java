package com.coffeecode.event.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventManager {

    private final Map<Class<?>, Set<Consumer<?>>> subscribers;
    private final ExecutorService executorService;
    private final BlockingQueue<Runnable> eventQueue;
    private volatile boolean isShutdown = false;

    public EventManager() {
        this.subscribers = new ConcurrentHashMap<>();
        this.eventQueue = new LinkedBlockingQueue<>(1000); // Bounded queue
        this.executorService = new ThreadPoolExecutor(
                1, // core pool size
                1, // max pool size
                60L, // keep alive time
                TimeUnit.SECONDS,
                eventQueue,
                new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "EventManager-Worker");
                thread.setDaemon(true);
                return thread;
            }
        },
                new ThreadPoolExecutor.CallerRunsPolicy() // Fallback handling
        );
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
            log.warn("Attempting to publish event after shutdown: {}", event);
            return;
        }

        try {
            Set<Consumer<?>> handlers = subscribers.get(event.getClass());
            if (handlers != null && !handlers.isEmpty()) {
                handlers.forEach(handler -> {
                    try {
                        executorService.submit(() -> {
                            try {
                                ((Consumer<T>) handler).accept(event);
                            } catch (Exception e) {
                                log.error("Error handling event: {}", e.getMessage(), e);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                        log.warn("Event rejected, queue full or shutdown: {}", event);
                        // Fallback to synchronous execution
                        ((Consumer<T>) handler).accept(event);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error publishing event: {}", e.getMessage(), e);
        }
    }

    public void shutdown() {
        isShutdown = true;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("EventManager thread pool did not terminate");
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
