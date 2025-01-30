package com.coffeecode.ui.map.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.jxmapviewer.viewer.GeoPosition;

import lombok.Getter;

@Getter
public class NodePopupMenu extends JPopupMenu {

    // UI Components
    private final JTextField locationField;
    private final JLabel latitudeLabel;
    private final JLabel longitudeLabel;
    private final JButton addButton;
    private final JButton setStartButton;
    private final JButton setEndButton;

    // State
    private GeoPosition currentPosition;

    public NodePopupMenu() {
        // Initialize components
        locationField = new JTextField(20);
        latitudeLabel = new JLabel();
        longitudeLabel = new JLabel();
        addButton = createStyledButton("Add Node");
        setStartButton = createStyledButton("Set as Start");
        setEndButton = createStyledButton("Set as End");

        setupLayout();
    }

    private void setupLayout() {
        // Main container with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Form layout
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(buttonPanel);
        add(mainPanel);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        // Add fields with labels
        addFormRow(panel, "Location:", locationField, gbc, 0);
        addFormRow(panel, "Latitude:", latitudeLabel, gbc, 1);
        addFormRow(panel, "Longitude:", longitudeLabel, gbc, 2);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        addStyledButton(panel, addButton);
        addStyledButton(panel, setStartButton);
        addStyledButton(panel, setEndButton);

        return panel;
    }

    private void addFormRow(JPanel panel, String labelText, JComponent field,
            GridBagConstraints gbc, int row) {
        gbc.gridy = row;

        // Label
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        panel.add(new JLabel(labelText), gbc);

        // Field
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(150, 30));
        return button;
    }

    private void addStyledButton(JPanel panel, JButton button) {
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    // Public API
    public void showAt(Component invoker, Point location, GeoPosition position, String locationName) {
        currentPosition = position;
        locationField.setText(locationName);
        latitudeLabel.setText(String.format("%.6f", position.getLatitude()));
        longitudeLabel.setText(String.format("%.6f", position.getLongitude()));
        show(invoker, location.x, location.y);
    }

    public void addNodeActionListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addStartNodeActionListener(ActionListener listener) {
        setStartButton.addActionListener(listener);
    }

    public void addEndNodeActionListener(ActionListener listener) {
        setEndButton.addActionListener(listener);
    }
}
