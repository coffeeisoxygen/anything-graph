package com.coffeecode.test.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.coffeecode.event.core.EventListener;
import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.model.LocationGraph;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHelper {

    private final LocationGraph graph;
    private final List<GraphEvent> events;
    private final CountDownLatch eventLatch;
    private final AtomicInteger expectedEvents = new AtomicInteger(0);

    public TestHelper(LocationGraph graph) {
        this.graph = graph;
        this.events = Collections.synchronizedList(new ArrayList<>());
        this.eventLatch = new CountDownLatch(1);
        setupEventSubscriptions();
    }

    private void setupEventSubscriptions() {
        EventListener<GraphEvent.NodeAdded> nodeAddedListener = event -> {
            events.add(event);
            log.debug("Node added event received: {}", event);
            int remaining = expectedEvents.decrementAndGet();
            if (remaining <= 0) {
                eventLatch.countDown();
            }
        };

        EventListener<GraphEvent.EdgeAdded> edgeAddedListener = event -> {
            events.add(event);
            log.debug("Edge added event received: {}", event);
            int remaining = expectedEvents.decrementAndGet();
            if (remaining <= 0) {
                eventLatch.countDown();
            }
        };

        EventListener<GraphEvent.NodeRemoved> nodeRemovedListener = event -> {
            events.add(event);
            log.debug("Node removed event received: {}", event);
            int remaining = expectedEvents.decrementAndGet();
            if (remaining <= 0) {
                eventLatch.countDown();
            }
        };

        EventListener<GraphEvent.EdgeRemoved> edgeRemovedListener = event -> {
            events.add(event);
            log.debug("Edge removed event received: {}", event);
            int remaining = expectedEvents.decrementAndGet();
            if (remaining <= 0) {
                eventLatch.countDown();
            }
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
