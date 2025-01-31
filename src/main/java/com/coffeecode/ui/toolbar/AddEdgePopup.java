package com.coffeecode.ui.toolbar;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.coffeecode.core.GraphResult;
import com.coffeecode.model.weight.EdgeType;
import com.coffeecode.ui.service.MainFrameService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddEdgePopup extends JDialog {

    private static final double DEFAULT_THRESHOLD = 100.0;
    private static final int SLIDER_MIN = 0;
    private static final int SLIDER_MAX = 1000;

    private final MainFrameService service;
    private JComboBox<String> sourceNodeBox;
    private JComboBox<String> targetNodeBox;
    private JComboBox<EdgeType> edgeTypeBox;
    private JSlider thresholdSlider;
    private JLabel weightLabel;

    public AddEdgePopup(JFrame parent, MainFrameService service) {
        super(parent, "Add Edge", true);
        this.service = service;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        add(createManualPanel(), BorderLayout.NORTH);
        add(createAutoConnectPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createManualPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        setupNodeSelectors(panel, gbc);
        setupEdgeTypeSelector(panel, gbc);
        setupWeightDisplay(panel, gbc);

        return panel;
    }

    private void setupNodeSelectors(JPanel panel, GridBagConstraints gbc) {
        sourceNodeBox = new JComboBox<>();
        targetNodeBox = new JComboBox<>();
        populateNodeBoxes();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("From:"), gbc);
        gbc.gridx = 1;
        panel.add(sourceNodeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        panel.add(targetNodeBox, gbc);
    }

    private void setupEdgeTypeSelector(JPanel panel, GridBagConstraints gbc) {
        edgeTypeBox = new JComboBox<>(EdgeType.values());
        edgeTypeBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(((EdgeType) value).getDisplayName());
                return this;
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(edgeTypeBox, gbc);
    }

    private void setupWeightDisplay(JPanel panel, GridBagConstraints gbc) {
        weightLabel = new JLabel("Weight: 0.0");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(weightLabel, gbc);

        // Add listeners to update weight display
        ActionListener weightUpdater = e -> updateWeightDisplay();
        sourceNodeBox.addActionListener(weightUpdater);
        targetNodeBox.addActionListener(weightUpdater);
        edgeTypeBox.addActionListener(weightUpdater);
    }

    private JPanel createAutoConnectPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add threshold slider
        thresholdSlider = new JSlider(SLIDER_MIN, SLIDER_MAX, (int) DEFAULT_THRESHOLD);
        thresholdSlider.setPaintTicks(true);
        thresholdSlider.setPaintLabels(true);
        thresholdSlider.setMajorTickSpacing(200);
        thresholdSlider.setMinorTickSpacing(50);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Auto-connect threshold (km):"), gbc);

        gbc.gridy = 1;
        panel.add(thresholdSlider, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Edge");
        JButton autoConnectButton = new JButton("Auto Connect");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> addSingleEdge());
        autoConnectButton.addActionListener(e -> autoConnectNodes());
        closeButton.addActionListener(e -> dispose());

        panel.add(addButton);
        panel.add(autoConnectButton);
        panel.add(closeButton);

        return panel;
    }

    private void populateNodeBoxes() {
        Set<String> nodeIds = service.getAllNodeIds();
        sourceNodeBox.removeAllItems();
        targetNodeBox.removeAllItems();

        nodeIds.forEach(id -> {
            sourceNodeBox.addItem(id);
            targetNodeBox.addItem(id);
        });
    }

    private void addSingleEdge() {
        String sourceId = (String) sourceNodeBox.getSelectedItem();
        String targetId = (String) targetNodeBox.getSelectedItem();
        EdgeType type = (EdgeType) edgeTypeBox.getSelectedItem();

        if (sourceId.equals(targetId)) {
            showError("Cannot connect node to itself");
            return;
        }

        GraphResult<Boolean> result = service.addEdge(sourceId, targetId, type);
        if (result.isSuccess()) {
            dispose();
        } else {
            showError(result.getMessage());
        }
    }

    private void autoConnectNodes() {
        double threshold = thresholdSlider.getValue();
        EdgeType type = (EdgeType) edgeTypeBox.getSelectedItem();

        int option = showConfirmDialog(
                "This will connect all nodes within " + threshold
                + "km using " + type.getDisplayName() + ". Continue?");

        if (option == JOptionPane.YES_OPTION) {
            GraphResult<Integer> result = service.autoConnectNodes(threshold, type);
            handleAutoConnectResult(result);
        }
    }

    private void updateWeightDisplay() {
        try {
            String sourceId = (String) sourceNodeBox.getSelectedItem();
            String targetId = (String) targetNodeBox.getSelectedItem();
            EdgeType type = (EdgeType) edgeTypeBox.getSelectedItem();

            if (sourceId != null && targetId != null && type != null) {
                double weight = service.calculateEdgeWeight(sourceId, targetId, type);
                weightLabel.setText(String.format("Weight: %.2f", weight));
            }
        } catch (Exception e) {
            weightLabel.setText("Weight: N/A");
        }
    }

    private void handleAutoConnectResult(GraphResult<Integer> result) {
        if (result.isSuccess()) {
            showMessage("Added " + result.getData() + " connections", "Success");
            dispose();
        } else {
            showError(result.getMessage());
        }
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message,
                "Confirm Auto-Connect", JOptionPane.YES_NO_OPTION);
    }

    public static void showDialog(JFrame parent, MainFrameService service) {
        new AddEdgePopup(parent, service).setVisible(true);
    }
}
