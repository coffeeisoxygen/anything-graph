package com.coffeecode.ui.toolbar;

import javax.swing.*;

import com.coffeecode.ui.service.MainFrameService;

import java.awt.*;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

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

    private void handleGetFromMap() {
        JOptionPane.showMessageDialog(this, "Fetching coordinates from map...");
    }

    private void handleAddNode() {
        try {
            String name = nameField.getText();
            double longitude = Double.parseDouble(longitudeField.getText());
            double latitude = Double.parseDouble(latitudeField.getText());

            if (name.isEmpty()) {
                showError("Name cannot be empty");
                return;
            }

            boolean added = service.addNode(name, latitude, longitude);
            if (added) {
                dispose();
            } else {
                showError("Node already exists");
            }
        } catch (NumberFormatException e) {
            showError("Invalid coordinates");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showPopup(JFrame parent, MainFrameService service) {
        AddNodePopup popup = new AddNodePopup(parent, service);
        popup.setVisible(true);
    }
}
