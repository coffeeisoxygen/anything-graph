package com.coffeecode.ui;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class GraphPanelTest {

    @Test
    void shouldInitializeGraphPanel() {
        GraphPanel panel = new GraphPanel();

        assertThat(panel.getGraph()).isNotNull();
        assertThat(panel.getViewer()).isNotNull();
        assertThat(panel.getViewPanel()).isNotNull();
    }
}
