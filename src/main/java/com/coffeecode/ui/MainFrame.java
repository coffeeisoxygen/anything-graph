package com.coffeecode.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.coffeecode.listener.NodeChangeListener;
import com.coffeecode.model.LocationNode;
import com.coffeecode.ui.map.MapPanel;
import com.coffeecode.ui.panelgraph.GraphPanel;
import com.coffeecode.ui.service.MainFrameService;
import com.coffeecode.ui.toolbar.ToolbarPanel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MainFrame extends JFrame {

    private final MapPanel mapPanel;
    private final GraphPanel graphPanel;
    private final ToolbarPanel toolbar;
    private final MainFrameService service;

    public MainFrame() {
        super("Graph Algorithm Visualizer");
        log.debug("Initializing main frame...");

        // Initialize components
        service = new MainFrameService();
        toolbar = new ToolbarPanel();
        mapPanel = new MapPanel();
        graphPanel = new GraphPanel();

        setupLayout();
        setupEventHandlers();
        setupWindowProperties();

        log.info("Main frame initialized successfully");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create split pane
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                mapPanel,
                graphPanel
        );
        splitPane.setResizeWeight(0.5);

        // Add components
        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        mapPanel.addNodeChangeListener(new NodeChangeListener() {
            @Override
            public void onNodeAdded(LocationNode node) {
                service.getLocationGraph().addNode(node);
                graphPanel.updateGraph(service.getLocationGraph());
            }

            @Override
            public void onNodeRemoved(LocationNode node) {
                service.getLocationGraph().removeNode(node);
                graphPanel.updateGraph(service.getLocationGraph());
            }

            @Override
            public void onStartNodeChanged(LocationNode node) {
                // service.getLocationGraph().setStartNode(node);
                // graphPanel.updateGraph(service.getLocationGraph());
            }

            @Override
            public void onEndNodeChanged(LocationNode node) {
                // service.getLocationGraph().setEndNode(node);
                // graphPanel.updateGraph(service.getLocationGraph());
            }

        });
    }

    private void setupWindowProperties() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 800));
        pack();
        setLocationRelativeTo(null);
    }
}
