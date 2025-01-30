package com.coffeecode.ui;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import javax.swing.*;
import java.awt.*;

@Slf4j
@Getter
public class MainFrame extends JFrame {
    private final MapPanel mapPanel;
    private final GraphPanel graphPanel;
    private final JToolBar toolBar;

    public MainFrame() {
        super("Graph Algorithm Visualizer");
        log.debug("Initializing main frame...");
        
        initializeFrame();
        initializeComponents();
        setupLayout();
        
        log.info("Main frame initialized successfully");
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 800));
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
    
    private JToolBar createToolBar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JComboBox<String> algorithmSelector = new JComboBox<>(
                new String[]{"BFS", "DFS", "Dijkstra", "A*"}
        );

        JButton runButton = new JButton("Run");
        JButton stopButton = new JButton("Stop");
        JSlider speedSlider = new JSlider(0, 100, 50);

        runButton.addActionListener(e -> {
            String algorithm = (String) algorithmSelector.getSelectedItem();
            // TODO: Implement algorithm execution
        });

        stopButton.addActionListener(e -> {
            // TODO: Implement stop functionality
        });

        bar.add(new JLabel("Algorithm: "));
        bar.add(algorithmSelector);
        bar.add(runButton);
        bar.add(stopButton);
        bar.add(new JLabel("Speed: "));
        bar.add(speedSlider);

        return bar;
    }

    public static void main(String[] args) {
        try {
            System.setProperty("org.graphstream.ui", "swing");
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );

            SwingUtilities.invokeLater(() -> {
                new MainFrame().setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
