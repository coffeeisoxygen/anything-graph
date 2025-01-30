package com.coffeecode.ui;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.coffeecode.model.LocationNode;

class MapPanelTest {

    @Test
    void shouldInitializeWithDefaultComponents() {
        MapPanel mapPanel = new MapPanel();

        assertThat(mapPanel.getMapViewer()).isNotNull();
        assertThat(mapPanel.getNodes()).isEmpty();
        assertThat(mapPanel.getPainters()).isEmpty();
    }

    @Test
    void shouldAddNode() {
        MapPanel mapPanel = new MapPanel();
        LocationNode node = new LocationNode("Test", 0.0, 0.0);

        mapPanel.addNode(node);

        assertThat(mapPanel.getNodes()).hasSize(1);
        assertThat(mapPanel.getNodes()).contains(node);
    }
}
