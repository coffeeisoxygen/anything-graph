package com.coffeecode.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;
import com.coffeecode.model.LocationNode;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
public class CoreBenchmarkTest {

    private LocationNode[] nodes;
    private LocationEdge[] edges;
    private LocationGraph graph;

    @Setup
    public void setup() {
        nodes = new LocationNode[1000];
        edges = new LocationEdge[1000];
        graph = new LocationGraph();

        // Prepare test data
        for (int i = 0; i < 1000; i++) {
            nodes[i] = new LocationNode(
                    "Node" + i,
                    Math.random() * 180 - 90,
                    Math.random() * 360 - 180
            );
        }

        for (int i = 0; i < 999; i++) {
            edges[i] = new LocationEdge(nodes[i], nodes[i + 1], i + 1.0);
        }
    }

    @Benchmark
    public void nodeCreationBenchmark() {
        new LocationNode("Test", 0.0, 0.0);
    }

    @Benchmark
    public void edgeCreationBenchmark() {
        new LocationEdge(nodes[0], nodes[1], 1.0);
    }

    @Benchmark
    public void graphAddNodeBenchmark() {
        graph.addNode(new LocationNode("Test", 0.0, 0.0));
    }

    @Benchmark
    public void graphAddEdgeBenchmark() {
        graph.addEdge(new LocationEdge(nodes[0], nodes[1], 1.0));
    }

    @Benchmark
    public void graphOperationsBenchmark() {
        LocationNode source = nodes[0];
        LocationNode target = nodes[999];
        graph.getEdges(source);
        graph.hasNode(target);
        graph.getNodes();
    }
}
