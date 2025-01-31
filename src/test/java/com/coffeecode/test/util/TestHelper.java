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
    private final CountDownLatch completionLatch;
    private volatile boolean isShutdown;

    public TestHelper() {
        this.events = Collections.synchronizedList(new ArrayList<>());
        this.completionLatch = new CountDownLatch(1);
        this.isShutdown = false;
    }

    public void recordEvent(Object event) {
        if (!isShutdown) {
            events.add(event);
        }
    }

    public List<Object> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void clearEvents() {
        events.clear();
    }

    public synchronized void waitForEvents() throws InterruptedException {
        if (!isShutdown) {
            Thread.sleep(DEFAULT_TIMEOUT_MS);
        }
    }

    public synchronized void waitForEvents(long timeoutMs) throws InterruptedException {
        boolean completed = completionLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
        if (!completed) {
            log.warn("Event wait timeout after {} ms", timeoutMs);
        }
    }

    public synchronized void shutdown() {
        isShutdown = true;
        completionLatch.countDown();
    }
}
