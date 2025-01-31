package com.coffeecode.event;

import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationNode;

import lombok.Getter;

public sealed interface GraphEvent {

    @Getter
    final class NodeAdded implements GraphEvent {

        private final LocationNode node;

        public NodeAdded(LocationNode node) {
            this.node = node;
        }
    }

    @Getter
    final class NodeRemoved implements GraphEvent {

        private final LocationNode node;

        public NodeRemoved(LocationNode node) {
            this.node = node;
        }
    }

    @Getter
    final class EdgeAdded implements GraphEvent {

        private final LocationEdge edge;

        public EdgeAdded(LocationEdge edge) {
            this.edge = edge;
        }
    }

    @Getter
    final class EdgeRemoved implements GraphEvent {

        private final LocationEdge edge;

        public EdgeRemoved(LocationEdge edge) {
            this.edge = edge;
        }
    }

    /**
     * Event indicating entire graph was cleared
     */
    final class GraphCleared implements GraphEvent {

        private GraphCleared() {
        } // Prevents external instantiation

        // Singleton instance since this event has no state
        public static final GraphCleared INSTANCE = new GraphCleared();
    }
}
