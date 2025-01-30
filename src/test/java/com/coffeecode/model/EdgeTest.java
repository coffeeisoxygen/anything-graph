package com.coffeecode.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

public class EdgeTest {

    @Test
    void shouldCreateEdgeWithWeight() {
        Node source = new Node("A");
        Node destination = new Node("B");
        Edge edge = new Edge(source, destination, 10.0);

        assertThat(edge.getSource()).isEqualTo(source);
        assertThat(edge.getDestination()).isEqualTo(destination);
        assertThat(edge.getWeight()).isEqualTo(10.0);
    }

    @Test
    void shouldNotAllowNegativeWeight() {
        Node source = new Node("A");
        Node destination = new Node("B");

        assertThatThrownBy(() -> new Edge(source, destination, -1.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCreateEdgeWithDefaultWeight() {
        Node source = new Node("A");
        Node destination = new Node("B");
        Edge edge = new Edge(source, destination);

        assertThat(edge.getWeight()).isEqualTo(1.0);
    }
}
