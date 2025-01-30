package com.coffeecode.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

import lombok.Getter;

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

    // Test method
    public void addTestNode() {
        graph.addNode("A");
        graph.getNode("A").setAttribute("ui.label", "A");
    }
}
