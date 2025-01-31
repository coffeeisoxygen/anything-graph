package com.coffeecode.event.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.coffeecode.model.LocationNode;

import lombok.Value;

public interface AlgorithmEvent {

    @Value
    class Started implements AlgorithmEvent {

        String algorithmName;
        LocationNode start;
        LocationNode end;
    }

    @Value
    class StepCompleted implements AlgorithmEvent {

        LocationNode node;
        double progress;
    }

    @Value
    class StateUpdated implements AlgorithmEvent {

        LocationNode node;
        double progress;
    }

    @Value
    class Paused implements AlgorithmEvent {

        public static final Paused INSTANCE = new Paused();

        private Paused() {
        }
    }

    @Value
    class Resumed implements AlgorithmEvent {

        public static final Resumed INSTANCE = new Resumed();

        private Resumed() {
        }
    }

    @Value
    class Completed implements AlgorithmEvent {

        Map<String, Double> metrics;

        public Completed(Map<String, Double> metrics) {
            this.metrics = Collections.unmodifiableMap(new HashMap<>(metrics));
        }
    }

    @Value
    class Failed implements AlgorithmEvent {

        String error;
    }

    @Value
    class Reset implements AlgorithmEvent {

        public static final Reset INSTANCE = new Reset();

        private Reset() {
        }
    }
}
