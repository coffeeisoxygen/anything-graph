package com.coffeecode.ui.listener;

import com.coffeecode.model.LocationNode;
import com.coffeecode.ui.event.NodeEvent;

public interface NodeChangeListener {

    // Existing direct methods
    void onNodeAdded(LocationNode node);

    void onNodeRemoved(LocationNode node);

    void onStartNodeChanged(LocationNode node);

    void onEndNodeChanged(LocationNode node);

    // New event-based method
    default void onNodeEvent(NodeEvent event) {
        switch (event.getType()) {
            case ADDED ->
                onNodeAdded(event.getNode());
            case REMOVED ->
                onNodeRemoved(event.getNode());
            case START_SET ->
                onStartNodeChanged(event.getNode());
            case END_SET ->
                onEndNodeChanged(event.getNode());
            default ->
                handleCustomEvent(event);
        }
    }

    // Hook for custom event handling
    default void handleCustomEvent(NodeEvent event) {
    }
}
