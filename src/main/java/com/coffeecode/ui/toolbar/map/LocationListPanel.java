package com.coffeecode.ui.toolbar.map;

import javax.swing.*;
import java.awt.*;
import com.coffeecode.ui.service.MainFrameService;
import org.jxmapviewer.viewer.GeoPosition;

public class LocationListPanel extends JPanel {

    private final DefaultListModel<PendingLocation> locationListModel;
    private final JList<PendingLocation> locationList;

    public LocationListPanel(MainFrameService service, MapPanel mapPanel) {
        this.locationListModel = new DefaultListModel<>();
        this.locationList = new JList<>(locationListModel);
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(250, 0));
        setOpaque(false); // Make this panel transparent

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Selected Locations"));
        listPanel.add(new JScrollPane(locationList), BorderLayout.CENTER);

        JPanel listButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> removeSelected());
        listButtonPanel.add(removeButton);
        listPanel.add(listButtonPanel, BorderLayout.SOUTH);

        add(listPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add All");
        JButton clearButton = new JButton("Clear");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> handleAddAll());
        clearButton.addActionListener(e -> handleClear());
        closeButton.addActionListener(e -> {
            /* Close dialog logic */ });

        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void removeSelected() {
        int index = locationList.getSelectedIndex();
        if (index != -1) {
            locationListModel.remove(index);
        }
    }

    private void handleAddAll() {
        if (locationListModel.isEmpty()) {
            return;
        }

        // Logic to add all locations
    }

    private void handleClear() {
        locationListModel.clear();
    }
}
