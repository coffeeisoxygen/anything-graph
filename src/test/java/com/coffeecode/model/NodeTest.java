package com.coffeecode.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class NodeTest {

    @Test
    void shouldCreateNodeWithId() {
        Node node = new Node("A");
        assertThat(node.getId()).isEqualTo("A");
    }

    @Test
    void shouldCreateNodeWithCoordinates() {
        Node node = new Node("A", 1.0, 2.0);
        assertThat(node.getLatitude()).isEqualTo(1.0);
        assertThat(node.getLongitude()).isEqualTo(2.0);
    }

    @Test
    void shouldNotAllowNullId() {
        assertThatThrownBy(() -> new Node(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldEqualNodesWithSameId() {
        Node node1 = new Node("A", 1.0, 2.0);
        Node node2 = new Node("A", 3.0, 4.0);
        assertThat(node1).isEqualTo(node2);
    }
}
