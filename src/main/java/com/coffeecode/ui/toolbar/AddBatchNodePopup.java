package com.coffeecode.ui.toolbar;

import com.coffeecode.core.GraphResult;
import com.coffeecode.model.LocationNode;
import com.coffeecode.ui.service.MainFrameService;
import com.coffeecode.util.NominatimService;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AddBatchNodePopup extends JDialog {

    private final MainFrameService service;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final NominatimService nominatimService;
    private JLabel statusLabel;

    public AddBatchNodePopup(JFrame parent, MainFrameService service) {
        super(parent, "Add Multiple Nodes", true);
        this.service = service;
        this.nominatimService = new NominatimService();

        // Setup table model
        String[] columns = {"ID", "Latitude", "Longitude", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 3; // Status column not editable
            }
        };

        table = new JTable(tableModel);
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setSize(600, 400);
        setLocationRelativeTo(getParent());

        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel(
                "<html>Enter location names, then click 'Get All Locations' "
                + "to fetch coordinates automatically</html>"
        );
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        infoPanel.add(statusLabel, BorderLayout.EAST);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addRowButton = new JButton("Add Row");
        JButton clearAllButton = new JButton("Clear All");
        JButton getLocationsButton = new JButton("Get All Locations");
        JButton addAllButton = new JButton("Add All");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(addRowButton);
        buttonPanel.add(clearAllButton);
        buttonPanel.add(getLocationsButton);
        buttonPanel.add(addAllButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        addRowButton.addActionListener(e -> addEmptyRow());
        clearAllButton.addActionListener(e -> clearAll());
        getLocationsButton.addActionListener(e -> getAllLocations());
        addAllButton.addActionListener(e -> addAllNodes());
        closeButton.addActionListener(e -> dispose());

        // Add initial empty row
        addEmptyRow();
    }

    private void addEmptyRow() {
        tableModel.addRow(new Object[]{"", "", "", "Not Added"});
    }

    private void removeSelectedRows() {
        int[] rows = table.getSelectedRows();
        for (int i = rows.length - 1; i >= 0; i--) {
            tableModel.removeRow(rows[i]);
        }
    }

    private void addAllNodes() {
        List<LocationNode> nodes = new ArrayList<>();

        // Collect and validate all nodes
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                String id = (String) tableModel.getValueAt(i, 0);
                double lat = Double.parseDouble((String) tableModel.getValueAt(i, 1));
                double lon = Double.parseDouble((String) tableModel.getValueAt(i, 2));

                nodes.add(new LocationNode(id, lat, lon));
                tableModel.setValueAt("Valid", i, 3);
            } catch (Exception e) {
                tableModel.setValueAt("Invalid", i, 3);
                log.error("Invalid node at row {}: {}", i, e.getMessage());
            }
        }

        if (nodes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No valid nodes to add",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add nodes in batch
        GraphResult<Integer> result = service.addNodes(nodes);
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    String.format("Added %d nodes successfully", result.getData()),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to add nodes: " + result.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importCSV() {
        // TODO: Implement CSV import
        JOptionPane.showMessageDialog(this,
                "CSV import not implemented yet",
                "Not Available",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void getAllLocations() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Fetching locations...");

        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String locationName = (String) tableModel.getValueAt(i, 0);
                    if (locationName != null && !locationName.trim().isEmpty()) {
                        try {
                            double[] coords = nominatimService.findLongLat(locationName);
                            if (coords.length == 2) {
                                tableModel.setValueAt(String.format("%.6f", coords[1]), i, 1);
                                tableModel.setValueAt(String.format("%.6f", coords[0]), i, 2);
                                tableModel.setValueAt("Found", i, 3);
                            } else {
                                tableModel.setValueAt("Not Found", i, 3);
                            }
                            publish(i);
                        } catch (Exception e) {
                            log.error("Error fetching location: {}", locationName, e);
                            tableModel.setValueAt("Error", i, 3);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1) + 1;
                statusLabel.setText(String.format("Processing... %d/%d",
                        progress, tableModel.getRowCount()));
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                statusLabel.setText("Location search completed");
            }
        }.execute();
    }

    private void clearAll() {
        tableModel.setRowCount(0);
        addEmptyRow();
        statusLabel.setText("Cleared all entries");
    }

    public static void showPopup(JFrame parent, MainFrameService service) {
        AddBatchNodePopup popup = new AddBatchNodePopup(parent, service);
        popup.setVisible(true);
    }
}
