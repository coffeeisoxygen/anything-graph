package com.coffeecode.service.algorithm;

import com.coffeecode.model.*;
import com.coffeecode.algorithm.pathfinding.BFS;
import com.coffeecode.algorithm.pathfinding.DFS;
import com.coffeecode.event.core.*;
import com.coffeecode.event.listener.*;
import java.util.*;
import java.util.concurrent.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlgorithmService {

    private final Map<String, GraphAlgorithm> algorithms;
    private final Set<AlgorithmEventListener> algorithmListeners;
    private final Set<VisualizationEventListener> visualizationListeners;
    private final ExecutorService executor;

    @Getter
    private final LocationGraph graph;
    @Getter
    private LocationNode startNode;
    @Getter
    private LocationNode endNode;

    private volatile boolean isRunning;
    private int delay = 500;

    public AlgorithmService() {
        this.graph = new LocationGraph();
        this.algorithms = new ConcurrentHashMap<>();
        this.algorithmListeners = new CopyOnWriteArraySet<>();
        this.visualizationListeners = new CopyOnWriteArraySet<>();
        this.executor = Executors.newSingleThreadExecutor();
        registerDefaultAlgorithms();
    }

    public void setStartNode(LocationNode node) {
        this.startNode = node;
        publishAlgorithmEvent(new AlgorithmEvent(
                "Setup",
                AlgorithmEvent.AlgorithmEventType.START_NODE_SET,
                node
        ));
    }

    public void setEndNode(LocationNode node) {
        this.endNode = node;
        publishAlgorithmEvent(new AlgorithmEvent(
                "Setup",
                AlgorithmEvent.AlgorithmEventType.END_NODE_SET,
                node
        ));
    }

    public CompletableFuture<List<LocationNode>> executeAlgorithm(String algorithmName) {
        if (!algorithms.containsKey(algorithmName)) {
            throw new IllegalArgumentException("Unknown algorithm: " + algorithmName);
        }

        isRunning = true;
        publishAlgorithmEvent(new AlgorithmEvent(
                algorithmName,
                AlgorithmEvent.AlgorithmEventType.STARTED,
                null
        ));

        return CompletableFuture.supplyAsync(() -> {
            try {
                return algorithms.get(algorithmName).execute(
                        graph,
                        startNode,
                        endNode,
                        this::handleNodeVisit
                );
            } catch (Exception e) {
                log.error("Algorithm execution failed", e);
                publishAlgorithmEvent(new AlgorithmEvent(
                        algorithmName,
                        AlgorithmEvent.AlgorithmEventType.CANCELLED,
                        null
                ));
                return Collections.emptyList();
            }
        }, executor);
    }

    private void handleNodeVisit(LocationNode node) {
        if (!isRunning) {
            throw new RuntimeException("Algorithm cancelled");
        }

        publishVisualizationEvent(new VisualizationEvent(
                node,
                VisualizationEvent.VisualizationType.PROCESSING
        ));

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void addAlgorithmListener(AlgorithmEventListener listener) {
        algorithmListeners.add(listener);
    }

    public void addVisualizationListener(VisualizationEventListener listener) {
        visualizationListeners.add(listener);
    }

    private void publishAlgorithmEvent(AlgorithmEvent event) {
        algorithmListeners.forEach(listener -> {
            try {
                listener.onAlgorithmEvent(event);
            } catch (Exception e) {
                log.error("Error notifying algorithm listener", e);
            }
        });
    }

    private void publishVisualizationEvent(VisualizationEvent event) {
        visualizationListeners.forEach(listener -> {
            try {
                listener.onVisualizationEvent(event);
            } catch (Exception e) {
                log.error("Error notifying visualization listener", e);
            }
        });
    }

    private void registerDefaultAlgorithms() {
        algorithms.put("BFS", new BFS());
        algorithms.put("DFS", new DFS());
    }
}
