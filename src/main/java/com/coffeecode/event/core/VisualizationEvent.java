package com.coffeecode.event.core;

import com.coffeecode.model.LocationNode;
import lombok.Getter;
import java.time.Instant;

@Getter
public class VisualizationEvent {

    private final LocationNode node;
    private final VisualizationType type;
    private final Instant timestamp;

    public VisualizationEvent(LocationNode node, VisualizationType type) {
        this.node = node;
        this.type = type;
        this.timestamp = Instant.now();
    }

    public enum VisualizationType {
        HIGHLIGHT, UNHIGHLIGHT, PATH_FOUND, PROCESSING
    }
}
