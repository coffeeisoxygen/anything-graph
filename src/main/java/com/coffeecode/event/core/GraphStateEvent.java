package com.coffeecode.event.core;

import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationNode;

import lombok.Getter;

public sealed interface GraphStateEvent {

    @Getter
    final class NodeVisited implements GraphStateEvent {

        private final LocationNode node;

        public NodeVisited(LocationNode node) {
            this.node = node;
        }
    }

    @Getter
    final class NodeProcessing implements GraphStateEvent {

        private final LocationNode node;

        public NodeProcessing(LocationNode node) {
            this.node = node;
        }
    }

    @Getter
    final class EdgeVisited implements GraphStateEvent {

        private final LocationEdge edge;

        public EdgeVisited(LocationEdge edge) {
            this.edge = edge;
        }
    }

    @Getter
    final class EdgeProcessing implements GraphStateEvent {

        private final LocationEdge edge;

        public EdgeProcessing(LocationEdge edge) {
            this.edge = edge;
        }
    }

    @Getter
    final class DistanceUpdated implements GraphStateEvent {

        private final LocationNode node;
        private final double distance;

        public DistanceUpdated(LocationNode node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    final class StateCleared implements GraphStateEvent {

        private StateCleared() {
        }
        public static final StateCleared INSTANCE = new StateCleared();
    }
}
