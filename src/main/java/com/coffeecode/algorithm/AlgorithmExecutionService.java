package com.coffeecode.algorithm;

import java.util.concurrent.CompletableFuture;

import com.coffeecode.algorithm.core.GraphAlgorithm;
import com.coffeecode.event.state.AlgorithmState;
import com.coffeecode.event.state.GraphState;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlgorithmExecutionService {

    private final LocationGraph graph;
    private final GraphState graphState;
    private final AlgorithmState algorithmState;
    private volatile GraphAlgorithm currentAlgorithm;

    public void execute(String algorithmType, LocationNode start, LocationNode end) {
        algorithmState.reset();
        graphState.clear();

        currentAlgorithm = createAlgorithm(algorithmType);
        CompletableFuture.runAsync(() -> {
            try {
                currentAlgorithm.execute(graph, start, end, graphState);
            } catch (Exception e) {
                algorithmState.fail(e.getMessage());
            }
        });
    }

    public void pause() {
        algorithmState.pause();
        currentAlgorithm.pause();
    }

    public void step() {
        if (currentAlgorithm != null && currentAlgorithm.hasNextStep()) {
            currentAlgorithm.executeStep();
        }
    }
}
