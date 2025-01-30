package com.coffeecode.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

public class EdgeTest {

    @Test
    void shouldCreateEdgeWithWeight() {
        LocationNode source = new LocationNode("A");
        LocationNode destination = new LocationNode("B");
        LocationEdge edge = new LocationEdge(source, destination, 10.0);

        assertThat(edge.getSource()).isEqualTo(source);
        assertThat(edge.getDestination()).isEqualTo(destination);
        assertThat(edge.getWeight()).isEqualTo(10.0);
    }

    @Test
    void shouldNotAllowNegativeWeight() {
        LocationNode source = new LocationNode("A");
        LocationNode destination = new LocationNode("B");

        assertThatThrownBy(() -> new LocationEdge(source, destination, -1.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCreateEdgeWithDefaultWeight() {
        LocationNode source = new LocationNode("A");
        LocationNode destination = new LocationNode("B");
        LocationEdge edge = new LocationEdge(source, destination);

        assertThat(edge.getWeight()).isEqualTo(1.0);
    }
}
