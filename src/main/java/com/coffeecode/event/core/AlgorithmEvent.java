package com.coffeecode.event.core;

import com.coffeecode.model.LocationNode;
import lombok.Getter;
import java.time.Instant;

@Getter
public class AlgorithmEvent {

    private final String algorithmName;
    private final AlgorithmEventType type;
    private final LocationNode node;
    private final Instant timestamp;

    public AlgorithmEvent(String algorithmName, AlgorithmEventType type, LocationNode node) {
        this.algorithmName = algorithmName;
        this.type = type;
        this.node = node;
        this.timestamp = Instant.now();
    }

    public enum AlgorithmEventType {
        // Algorithm execution states
        STARTED, // When algorithm starts
        STEP, // Each algorithm step
        FINISHED, // Algorithm completion
        CANCELLED, // Algorithm cancellation

        // Node selection states
        START_NODE_SET, // Start node selected
        END_NODE_SET, // End node selected

        // Algorithm preparation
        READY, // Both nodes selected
        NOT_READY       // Missing start/end nodes
    }
}
