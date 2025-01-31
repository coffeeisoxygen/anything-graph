package com.coffeecode.event.state;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.coffeecode.event.core.AlgorithmEvent;
import com.coffeecode.event.core.EventManager;
import com.coffeecode.model.LocationNode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class AlgorithmState {

    private volatile boolean isPaused;
    private volatile boolean isComplete;
    private final EventManager eventManager;

    // Current algorithm state
    private LocationNode currentNode;
    private double progress;

    // Stats tracking (for future algorithm analysis)
    @Getter(AccessLevel.PACKAGE) // For testing
    private final Map<String, Double> metrics;

    public AlgorithmState(EventManager eventManager) {
        this.eventManager = Objects.requireNonNull(eventManager, "EventManager cannot be null");
        this.metrics = new ConcurrentHashMap<>();
        this.isPaused = false;
        this.isComplete = false;
        this.currentNode = null;
        this.progress = 0.0;
        this.metrics.clear();
    }

    public synchronized void reset() {
        isPaused = false;
        isComplete = false;
        currentNode = null;
        progress = 0.0;
        metrics.clear();
    }

    public synchronized void step() {
        if (!isPaused && !isComplete) {
            eventManager.publish(new AlgorithmEvent.StepCompleted(currentNode, progress));
        }
    }

    public synchronized void updateState(LocationNode node, double progress) {
        this.currentNode = node;
        this.progress = progress;
        eventManager.publish(new AlgorithmEvent.StateUpdated(node, progress));
    }

    public synchronized void pause() {
        isPaused = true;
        eventManager.publish(new AlgorithmEvent.Paused());
    }

    public synchronized void resume() {
        isPaused = false;
        eventManager.publish(new AlgorithmEvent.Resumed());
    }

    public synchronized void complete() {
        isComplete = true;
        eventManager.publish(new AlgorithmEvent.Completed(metrics));
    }

    public synchronized void updateMetric(String key, double value) {
        metrics.put(key, value);
    }
}
