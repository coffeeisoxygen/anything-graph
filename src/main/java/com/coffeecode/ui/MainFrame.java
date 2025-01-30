package com.coffeecode.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import lombok.Getter;

@Getter
public class MainFrame extends JFrame {

    private final MapPanel mapPanel;
    private final GraphPanel graphPanel;
    private final JToolBar toolBar;
    private final JSplitPane splitPane;

    public MainFrame() {
        super("Graph Algorithm Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize components
        mapPanel = new MapPanel();
        graphPanel = new GraphPanel();
        toolBar = createToolBar();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapPanel, graphPanel);

        // Layout setup
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Window settings
        setSize(1200, 800);
        splitPane.setDividerLocation(0.5);
        setLocationRelativeTo(null);
    }

    private JToolBar createToolBar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JComboBox<String> algorithmSelector = new JComboBox<>(new String[]{
            "BFS", "DFS", "Dijkstra", "A*"
        });

        JButton runButton = new JButton("Run");
        JButton stopButton = new JButton("Stop");
        JSlider speedSlider = new JSlider(0, 100, 50);

        bar.add(new JLabel("Algorithm: "));
        bar.add(algorithmSelector);
        bar.add(runButton);
        bar.add(stopButton);
        bar.add(new JLabel("Speed: "));
        bar.add(speedSlider);

        return bar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
