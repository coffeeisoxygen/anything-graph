package com.coffeecode.test.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.coffeecode.event.core.EventListener;
import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.model.LocationGraph;

public class TestHelper {

    private final LocationGraph graph;
    private final List<GraphEvent> events = new ArrayList<>();
    private CountDownLatch eventLatch = new CountDownLatch(1);

    public TestHelper(LocationGraph graph) {
        this.graph = graph;
        setupEventSubscriptions();
    }

    private void setupEventSubscriptions() {
        EventListener<GraphEvent.NodeAdded> nodeAddedListener = event -> {
            events.add(event);
            eventLatch.countDown();
        };

        EventListener<GraphEvent.EdgeAdded> edgeAddedListener = event -> {
            events.add(event);
            eventLatch.countDown();
        };

        EventListener<GraphEvent.NodeRemoved> nodeRemovedListener = event -> {
            events.add(event);
            eventLatch.countDown();
        };

        EventListener<GraphEvent.EdgeRemoved> edgeRemovedListener = event -> {
            events.add(event);
            eventLatch.countDown();
        };

        graph.subscribe(GraphEvent.NodeAdded.class, nodeAddedListener);
        graph.subscribe(GraphEvent.EdgeAdded.class, edgeAddedListener);
        graph.subscribe(GraphEvent.NodeRemoved.class, nodeRemovedListener);
        graph.subscribe(GraphEvent.EdgeRemoved.class, edgeRemovedListener);
    }

    public List<GraphEvent> getEvents() {
        return events;
    }

    public void waitForEvents() throws InterruptedException {
        eventLatch.await(100, TimeUnit.MILLISECONDS);
    }

    public void clearEvents() {
        events.clear();
    }
}
