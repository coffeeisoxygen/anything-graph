package com.coffeecode.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.coffeecode.ui.panelgraph.GraphPanel;
import com.coffeecode.ui.service.MainFrameService;
import com.coffeecode.ui.toolbar.ToolbarPanel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MainFrame extends JFrame {

    private final GraphPanel graphPanel;
    private final ToolbarPanel toolbar;
    private final MainFrameService service;

    public MainFrame() {
        super("Graph Algorithm Visualizer");
        log.debug("Initializing main frame...");

        // Initialize components
        service = new MainFrameService();
        toolbar = new ToolbarPanel();
        graphPanel = new GraphPanel();

        setupLayout();
        setupWindowProperties();

        log.info("Main frame initialized successfully");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create split pane
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                graphPanel,
                new JPanel() // Placeholder for the second component
        );
        splitPane.setResizeWeight(0.5);

        // Add components
        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void setupWindowProperties() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 800));
        pack();
        setLocationRelativeTo(null);
    }
}
