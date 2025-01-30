package com.coffeecode.ui;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewPanel;
import org.graphstream.ui.swing_viewer.SwingViewer;

import javax.swing.*;

import java.awt.*;
import java.io.Serializable;


@SuppressWarnings("serial")
public class GraphPanel extends JPanel implements Serializable {

    private transient Graph visualGraph;
    private transient Viewer viewer;  // Changed from final to allow reconstruction
    private transient ViewPanel viewPanel;

    public GraphPanel() {
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        System.setProperty("org.graphstream.ui", "swing");

        // Initialize graph and viewer
        visualGraph = new SingleGraph("visualization");
        // Use SwingViewer instead of abstract Viewer
        viewPanel = (ViewPanel) viewer.addDefaultView(false);  // false means no JFrame is created
        ViewPanel viewPanel = viewer.addDefaultView(false);  // false means no JFrame is created
        add(viewPanel, BorderLayout.CENTER);
        setupGraphStyle();
    }

    private void setupGraphStyle() {
        visualGraph.setAttribute("ui.stylesheet", """
            node {
                size: 30px;
                fill-color: #666666;
                text-size: 14px;
            }
            node.visited {
                fill-color: #00ff00;
            }
            edge {
                size: 2px;
                fill-color: #999999;
                text-size: 14px;
            }
            """);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        initialize();
    }
}
