package com.coffeecode.event.core;

import com.coffeecode.model.LocationNode;
import lombok.Getter;
import java.time.Instant;

@Getter
public class GraphEvent {

    private final LocationNode node;
    private final GraphEventType type;
    private final Instant timestamp;

    public GraphEvent(LocationNode node, GraphEventType type) {
        this.node = node;
        this.type = type;
        this.timestamp = Instant.now();
    }

    public enum GraphEventType {
        NODE_ADDED, NODE_REMOVED, START_NODE_SET, END_NODE_SET
    }
}
