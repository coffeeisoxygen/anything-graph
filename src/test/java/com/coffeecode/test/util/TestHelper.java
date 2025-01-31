package com.coffeecode.test.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.coffeecode.event.core.GraphEvent;
import com.coffeecode.model.LocationGraph;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHelper {

    private final LocationGraph graph;
    private final List<GraphEvent> events;
    private final CountDownLatch eventLatch;
    private volatile int expectedEventCount;

    public TestHelper(LocationGraph graph) {
        this.graph = graph;
        this.events = Collections.synchronizedList(new ArrayList<>());
        this.eventLatch = new CountDownLatch(1);
        this.expectedEventCount = 0;
        setupEventSubscriptions();
    }

    public synchronized void expectEvents(int count) {
        this.expectedEventCount = count;
        if (count > 0) {
            this.eventLatch.countDown();
            new CountDownLatch(1); // Reset latch
        }
    }

    private synchronized void onEventReceived(GraphEvent event) {
        events.add(event);
        log.debug("Event received: {}", event);
        if (events.size() >= expectedEventCount) {
            eventLatch.countDown();
        }
    }

    private void setupEventSubscriptions() {
        graph.subscribe(GraphEvent.NodeAdded.class,
                event -> onEventReceived(event));
        graph.subscribe(GraphEvent.EdgeAdded.class,
                event -> onEventReceived(event));
        graph.subscribe(GraphEvent.NodeRemoved.class,
                event -> onEventReceived(event));
        graph.subscribe(GraphEvent.EdgeRemoved.class,
                event -> onEventReceived(event));
    }

    public List<GraphEvent> getEvents() {
        return new ArrayList<>(events);
    }

    public void waitForEvents() throws InterruptedException {
        if (!eventLatch.await(500, TimeUnit.MILLISECONDS)) {
            log.warn("Timeout waiting for events. Expected: {}, Got: {}",
                    expectedEventCount, events.size());
        }
    }

    public synchronized void clearEvents() {
        events.clear();
        expectedEventCount = 0;
    }
}
