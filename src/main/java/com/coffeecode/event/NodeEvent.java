package com.coffeecode.event;

import com.coffeecode.model.LocationNode;
import lombok.Getter;

@Getter
public class NodeEvent {

    private final Object source;
    private final LocationNode node;
    private final NodeEventType type;

    public NodeEvent(Object source, LocationNode node, NodeEventType type) {
        this.source = source;
        this.node = node;
        this.type = type;
    }
}
