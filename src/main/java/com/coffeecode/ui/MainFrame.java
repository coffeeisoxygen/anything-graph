package com.coffeecode.ui;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
public class MainFrame extends JFrame {

    private MapPanel mapPanel;
    private GraphPanel graphPanel;
    private JToolBar toolBar;
    private final AtomicBoolean isRunning;
    private JComboBox<String> algorithmSelector;
    private JButton runButton;
    private JButton stopButton;
    private JSlider speedSlider;

    public MainFrame() {
        super("Graph Algorithm Visualizer");
        log.debug("Initializing main frame...");

        this.isRunning = new AtomicBoolean(false);

        try {
            initializeFrame();
            initializeComponents();
            setupLayout();
            setupEventHandlers();

            log.info("Main frame initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize main frame", e);
            throw new RuntimeException("MainFrame initialization failed", e);
        }
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 800));

        // Enable window closing event logging
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                log.info("Application shutdown requested");
            }
        });
    }

    private void initializeComponents() {
        try {
            mapPanel = new MapPanel();
            graphPanel = new GraphPanel();
            toolBar = createToolBar();
        } catch (Exception e) {
            log.error("Failed to initialize components", e);
            throw new RuntimeException("Component initialization failed", e);
        }
    }

    private JToolBar createToolBar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        algorithmSelector = new JComboBox<>(new String[]{"BFS", "DFS", "Dijkstra", "A*"});
        runButton = new JButton("Run");
        stopButton = new JButton("Stop");
        speedSlider = new JSlider(0, 100, 50);

        stopButton.setEnabled(false);

        bar.add(new JLabel("Algorithm: "));
        bar.add(algorithmSelector);
        bar.add(runButton);
        bar.add(stopButton);
        bar.add(new JLabel("Speed: "));
        bar.add(speedSlider);

        return bar;
    }

    private void setupEventHandlers() {
        runButton.addActionListener(e -> handleRunButtonClick());
        stopButton.addActionListener(e -> handleStopButtonClick());

        algorithmSelector.addActionListener(e -> {
            String selected = (String) algorithmSelector.getSelectedItem();
            log.debug("Algorithm selected: {}", selected);
        });

        speedSlider.addChangeListener(e -> {
            if (!speedSlider.getValueIsAdjusting()) {
                log.debug("Animation speed changed to: {}", speedSlider.getValue());
            }
        });
    }

    private void handleRunButtonClick() {
        if (isRunning.compareAndSet(false, true)) {
            String algorithm = (String) algorithmSelector.getSelectedItem();
            log.info("Starting algorithm: {}", algorithm);

            runButton.setEnabled(false);
            stopButton.setEnabled(true);
            algorithmSelector.setEnabled(false);

            // TODO: Implement algorithm execution through service layer
        }
    }

    private void handleStopButtonClick() {
        if (isRunning.compareAndSet(true, false)) {
            log.info("Stopping algorithm execution");

            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            algorithmSelector.setEnabled(true);

            // TODO: Implement stop functionality through service layer
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                mapPanel,
                graphPanel
        );
        splitPane.setResizeWeight(0.5);

        add(toolBar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }
}
