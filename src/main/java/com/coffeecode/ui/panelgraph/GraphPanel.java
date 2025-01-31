package com.coffeecode.ui.panelgraph;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

import com.coffeecode.model.LocationGraph;
import com.coffeecode.service.GraphConverter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GraphPanel extends JPanel {

    private final Graph graph;
    private final SwingViewer viewer;
    private final ViewPanel viewPanel;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setLayout(new BorderLayout());

        // Enable CSS styling
        System.setProperty("org.graphstream.ui", "swing");
        graph.setAttribute("ui.stylesheet",
                "node { size: 20px; fill-color: #666666; text-size: 14; }"
                + "node.visited { fill-color: #00ff00; }"
                + "edge { size: 2px; }"
        );

        // Create viewer
        viewer = new SwingViewer(graph,
                org.graphstream.ui.view.Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        // Add view panel
        viewPanel = (ViewPanel) viewer.addDefaultView(false);
        add(viewPanel, BorderLayout.CENTER);

        log.debug("Graph panel initialized");
    }

    /**
     * Updates the visualization with a new LocationGraph
     */
    public void updateGraph(LocationGraph locationGraph) {
        try {
            GraphConverter.updateGraphVisualization(locationGraph, graph);

            // Center the view
            viewer.getDefaultView().getCamera().resetView();

        } catch (Exception e) {
            log.error("Failed to update graph visualization", e);
            throw new GraphPanelInitializationException("Graph update failed", e);
        }
    }

    public void refresh() {
        viewer.getGraphicGraph().setAttribute("ui.repaint");
    }
}
