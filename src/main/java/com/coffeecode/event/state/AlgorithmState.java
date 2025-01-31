package com.coffeecode.event.state;

import java.util.Collections;
import java.util.HashMap;
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

    private LocationNode currentNode;
    private double progress;

    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Double> metrics;

    public AlgorithmState(EventManager eventManager) {
        this.eventManager = Objects.requireNonNull(eventManager);
        this.metrics = new ConcurrentHashMap<>();
        resethelper();
    }

    public synchronized void reset() {
        resethelper();
        eventManager.publish(AlgorithmEvent.Reset.INSTANCE);
    }

    private synchronized void resethelper() {
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
        if (!isPaused) {
            isPaused = true;
            eventManager.publish(AlgorithmEvent.Paused.INSTANCE);
        }
    }

    public synchronized void resume() {
        if (isPaused) {
            isPaused = false;
            eventManager.publish(AlgorithmEvent.Resumed.INSTANCE);
        }
    }

    public synchronized void complete() {
        if (!isComplete) {
            isComplete = true;
            eventManager.publish(new AlgorithmEvent.Completed(
                    Collections.unmodifiableMap(new HashMap<>(metrics))));
            log.debug("Algorithm completed with metrics: {}", metrics);
        }
    }

    public synchronized void updateMetric(String key, double value) {
        metrics.put(key, value);
    }

    public synchronized void fail(String error) {
        isComplete = true;
        eventManager.publish(new AlgorithmEvent.Failed(error));
    }

    public void updateProgress(double progress2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateProgress'");
    }
}
