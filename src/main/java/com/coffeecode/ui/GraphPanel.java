package com.coffeecode.ui;

import lombok.Getter;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.Serializable;

// Model imports
import com.coffeecode.model.LocationNode;
import com.coffeecode.model.LocationEdge;
import com.coffeecode.model.LocationGraph;

// GraphStream imports
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

// Java utilities
import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.IdAlreadyInUseException;

@Getter
public class GraphPanel extends JPanel implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient Graph visualGraph; // GraphStream graph marked as transient
    private transient Viewer viewer;
    private transient ViewPanel viewPanel;
    private final JScrollPane scrollPane;
    private final transient Map<String, LocationNode> nodeMapping;
    private transient LocationGraph modelGraph;
    private final GraphAnimationController animationController;

    public GraphPanel() throws GraphPanelInitializationException {
        setLayout(new BorderLayout());

        nodeMapping = new HashMap<>();
        modelGraph = new LocationGraph();

        try {
            initializeGraph();
            initializeViewer();
            scrollPane = new JScrollPane(viewPanel);
            add(scrollPane, BorderLayout.CENTER);
            addResizeListener();
            animationController = new GraphAnimationController(this);
        } catch (Exception e) {
            throw new GraphPanelInitializationException("Failed to initialize graph panel", e);
        }
    }

    private void initializeGraph() {
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        visualGraph = new SingleGraph("VisualGraph");
        visualGraph.setAttribute("ui.quality");
        visualGraph.setAttribute("ui.antialias");
        visualGraph.setAttribute("ui.stylesheet", GraphStylesheet.getDefaultStylesheet());
    }

    // Called after deserialization
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initializeGraph(); // Reinitialize the graph
    }

    private void initializeViewer() {
        viewer = visualGraph.display(false);
        viewer.enableAutoLayout();
        viewPanel = (ViewPanel) viewer.addDefaultView(false);
    }

    private void addResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                viewPanel.setPreferredSize(scrollPane.getSize());
                viewPanel.revalidate();
            }
        });
    }

    public void addNode(LocationNode node) {
        try {
            String id = node.getId();
            if (nodeMapping.containsKey(id)) {
                return; // Node already exists
            }

            nodeMapping.put(id, node);
            modelGraph.addNode(node);

            synchronized (visualGraph) {
                visualGraph.addNode(id);
                org.graphstream.graph.Node visualNode = visualGraph.getNode(id);
                visualNode.setAttribute("xy", node.getLatitude(), node.getLongitude());
                visualNode.setAttribute("ui.label", id);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new GraphPanelInitializationException("Failed to add node: " + node.getId(), e);
        }
    }

    public void addEdge(LocationEdge edge) {
        try {
            String sourceId = edge.getSource().getId();
            String targetId = edge.getDestination().getId();
            String id = sourceId + "-" + targetId;

            if (visualGraph.getEdge(id) != null) {
                return; // Edge already exists
            }

            modelGraph.addEdge(edge);

            synchronized (visualGraph) {
                visualGraph.addEdge(id, sourceId, targetId);
                org.graphstream.graph.Edge visualEdge = visualGraph.getEdge(id);
                visualEdge.setAttribute("ui.label", String.format("%.1f", edge.getWeight()));
            }
        } catch (EdgeRejectedException | ElementNotFoundException | IdAlreadyInUseException e) {
            throw new GraphPanelInitializationException("Failed to add edge", e);
        }
    }

    public void setNodeState(String nodeId, String state) {
        org.graphstream.graph.Node node = visualGraph.getNode(nodeId);
        if (node != null) {
            node.setAttribute("ui.class", state);
        }
    }

    public void setEdgeState(String sourceId, String targetId, String state) {
        String edgeId = sourceId + "-" + targetId;
        org.graphstream.graph.Edge edge = visualGraph.getEdge(edgeId);
        if (edge != null) {
            edge.setAttribute("ui.class", state);
        }
    }

    public void resetStates() {
        visualGraph.nodes().forEach(node -> node.removeAttribute("ui.class"));
        visualGraph.edges().forEach(edge -> edge.removeAttribute("ui.class"));
    }

    public void clear() {
        synchronized (visualGraph) {
            visualGraph.clear();
            nodeMapping.clear();
            modelGraph = new LocationGraph();
        }
    }

    public void setAnimationSpeed(int speed) {
        animationController.setSpeed(speed);
    }

    public void pauseAnimation() {
        animationController.pause();
    }

    public void resumeAnimation() {
        animationController.resume();
    }
}
