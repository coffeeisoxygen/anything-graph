package com.coffeecode.ui.toolbar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.coffeecode.core.GraphResult;
import com.coffeecode.model.LocationEdge;
import com.coffeecode.ui.service.MainFrameService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoveEdgePopup extends JDialog {

    private final MainFrameService service;
    private JComboBox<LocationEdge> edgeBox;

    public RemoveEdgePopup(JFrame parent, MainFrameService service) {
        super(parent, "Remove Edge", true);
        this.service = service;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        add(createSelectionPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Edge selection combobox
        edgeBox = new JComboBox<>();
        populateEdges();
        edgeBox.setRenderer(new EdgeListRenderer());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Select Edge:"), gbc);
        gbc.gridy = 1;
        panel.add(edgeBox, gbc);

        return panel;
    }

    private void populateEdges() {
        Set<LocationEdge> edges = service.getAllEdges();
        edges.forEach(edge -> edgeBox.addItem(edge));
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeButton = new JButton("Remove");
        JButton cancelButton = new JButton("Cancel");

        removeButton.addActionListener(e -> removeSelectedEdge());
        cancelButton.addActionListener(e -> dispose());

        panel.add(removeButton);
        panel.add(cancelButton);
        return panel;
    }

    private void removeSelectedEdge() {
        LocationEdge edge = (LocationEdge) edgeBox.getSelectedItem();
        if (edge == null) {
            return;
        }

        GraphResult<Boolean> result = service.removeEdge(edge);
        if (result.isSuccess()) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to remove edge: " + result.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class EdgeListRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof LocationEdge edge) {
                String text = String.format("%s → %s (%.2f km)",
                        edge.getSource().getId(),
                        edge.getDestination().getId(),
                        edge.getWeight());
                setText(text);
                setToolTipText(String.format("From: %s%nTo: %s%nWeight: %.2f km",
                        edge.getSource().getId(),
                        edge.getDestination().getId(),
                        edge.getWeight()));
            }
            return this;
        }
    }

    public static void showDialog(JFrame parent, MainFrameService service) {
        new RemoveEdgePopup(parent, service).setVisible(true);
    }

}
