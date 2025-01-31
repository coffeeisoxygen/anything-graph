package com.coffeecode.ui.toolbar;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.coffeecode.core.GraphResult;
import com.coffeecode.ui.service.MainFrameService;

import lombok.Getter;

@Getter
public class ToolbarPanel extends JToolBar {

    private final JComboBox<String> algorithmSelector;
    private final JComboBox<String> algorithmType;
    private final JButton stopButton;
    private final JSlider speedSlider;
    private final JButton addNodeButton;
    private final JButton addBatchNodeButton;
    private final JButton removeNodeButton;
    private final JButton addEdgeButton;
    private final JButton removeEdgeButton;
    private final JButton removeAllEdgesButton;
    private final JButton clearAllButton;
    private final JButton playButton;
    private final JButton pauseButton;
    private final JButton nextStepButton;
    private final JButton prevStepButton;
    private final JButton skipToEndButton;
    private final transient MainFrameService service;

    public ToolbarPanel(MainFrameService service) {
        this.service = service;
        setFloatable(false);

        // Initialize components
        algorithmType = new JComboBox<>(new String[]{"PathFinding", "ShortestPath"});
        algorithmSelector = new JComboBox<>(new String[]{"BFS", "DFS", "Dijkstra", "A*"});
        speedSlider = new JSlider(0, 100, 50);
        addNodeButton = new JButton("Add Node");
        addBatchNodeButton = new JButton("Add Batch Node");
        removeNodeButton = new JButton("Remove Node");
        removeAllEdgesButton = new JButton("Remove All Edges");
        addEdgeButton = new JButton("Add Edge");
        removeEdgeButton = new JButton("Remove Edge");
        clearAllButton = new JButton("Clear All");
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        nextStepButton = new JButton("Next Step");
        prevStepButton = new JButton("Prev Step");
        skipToEndButton = new JButton("Skip to End");

        setupLayout();
        setupSlider();
        setupButtonActions();
    }

    private void setupLayout() {
        // Node control panel
        JPanel nodeControlPanel = new JPanel();
        nodeControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        nodeControlPanel.add(addNodeButton);
        nodeControlPanel.add(addBatchNodeButton);
        nodeControlPanel.add(removeNodeButton);

        // Edge control panel
        JPanel edgeControlPanel = new JPanel();
        edgeControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        edgeControlPanel.add(addEdgeButton);
        edgeControlPanel.add(removeEdgeButton);
        edgeControlPanel.add(removeAllEdgesButton);

        // Selector and visualization control panel
        JPanel selectorControlPanel = new JPanel();
        selectorControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        selectorControlPanel.add(new JLabel("Algorithm: "));
        selectorControlPanel.add(algorithmType);
        selectorControlPanel.add(algorithmSelector);
        selectorControlPanel.add(playButton);
        selectorControlPanel.add(pauseButton);
        selectorControlPanel.add(stopButton);
        selectorControlPanel.add(nextStepButton);
        selectorControlPanel.add(prevStepButton);
        selectorControlPanel.add(skipToEndButton);
        selectorControlPanel.add(new JLabel("Speed: "));
        selectorControlPanel.add(speedSlider);
        selectorControlPanel.add(clearAllButton);

        // Add panels to toolbar
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(nodeControlPanel);
        add(edgeControlPanel);
        add(selectorControlPanel);
    }

    private void setupSlider() {
        speedSlider.setPreferredSize(new Dimension(100, 20));
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(20);
    }

    private void setupButtonActions() {
        addNodeButton.addActionListener(e -> handleAddNode());
        addBatchNodeButton.addActionListener(e -> handleAddBatchNode());
        removeNodeButton.addActionListener(e -> handleRemoveNode());
        removeAllEdgesButton.addActionListener(e -> handleClearAllEdges());
        addEdgeButton.addActionListener(e -> handleAddEdge());
        removeEdgeButton.addActionListener(e -> handleRemoveEdge());
        clearAllButton.addActionListener(e -> handleClearAll());
        playButton.addActionListener(e -> handlePlay());
        pauseButton.addActionListener(e -> handlePause());
        stopButton.addActionListener(e -> handleStop());
        nextStepButton.addActionListener(e -> handleNextStep());
        prevStepButton.addActionListener(e -> handlePrevStep());
        skipToEndButton.addActionListener(e -> handleSkipToEnd());
    }

    // Placeholder methods for button actions
    private void handleAddNode() {
        AddNodePopup.showPopup(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                service
        );
    }

    private void handleAddBatchNode() {
        AddBatchNodePopup.showPopup(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                service
        );
    }

    private void handleRemoveNode() {
        String nodeId = JOptionPane.showInputDialog(
                this,
                "Enter node ID to remove:",
                "Remove Node",
                JOptionPane.QUESTION_MESSAGE
        );

        if (nodeId != null && !nodeId.isEmpty()) {
            GraphResult<Boolean> result = service.removeNode(nodeId);
            if (!result.isSuccess()) {
                JOptionPane.showMessageDialog(
                        this,
                        result.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void handleAddEdge() {
        AddEdgePopup.showDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                service
        );
    }

    private void handleRemoveEdge() {
        RemoveEdgePopup.showDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                service
        );
    }

    private void handleClearAllEdges() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all edges?",
                "Clear Edges",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            GraphResult<Boolean> clearResult = service.clearEdges();
            if (!clearResult.isSuccess()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to clear edges: " + clearResult.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void handleClearAll() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all nodes and edges?",
                "Clear Graph",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            GraphResult<Boolean> clearResult = service.clearGraph();
            if (!clearResult.isSuccess()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to clear graph: " + clearResult.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void handlePlay() {
        ToolbarControlManager controlManager = new ToolbarControlManager(this, new MainFrameService());
        controlManager.handlePlayAction();
    }

    private void handlePause() {
        System.out.println("Pause action triggered");
    }

    private void handleStop() {
        System.out.println("Stop action triggered");
    }

    private void handleNextStep() {
        System.out.println("Next Step action triggered");
    }

    private void handlePrevStep() {
        System.out.println("Prev Step action triggered");
    }

    private void handleSkipToEnd() {
        System.out.println("Skip to End action triggered");
    }
}
