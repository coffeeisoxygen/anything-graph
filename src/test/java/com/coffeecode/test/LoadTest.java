package com.coffeecode.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

public class LoadTest {

    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    private static final int OPERATIONS_PER_THREAD = 1000;
    private static final int TIMEOUT_SECONDS = 60;

    @Test
    @Timeout(value = TIMEOUT_SECONDS + 5, unit = TimeUnit.SECONDS)
    void shouldHandleConcurrentNodeOperations() throws InterruptedException {
        LocationGraph graph = new LocationGraph();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(THREAD_COUNT);
        Set<String> failedNodes = Collections.newSetFromMap(new ConcurrentHashMap<>());
        AtomicInteger successCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
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

        startLatch.countDown();
        boolean completed = completionLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executor.shutdown();
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(completed).as("All operations should complete within timeout").isTrue();
        assertThat(terminated).as("Executor should terminate cleanly").isTrue();
        assertThat(graph.getNodeCount()).as("Graph should contain all successfully added nodes").isEqualTo(successCount.get());
        assertThat(failedNodes).as("Should have no failed node additions").isEmpty();
        assertThat(successCount.get()).as("Should successfully add all nodes").isEqualTo(THREAD_COUNT * OPERATIONS_PER_THREAD);
    }

    @Test
    @Timeout(value = TIMEOUT_SECONDS + 5, unit = TimeUnit.SECONDS)
    void shouldHandleConcurrentEdgeOperations() throws InterruptedException {
        LocationGraph graph = new LocationGraph();
        List<LocationNode> nodes = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        Set<String> failedEdges = Collections.newSetFromMap(new ConcurrentHashMap<>());

        for (int i = 0; i < THREAD_COUNT * OPERATIONS_PER_THREAD; i++) {
            LocationNode node = new LocationNode("Node-" + i, (i % 90), (i % 180));
            nodes.add(node);
            graph.addNode(node);
        }

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.nanoTime();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            final int startIdx = threadId * OPERATIONS_PER_THREAD;

            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < OPERATIONS_PER_THREAD - 1; j++) {
                        int sourceIdx = startIdx + j;
                        int destIdx = startIdx + j + 1;

                        if (sourceIdx < nodes.size() && destIdx < nodes.size()) {
                            LocationNode source = nodes.get(sourceIdx);
                            LocationNode dest = nodes.get(destIdx);

                            LocationEdge edge = new LocationEdge(source, dest, 1.0 + (j % 10));
                            if (graph.addEdge(edge)) {
                                successCount.incrementAndGet();
                            } else {
                                String msg = String.format("Edge-%d-%d (source:%s, dest:%s)", threadId, j, source.getId(), dest.getId());
                                failedEdges.add(msg);
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

        double operationsPerSecond = (successCount.get() * 1000.0) / duration;
        System.out.printf("Edge Test Performance:%n");
        System.out.printf("Total Duration: %d ms%n", duration);
        System.out.printf("Successful Edges: %d%n", successCount.get());
        System.out.printf("Operations/second: %.2f%n", operationsPerSecond);
        System.out.printf("Failed Edges: %d%n", failedEdges.size());

        if (!failedEdges.isEmpty()) {
            System.out.println("Failed Edge Details:");
            failedEdges.forEach(System.out::println);
        }

        assertThat(completed).as("All operations should complete within timeout").isTrue();
        assertThat(terminated).as("Executor should terminate cleanly").isTrue();
        assertThat(graph.getEdgeCount()).as("Graph should contain all successful edges").isEqualTo(successCount.get());
        assertThat(failedEdges).as("Should have no failed edge additions").isEmpty();
    }
}
