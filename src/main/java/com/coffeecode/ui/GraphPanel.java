package com.coffeecode.ui;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Getter
public class GraphPanel extends JPanel {

    private final Graph graph;
    private final Viewer viewer;
    private final ViewPanel viewPanel;
    private final JScrollPane scrollPane;

    public GraphPanel() {
        setLayout(new BorderLayout());

        // Initialize components
        initializeGraph();
        initializeViewer();

        // Setup scroll pane
        scrollPane = new JScrollPane(viewPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Add resize listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                viewPanel.setPreferredSize(scrollPane.getSize());
                viewPanel.revalidate();
            }
        });
    }

    private void initializeGraph() {
        System.setProperty("org.graphstream.ui", "swing");
        graph = new SingleGraph("Graph");
        graph.setAttribute("ui.stylesheet", """
                node {
                    size: 30px;
                    fill-color: #666666;
                    text-size: 20px;
                    text-color: white;
                    text-style: bold;
                    text-alignment: center;
                }
                node.visited {
                    fill-color: #00ff00;
                }
                node.current {
                    fill-color: #ff0000;
                }
                edge {
                    size: 2px;
                    fill-color: #999999;
                    text-size: 20px;
                }
                edge.visited {
                    fill-color: #00ff00;
                    size: 3px;
                }
                """);
    }

    private void initializeViewer() {
        viewer = graph.display(false);
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        viewer.enableAutoLayout();
        viewPanel = (ViewPanel) viewer.addDefaultView(false);
    }

    public void addNode(String id, double x, double y) {
        graph.addNode(id);
        org.graphstream.graph.Node node = graph.getNode(id);
        node.setAttribute("xy", x, y);
        node.setAttribute("ui.label", id);
    }

    public void addEdge(String id, String sourceId, String targetId, double weight) {
        graph.addEdge(id, sourceId, targetId);
        org.graphstream.graph.Edge edge = graph.getEdge(id);
        edge.setAttribute("ui.label", String.format("%.1f", weight));
    }

    public void markNodeVisited(String id) {
        graph.getNode(id).setAttribute("ui.class", "visited");
    }

    public void markEdgeVisited(String id) {
        graph.getEdge(id).setAttribute("ui.class", "visited");
    }

    public void reset() {
        graph.nodes().forEach(node -> node.removeAttribute("ui.class"));
        graph.edges().forEach(edge -> edge.removeAttribute("ui.class"));
    }

    public void clear() {
        graph.clear();
    }
}
