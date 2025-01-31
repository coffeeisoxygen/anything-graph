package com.coffeecode.event.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventManager {
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;
    private static final int QUEUE_CAPACITY = 1000;
    private static final long KEEP_ALIVE_TIME = 60L;

    private final Map<Class<?>, Set<Consumer<?>>> subscribers;
    private final ThreadPoolExecutor executorService;
    private final BlockingQueue<Runnable> eventQueue;
    private volatile boolean isShutdown = false;

    public EventManager() {
        this.subscribers = new ConcurrentHashMap<>();
        this.eventQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.executorService = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            eventQueue,
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = Executors.defaultThreadFactory().newThread(r);
                    thread.setName("EventManager-" + thread.getId());
                    thread.setDaemon(true);
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // Fallback to calling thread
        );
        
        // Add monitoring
        executorService.setRejectedExecutionHandler((r, executor) -> {
            log.warn("Event queue full! Task rejected. Queue size: {}", eventQueue.size());
            if (!executor.isShutdown()) {
                r.run(); // Execute in calling thread
            }
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
