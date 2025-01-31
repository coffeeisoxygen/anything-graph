package com.coffeecode.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LocationNodeTest {

    @Test
    void shouldCreateValidLocationNode() {
        LocationNode node = new LocationNode("A", 40.7128, -74.0060);

        assertThat(node.getId()).isEqualTo("A");
        assertThat(node.getLatitude()).isEqualTo(40.7128);
        assertThat(node.getLongitude()).isEqualTo(-74.0060);
    }

    @ParameterizedTest
    @CsvSource({
        "-91.0, 0.0, Latitude must be between -90 and 90",
        "91.0, 0.0, Latitude must be between -90 and 90",
        "0.0, -181.0, Longitude must be between -180 and 180",
        "0.0, 181.0, Longitude must be between -180 and 180"
    })
    void shouldValidateCoordinateBoundaries(double latitude, double longitude, String message) {
        assertThatThrownBy(() -> new LocationNode("A", latitude, longitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(message);
    }

    @Test
    void shouldValidateId() {
        // Null ID
        assertThatThrownBy(() -> new LocationNode(null, 0.0, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null or empty");

        // Empty ID
        assertThatThrownBy(() -> new LocationNode("", 0.0, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null or empty");

        // Blank ID
        assertThatThrownBy(() -> new LocationNode("   ", 0.0, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null or empty");
    }

    @Test
    void shouldHaveConsistentEqualsAndHashCode() {
        LocationNode node1 = new LocationNode("A", 1.0, 1.0);
        LocationNode node2 = new LocationNode("A", 2.0, 2.0);
        LocationNode node3 = new LocationNode("B", 1.0, 1.0);

        assertThat(node1)
                .isEqualTo(node2) // Same ID -> equal
                .isNotEqualTo(node3) // Different ID -> not equal
                .hasSameHashCodeAs(node2);
    }

    @Test
    void shouldHaveReadableToString() {
        LocationNode node = new LocationNode("A", 1.0, 2.0);

        assertThat(node.toString())
                .contains("A")
                .contains("1.0")
                .contains("2.0");
    }
}
