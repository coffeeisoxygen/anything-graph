package com.coffeecode.ui.event;

import com.coffeecode.model.LocationNode;
import lombok.Getter;
import java.util.EventObject;

@Getter
public class NodeEvent extends EventObject {

    private final LocationNode node;
    private final NodeEventType type;

    public NodeEvent(Object source, LocationNode node, NodeEventType type) {
        super(source);
        this.node = node;
        this.type = type;
    }
}
