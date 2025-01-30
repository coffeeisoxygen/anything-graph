package com.coffeecode.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class NodeTest {

    @Test
    void shouldCreateNodeWithId() {
        LocationNode node = new LocationNode("A");
        assertThat(node.getId()).isEqualTo("A");
    }

    @Test
    void shouldCreateNodeWithCoordinates() {
        LocationNode node = new LocationNode("A", 1.0, 2.0);
        assertThat(node.getLatitude()).isEqualTo(1.0);
        assertThat(node.getLongitude()).isEqualTo(2.0);
    }

    @Test
    void shouldNotAllowNullId() {
        assertThatThrownBy(() -> new LocationNode(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldEqualNodesWithSameId() {
        LocationNode node1 = new LocationNode("A", 1.0, 2.0);
        LocationNode node2 = new LocationNode("A", 3.0, 4.0);
        assertThat(node1).isEqualTo(node2);
    }
}
