package com.coffeecode.model;

import lombok.*;
import java.util.*;

@Data
@Builder
public class PointData {

    public PointData(List<PointNode> nodes, List<PointEdge> edges, boolean directed, double maxDistance) {
        this.nodes = nodes != null ? nodes : new ArrayList<>();
        this.edges = edges != null ? edges : new ArrayList<>();
        this.directed = directed;
        this.maxDistance = maxDistance;
    }

    @Getter
    private final List<PointNode> nodes;
    @Getter
    private final List<PointEdge> edges;

    @Builder.Default
    private boolean directed = false;

    @Builder.Default
    private double maxDistance = 100.0;

    public PointData() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public boolean addNode(PointNode node) {
        if (nodes.stream().anyMatch(n -> n.getId().equals(node.getId()))) {
            return false;
        }
        return nodes.add(node);
    }

    public boolean addEdge(PointNode source, PointNode target) {
        if (!nodes.contains(source) || !nodes.contains(target)) {
            return false;
        }

        PointEdge edge = new PointEdge(source, target);
        if (edge.getWeight() > maxDistance) {
            return false;
        }

        edges.add(edge);
        if (!directed) {
            edges.add(new PointEdge(target, source));
        }
        return true;
    }

    public void clear() {
        nodes.forEach(node -> {
            node.setVisited(false);
            node.setParent(null);
        });
        edges.forEach(edge -> edge.setVisited(false));
    }
}
