package com.coffeecode.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.coffeecode.algorithm.GraphAlgorithm;
import com.coffeecode.algorithm.pathfinding.BFS;
import com.coffeecode.algorithm.pathfinding.DFS;
import com.coffeecode.event.NodeEvent;
import com.coffeecode.event.NodeEventType;
import com.coffeecode.listener.NodeChangeListener;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlgorithmService {

    @Getter
    private final LocationGraph graph;
    private final Map<String, GraphAlgorithm> algorithms;
    private final List<NodeChangeListener> listeners;

    @Getter
    private LocationNode startNode;
    @Getter
    private LocationNode endNode;

    private volatile boolean isRunning;
    private int delay = 500; // Default delay in ms

    public AlgorithmService() {
        this.graph = new LocationGraph();
        this.algorithms = new HashMap<>();
        this.listeners = new ArrayList<>();
        initializeAlgorithms();
    }

    private void initializeAlgorithms() {
        algorithms.put("BFS", new BFS());
        algorithms.put("DFS", new DFS());
        // Will add Dijkstra and A* later
    }

    public void setStartNode(LocationNode node) {
        this.startNode = node;
        notifyNodeEvent(node, NodeEventType.START_SET);
    }

    public void setEndNode(LocationNode node) {
        this.endNode = node;
        notifyNodeEvent(node, NodeEventType.END_SET);
    }

    public void setDelay(int milliseconds) {
        this.delay = milliseconds;
    }

    public CompletableFuture<List<LocationNode>> executeAlgorithm(String algorithmName) {
        if (!algorithms.containsKey(algorithmName)) {
            throw new IllegalArgumentException("Unknown algorithm: " + algorithmName);
        }

        if (startNode == null || endNode == null) {
            throw new IllegalStateException("Start and end nodes must be set");
        }

        isRunning = true;
        GraphAlgorithm algorithm = algorithms.get(algorithmName);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return algorithm.execute(graph, startNode, endNode, node -> {
                    if (!isRunning) {
                        throw new RuntimeException("Algorithm execution cancelled");
                    }
                    notifyNodeVisited(node);
                    sleep(delay);
                });
            } catch (RuntimeException e) {
                if ("Algorithm execution cancelled".equals(e.getMessage())) {
                    log.info("Algorithm execution cancelled");
                } else {
                    throw e;
                }
                return Collections.emptyList();
            }
        });
    }

    public void stopExecution() {
        isRunning = false;
    }

    public void addListener(NodeChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyNodeVisited(LocationNode node) {
        NodeEvent event = new NodeEvent(this, node, NodeEventType.VISITED);
        listeners.forEach(listener -> listener.onNodeEvent(event));
    }

    private void notifyNodeEvent(LocationNode node, NodeEventType type) {
        NodeEvent event = new NodeEvent(this, node, type);
        listeners.forEach(listener -> listener.onNodeEvent(event));
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Algorithm execution interrupted", e);
        }
    }
}
