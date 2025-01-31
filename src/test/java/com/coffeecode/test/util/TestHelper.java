package com.coffeecode.test.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.coffeecode.event.core.EventListener;
import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.model.LocationGraph;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHelper {

    private final LocationGraph graph;
    private final List<GraphEvent> events;
    private volatile CountDownLatch eventLatch;
    private volatile int expectedEventCount;

    public TestHelper(LocationGraph graph) {
        this.graph = graph;
        this.events = Collections.synchronizedList(new ArrayList<>());
        this.eventLatch = new CountDownLatch(1);
        setupEventSubscriptions();
    }

    public synchronized void expectEvents(int count) {
        clearEvents(); // Clear previous events
        this.expectedEventCount = count;
        this.eventLatch = new CountDownLatch(count);
    }

    private synchronized void onEventReceived(GraphEvent event) {
        events.add(event);
        log.debug("Event received: {} (count: {}/{})",
                event, events.size(), expectedEventCount);
        if (eventLatch != null) {
            eventLatch.countDown();
        }
    }

    private void setupEventSubscriptions() {
        // Create specific listeners for each event type
        EventListener<GraphEvent.NodeAdded> nodeAddedListener
                = event -> onEventReceived(event);
        EventListener<GraphEvent.EdgeAdded> edgeAddedListener
                = event -> onEventReceived(event);
        EventListener<GraphEvent.NodeRemoved> nodeRemovedListener
                = event -> onEventReceived(event);
        EventListener<GraphEvent.EdgeRemoved> edgeRemovedListener
                = event -> onEventReceived(event);

        // Subscribe with type-specific listeners
        graph.subscribe(GraphEvent.NodeAdded.class, nodeAddedListener);
        graph.subscribe(GraphEvent.EdgeAdded.class, edgeAddedListener);
        graph.subscribe(GraphEvent.NodeRemoved.class, nodeRemovedListener);
        graph.subscribe(GraphEvent.EdgeRemoved.class, edgeRemovedListener);
    }

    public synchronized void waitForEvents() throws InterruptedException {
        if (expectedEventCount > 0) {
            boolean completed = eventLatch.await(500, TimeUnit.MILLISECONDS);
            if (!completed) {
                log.warn("Timeout waiting for events. Expected: {}, Received: {}",
                        expectedEventCount, events.size());
            }
        }
    }

    public synchronized void clearEvents() {
        events.clear();
        expectedEventCount = 0;
        eventLatch = new CountDownLatch(1);
    }

    public List<GraphEvent> getEvents() {
        return new ArrayList<>(events);
    }
}
