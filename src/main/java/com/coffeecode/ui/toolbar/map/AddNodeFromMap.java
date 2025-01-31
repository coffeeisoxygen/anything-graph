package com.coffeecode.ui.toolbar.map;

import javax.swing.*;
import java.awt.*;
import com.coffeecode.ui.service.MainFrameService;
import com.coffeecode.util.NominatimService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddNodeFromMap extends JDialog {

    private final MainFrameService service;
    private final MapPanel mapPanel;
    private final LocationListPanel locationListPanel;

    public static void showDialog(JFrame parent, MainFrameService service) {
        AddNodeFromMap dialog = new AddNodeFromMap(parent, service);
        dialog.setVisible(true);
    }

    private AddNodeFromMap(JFrame parent, MainFrameService service) {
        super(parent, "Add Nodes from Map", true);
        this.service = service;
        this.mapPanel = new MapPanel(service);
        this.locationListPanel = new LocationListPanel(service, mapPanel);

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setSize(800, 600);
        setLocationRelativeTo(getParent());

        // Add map and location list panels
        add(mapPanel, BorderLayout.CENTER);
        add(locationListPanel, BorderLayout.EAST);
    }
}
