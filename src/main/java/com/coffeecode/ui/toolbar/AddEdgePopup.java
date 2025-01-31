package com.coffeecode.ui.toolbar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.coffeecode.core.GraphResult;
import com.coffeecode.ui.service.MainFrameService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddEdgePopup extends JDialog {

    private final MainFrameService service;
    private JComboBox<String> sourceNodeBox;
    private JComboBox<String> targetNodeBox;

    public AddEdgePopup(JFrame parent, MainFrameService service) {
        super(parent, "Add Edge", true);
        this.service = service;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));

        // Node selection panel
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        sourceNodeBox = new JComboBox<>();
        targetNodeBox = new JComboBox<>();
        populateNodeBoxes();

        selectionPanel.add(new JLabel("From:"));
        selectionPanel.add(sourceNodeBox);
        selectionPanel.add(new JLabel("To:"));
        selectionPanel.add(targetNodeBox);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Edge");
        JButton autoConnectButton = new JButton("Auto-Connect All");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> addSingleEdge());
        autoConnectButton.addActionListener(e -> autoConnectNodes());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(autoConnectButton);
        buttonPanel.add(closeButton);

        add(selectionPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getParent());
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

        if (sourceId.equals(targetId)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot connect node to itself",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        GraphResult<Boolean> result = service.addEdge(sourceId, targetId);
        if (result.isSuccess()) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    result.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void autoConnectNodes() {
        int option = JOptionPane.showConfirmDialog(this,
                "This will connect all nodes based on distance. Continue?",
                "Confirm Auto-Connect",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            GraphResult<Integer> result = service.autoConnectNodes();
            if (result.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        String.format("Added %d connections", result.getData()),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        result.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
