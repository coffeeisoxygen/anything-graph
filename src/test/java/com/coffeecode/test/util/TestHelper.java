package com.coffeecode.test.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHelper {

    private static final int DEFAULT_TIMEOUT_MS = 100;
    private final List<Object> events;
    private final CountDownLatch eventLatch;

    public TestHelper() {
        this.events = Collections.synchronizedList(new ArrayList<>());
        this.eventLatch = new CountDownLatch(1);
    }

    public void recordEvent(Object event) {
        events.add(event);
        eventLatch.countDown();
    }

    public List<Object> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void clearEvents() {
        events.clear();
    }

    public void waitForEvents() throws InterruptedException {
        waitForEvents(DEFAULT_TIMEOUT_MS);
    }

    public void waitForEvents(long timeoutMs) throws InterruptedException {
        boolean completed = eventLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
        if (!completed) {
            log.warn("Event wait timeout after {} ms", timeoutMs);
        }
    }
}
