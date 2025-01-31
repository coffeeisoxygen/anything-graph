package com.coffeecode.test;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

public class LoadTest {

    private static final int THREAD_COUNT = 10;
    private static final int OPERATIONS_PER_THREAD = 1000;
    private static final int TIMEOUT_SECONDS = 30;

    @Test
    void shouldHandleConcurrentNodeOperations() throws InterruptedException {
        // Setup
        LocationGraph graph = new LocationGraph();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(THREAD_COUNT);
        Set<String> failedNodes = Collections.newSetFromMap(new ConcurrentHashMap<>());
        AtomicInteger successCount = new AtomicInteger(0);

        // Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        // Submit tasks
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        String id = String.format("Node-%d-%d", threadId, j);
                        LocationNode node = new LocationNode(id, j % 90, j % 180);
                        if (graph.addNode(node)) {
                            successCount.incrementAndGet();
                        } else {
                            failedNodes.add(id);
                        }
                    }
                } catch (Exception e) {
                    failedNodes.add("Thread-" + threadId + "-failed");
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        // Start all threads simultaneously
        startLatch.countDown();

        // Wait for completion with timeout
        boolean completed = completionLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executor.shutdown();
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);

        // Assertions
        assertThat(completed)
                .as("All operations should complete within timeout")
                .isTrue();

        assertThat(terminated)
                .as("Executor should terminate cleanly")
                .isTrue();

        assertThat(graph.getNodeCount())
                .as("Graph should contain all successfully added nodes")
                .isEqualTo(successCount.get());

        assertThat(failedNodes)
                .as("Should have no failed node additions")
                .isEmpty();

        assertThat(successCount.get())
                .as("Should successfully add all nodes")
                .isEqualTo(THREAD_COUNT * OPERATIONS_PER_THREAD);
    }

    @Test
    void shouldHandleConcurrentEdgeOperations() throws InterruptedException {
        LocationGraph graph = new LocationGraph();
        Map<String, LocationNode> nodes = new ConcurrentHashMap<>();
        AtomicInteger successCount = new AtomicInteger(0);
        Set<String> failedEdges = Collections.newSetFromMap(new ConcurrentHashMap<>());

        // Pre-create nodes
        for (int i = 0; i < OPERATIONS_PER_THREAD * 2; i++) {
            LocationNode node = new LocationNode("Node-" + i, i % 90, i % 180);
            nodes.put(node.getId(), node);
            graph.addNode(node);
        }

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.nanoTime();

        // Submit edge creation tasks
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Random random = new Random();

                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        // Get random source and destination
                        String sourceId = "Node-" + random.nextInt(OPERATIONS_PER_THREAD * 2);
                        String destId = "Node-" + random.nextInt(OPERATIONS_PER_THREAD * 2);

                        LocationNode source = nodes.get(sourceId);
                        LocationNode dest = nodes.get(destId);

                        if (source != null && dest != null && !source.equals(dest)) {
                            LocationEdge edge = new LocationEdge(source, dest, random.nextDouble() * 10 + 1);
                            if (graph.addEdge(edge)) {
                                successCount.incrementAndGet();
                            } else {
                                failedEdges.add(String.format("Edge-%d-%d", threadId, j));
                            }
                        }
                    }
                } catch (Exception e) {
                    failedEdges.add("Thread-" + threadId + "-failed: " + e.getMessage());
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = completionLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executor.shutdown();
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);

        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

        // Performance metrics
        double operationsPerSecond = (successCount.get() * 1000.0) / duration;
        System.out.printf("Performance Metrics:%n");
        System.out.printf("Total Duration: %d ms%n", duration);
        System.out.printf("Successful Operations: %d%n", successCount.get());
        System.out.printf("Operations/second: %.2f%n", operationsPerSecond);
        System.out.printf("Failed Operations: %d%n", failedEdges.size());

        // Assertions
        assertThat(completed).as("All operations should complete within timeout").isTrue();
        assertThat(terminated).as("Executor should terminate cleanly").isTrue();
        assertThat(graph.getEdgeCount()).as("Graph should contain all successful edges").isEqualTo(successCount.get());
        assertThat(failedEdges).as("Should have no failed edge additions").isEmpty();
    }
}
