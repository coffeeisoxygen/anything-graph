package com.coffeecode.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

public class LoadTest {

    @Test
    void shouldHandleConcurrentOperations() throws InterruptedException {
        LocationGraph graph = new LocationGraph();
        int threadCount = 10;
        int operationsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String id = "Node-" + threadId + "-" + j;
                        LocationNode node = new LocationNode(id, j, j);
                        graph.addNode(node);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(graph.getNodeCount())
                .isEqualTo(threadCount * operationsPerThread);
    }
}
