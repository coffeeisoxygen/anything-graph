package com.coffeecode.ui.toolbar;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;

import lombok.Getter;

@Getter
public class ToolbarPanel extends JToolBar {

    private final JComboBox<String> algorithmSelector;
    private final JButton runButton;
    private final JButton stopButton;
    private final JSlider speedSlider;

    public ToolbarPanel() {
        setFloatable(false);

        // Initialize components
        algorithmSelector = new JComboBox<>(new String[]{"BFS", "DFS", "Dijkstra", "A*"});
        runButton = new JButton("Run");
        stopButton = new JButton("Stop");
        speedSlider = new JSlider(0, 100, 50);

        setupLayout();
        setupSlider();
    }

    private void setupLayout() {
        // Algorithm selection
        add(new JLabel("Algorithm: "));
        add(algorithmSelector);
        addSeparator();

        // Control buttons
        add(runButton);
        add(stopButton);
        addSeparator();

        // Speed control
        add(new JLabel("Speed: "));
        add(speedSlider);
    }

    private void setupSlider() {
        speedSlider.setPreferredSize(new Dimension(100, 20));
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(20);
    }
}
