package com.coffeecode.ui;

import javax.swing.*;
import java.awt.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewPanel;
import org.graphstream.ui.view.util.DefaultMouseManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GraphPanel extends JPanel {

    private Graph graph;
    private Viewer viewer;
    private ViewPanel viewPanel;

    public GraphPanel() {
        setLayout(new BorderLayout());
        initializeGraph();
        initializeViewer();

        JScrollPane scrollPane = new JScrollPane(viewPanel);
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(600, 400));

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
        viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        viewPanel = viewer.addDefaultView(true);

        DefaultMouseManager mouseManager = new DefaultMouseManager();
        viewPanel.addMouseListener(mouseManager);
        viewPanel.addMouseMotionListener(mouseManager);
    }

    public void addNode(String id, double x, double y) {
        SwingUtilities.invokeLater(() -> { // Ensure EDT access
            Node node = graph.addNode(id);
            node.setAttribute("ui.label", id);
            node.setAttribute("xy", x, y);
        });
    }

    public void addEdge(String id, String sourceId, String targetId, double weight) {
        SwingUtilities.invokeLater(() -> { // Ensure EDT access
            Edge edge = graph.addEdge(id, sourceId, targetId);
            edge.setAttribute("ui.label", String.format("%.1f", weight));
        });
    }

    public void markNodeAsVisited(String id) {
        SwingUtilities.invokeLater(() -> { // Ensure EDT access
            Node node = graph.getNode(id);
            if (node != null) {  // Check for null node
                node.setAttribute("ui.class", "visited");
            }
        });
    }

    public void markNodeAsCurrent(String id) {
        SwingUtilities.invokeLater(() -> { // Ensure EDT access
            Node node = graph.getNode(id);
            if (node != null) { // Check for null node
                node.setAttribute("ui.class", "current");
            }
        });
    }

    public void markEdgeAsVisited(String id) {
        SwingUtilities.invokeLater(() -> { // Ensure EDT access
            Edge edge = graph.getEdge(id);
            if (edge != null) { // Check for null edge
                edge.setAttribute("ui.class", "visited");
            }
        });
    }

    public void clearGraph() {
        SwingUtilities.invokeLater(() -> graph.clear());
    }

    public Graph getGraph() {
        return graph;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // Initialize on EDT
            JFrame frame = new JFrame("Graph Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GraphPanel graphPanel = new GraphPanel();
            frame.add(graphPanel);

            // Example usage (add nodes and edges):
            graphPanel.addNode("A", 100, 150);
            graphPanel.addNode("B", 300, 150);
            graphPanel.addEdge("AB", "A", "B", 2.5);
            graphPanel.addNode("C", 200, 300);
            graphPanel.addEdge("AC", "A", "C", 1.8);
            graphPanel.addEdge("BC", "B", "C", 3.2);

            frame.pack();
            frame.setVisible(true);
        });
    }
}
