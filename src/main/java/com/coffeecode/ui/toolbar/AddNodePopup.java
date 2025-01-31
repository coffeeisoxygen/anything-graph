package com.coffeecode.ui.toolbar;

import javax.swing.*;

import com.coffeecode.ui.service.MainFrameService;
import com.coffeecode.ui.validation.LocationValidator;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

@Slf4j
public class AddNodePopup extends JDialog {

    private final JTextField nameField;
    private final JTextField longitudeField;
    private final JTextField latitudeField;
    private final JButton getFromMapButton;
    private final JButton addNodeButton;
    private final JButton closeButton;
    private final MainFrameService service;

    public AddNodePopup(JFrame parent, MainFrameService service) {
        super(parent, "Add Node", true);
        this.service = service;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Name: "), gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        add(nameField, gbc);

        // Longitude input
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Longitude: "), gbc);

        longitudeField = new JTextField(15);
        gbc.gridx = 1;
        add(longitudeField, gbc);

        // Latitude input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Latitude: "), gbc);

        latitudeField = new JTextField(15);
        gbc.gridx = 1;
        add(latitudeField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        getFromMapButton = new JButton("Get From Map");
        addNodeButton = new JButton("Add Node");
        closeButton = new JButton("Close");

        buttonPanel.add(getFromMapButton);
        buttonPanel.add(addNodeButton);
        buttonPanel.add(closeButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        setupButtonActions();
        pack();
        setLocationRelativeTo(parent);
    }

    private void setupButtonActions() {
        getFromMapButton.addActionListener(e -> handleGetFromMap());
        addNodeButton.addActionListener(e -> handleAddNode());
        closeButton.addActionListener(e -> dispose());
    }

    private void handleAddNode() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Node name cannot be empty");
            return;
        }

        try {
            double longitude = Double.parseDouble(longitudeField.getText());
            double latitude = Double.parseDouble(latitudeField.getText());

            // Validate coordinates before adding
            LocationValidator.validateCoordinates(latitude, longitude);

            boolean added = service.addNode(name, latitude, longitude);
            if (added) {
                log.debug("Node added successfully: {}", name);
                dispose();
            } else {
                showError("Node with this name already exists");
            }
        } catch (NumberFormatException e) {
            showError("Invalid coordinate format. Please enter valid numbers");
            log.debug("Invalid coordinate format entered");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            log.debug("Invalid coordinates: {}", e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void handleGetFromMap() {
        // Will be implemented when map feature is added
        log.debug("Get from map requested - Not implemented yet");
        JOptionPane.showMessageDialog(this,
                "Map feature coming soon!",
                "Not Implemented",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showPopup(JFrame parent, MainFrameService service) {
        AddNodePopup popup = new AddNodePopup(parent, service);
        popup.setVisible(true);
    }
}
