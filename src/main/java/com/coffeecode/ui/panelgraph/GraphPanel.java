package com.coffeecode.ui.panelgraph;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

import com.coffeecode.model.LocationGraph;
import com.coffeecode.ui.exception.GraphPanelInitializationException;
import com.coffeecode.util.GraphConverter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GraphPanel extends JPanel {

    static {
        // Must be set before any GraphStream object creation
        System.setProperty("org.graphstream.ui", "swing");
    }

    private final Graph graph;
    private final SwingViewer viewer;
    private final ViewPanel viewPanel;

    public GraphPanel() {
        setLayout(new BorderLayout());

        graph = new SingleGraph("MainGraph");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        viewer = new SwingViewer(graph,
                SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        viewPanel = (ViewPanel) viewer.addDefaultView(false);
        add(viewPanel, BorderLayout.CENTER);

        applyDefaultStyle();
    }

    private void applyDefaultStyle() {
        graph.setAttribute("ui.stylesheet", """
            graph { padding: 50px; }
            node {
                size: 30px;
                fill-color: #666666;
                text-size: 14px;
                text-color: white;
                text-style: bold;
            }
            edge {
                size: 2px;
                fill-color: #999999;
                text-size: 14px;
            }
        """);
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
}
