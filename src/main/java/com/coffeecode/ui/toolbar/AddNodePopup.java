package com.coffeecode.ui.toolbar;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.coffeecode.ui.service.MainFrameService;
import com.coffeecode.ui.validation.LocationValidator;
import com.coffeecode.util.NominatimService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AddNodePopup extends JDialog {

    private final MainFrameService service;
    private final NominatimService nominatimService = new NominatimService();
    private JTextField nameField;
    private JTextField longitudeField;
    private JTextField latitudeField;
    private JButton searchButton;
    private JButton getFromMapButton;
    private JButton addNodeButton;
    private JButton closeButton;

    public AddNodePopup(JFrame parent, MainFrameService service) {
        super(parent, "Add Node", true);
        this.service = service;
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name input with search button
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Name: "), gbc);

        JPanel namePanel = new JPanel(new BorderLayout(5, 0));
        nameField = new JTextField(15);
        searchButton = new JButton("🔍");
        namePanel.add(nameField, BorderLayout.CENTER);
        namePanel.add(searchButton, BorderLayout.EAST);

        gbc.gridx = 1;
        add(namePanel, gbc);

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
        setLocationRelativeTo(getParent());
    }

    private void setupButtonActions() {
        searchButton.addActionListener(e -> handleSearch());
        getFromMapButton.addActionListener(e -> handleGetFromMap());
        addNodeButton.addActionListener(e -> handleAddNode());
        closeButton.addActionListener(e -> dispose());
    }

    private void handleSearch() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter a location name");
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            double[] coords = nominatimService.findLongLat(name);

            if (coords.length == 2) {
                longitudeField.setText(String.format("%.6f", coords[0]));
                latitudeField.setText(String.format("%.6f", coords[1]));
                log.debug("Found coordinates for {}: {}, {}", name, coords[0], coords[1]);
            } else {
                showError("Location not found");
            }
        } catch (Exception ex) {
            log.error("Error searching location", ex);
            showError("Error searching location: " + ex.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

        private void handleGetFromMap() {
            showError("This feature is still in development phase");
        }

        private void handleAddNode() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Node name cannot be empty");
            return;
        }

        if (longitudeField.getText().isEmpty() || latitudeField.getText().isEmpty()) {
            showError("Coordinates cannot be empty");
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
                showSuccess("Node added successfully");
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

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showPopup(JFrame parent, MainFrameService service) {
        AddNodePopup popup = new AddNodePopup(parent, service);
        popup.setVisible(true);
    }
}
