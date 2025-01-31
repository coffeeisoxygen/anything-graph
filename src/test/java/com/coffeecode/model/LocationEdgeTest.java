package com.coffeecode.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationEdgeTest {

    private LocationNode source;
    private LocationNode destination;

    @BeforeEach
    void setUp() {
        source = new LocationNode("A", 1.0, 1.0);
        destination = new LocationNode("B", 2.0, 2.0);
    }

    @Test
    void shouldCreateEdgeWithValidWeight() {
        LocationEdge edge = new LocationEdge(source, destination, 10.0);

        assertThat(edge.getSource()).isEqualTo(source);
        assertThat(edge.getDestination()).isEqualTo(destination);
        assertThat(edge.getWeight()).isEqualTo(10.0);
    }

    @Test
    void shouldValidateEndpoints() {
        // Null source
        assertThatThrownBy(() -> new LocationEdge(null, destination, 1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("source is marked non-null but is null");

        // Null destination
        assertThatThrownBy(() -> new LocationEdge(source, null, 1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("destination is marked non-null but is null");
    }

    @Test
    void shouldValidateWeight() {
        // Negative weight
        assertThatThrownBy(() -> new LocationEdge(source, destination, -1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Weight must be positive");

        // Zero weight
        assertThatThrownBy(() -> new LocationEdge(source, destination, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Weight must be positive");
    }

    @Test
    void shouldHaveConsistentEqualsAndHashCode() {
        LocationEdge edge1 = new LocationEdge(source, destination, 1.0);
        LocationEdge edge2 = new LocationEdge(source, destination, 2.0);
        LocationEdge edge3 = new LocationEdge(destination, source, 1.0);

        assertThat(edge1)
                .isEqualTo(edge2) // Same source/dest -> equal
                .isNotEqualTo(edge3) // Different source/dest -> not equal
                .hasSameHashCodeAs(edge2);
    }

    @Test
    void shouldHaveReadableToString() {
        LocationEdge edge = new LocationEdge(source, destination, 1.0);

        assertThat(edge.toString())
                .contains(source.getId())
                .contains(destination.getId())
                .contains("1.0");
    }
}
