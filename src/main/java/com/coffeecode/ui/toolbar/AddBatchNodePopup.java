package com.coffeecode.ui.toolbar;

import com.coffeecode.core.GraphResult;
import com.coffeecode.model.LocationNode;
import com.coffeecode.ui.service.MainFrameService;
import com.coffeecode.util.NominatimService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AddBatchNodePopup extends JDialog {

    private static final int RETRY_ATTEMPTS = 3;
    private static final long RATE_LIMIT_DELAY = 1000; // 1 second between requests

    private final transient MainFrameService service;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final transient NominatimService nominatimService;
    private JLabel statusLabel;

    public AddBatchNodePopup(JFrame parent, MainFrameService service) {
        super(parent, "Add Multiple Nodes", true);
        this.service = service;
        this.nominatimService = new NominatimService();

        tableModel = createTableModel();
        table = new JTable(tableModel);

        setupUI();
        addEmptyRow();
    }

    private DefaultTableModel createTableModel() {
        String[] columns = {"ID", "Latitude", "Longitude", "Status"};
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 3; // Status column not editable
            }
        };
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setSize(800, 500);
        setLocationRelativeTo(getParent());

        add(createInfoPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = table.getSelectedRow();
                    if (row == tableModel.getRowCount() - 1) {
                        addEmptyRow();
                    }
                    table.changeSelection(row + 1, 0, false, false);
                }
            }
        });

        // Add popup menu
        JPopupMenu popupMenu = createPopupMenu();
        table.setComponentPopupMenu(popupMenu);
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem retryItem = new JMenuItem("Retry Selected");
        retryItem.addActionListener(e -> retrySelectedLocations());
        menu.add(retryItem);
        return menu;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel infoLabel = new JLabel("<html><div style='width: 400px;'>"
                + "1. Enter location names in the ID column<br>"
                + "2. Click 'Get All Locations' to fetch coordinates automatically<br>"
                + "3. Review the results and edit if needed<br>"
                + "4. Click 'Add All' to add nodes to the graph"
                + "</div></html>");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabel = new JLabel("Ready");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(infoLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(statusLabel);
        return infoPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createButton("Add Row", e -> addEmptyRow()));
        buttonPanel.add(createButton("Clear All", e -> clearAll()));
        buttonPanel.add(createButton("Get All Locations", e -> getAllLocations()));
        buttonPanel.add(createButton("Add All", e -> addAllNodes()));
        buttonPanel.add(createButton("Close", e -> dispose()));
        return buttonPanel;
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    private void addEmptyRow() {
        tableModel.addRow(new Object[]{"", "", "", "Not Added"});
    }

    private void clearAll() {
        tableModel.setRowCount(0);
        addEmptyRow();
        statusLabel.setText("Cleared all entries");
    }

    private void getAllLocations() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Fetching locations...");

        new SwingWorker<Void, LocationSearchResult>() {
            @Override
            protected Void doInBackground() {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String locationName = (String) tableModel.getValueAt(i, 0);
                    if (locationName != null && !locationName.trim().isEmpty()) {
                        searchLocationWithRetry(i, locationName);
                        sleepBetweenRequests();
                    }
                }
                return null;
            }

            private void searchLocationWithRetry(int row, String locationName) {
                Exception lastError = null;
                for (int attempt = 0; attempt < RETRY_ATTEMPTS; attempt++) {
                    try {
                        double[] coords = nominatimService.findLongLat(locationName);
                        publish(new LocationSearchResult(row, coords, null));
                        return;
                    } catch (Exception e) {
                        lastError = e;
                        log.warn("Attempt {} failed for {}: {}", attempt + 1, locationName, e.getMessage());
                        sleepBetweenAttempts(attempt);
                    }
                }
                publish(new LocationSearchResult(row, null, lastError));
            }

            private void sleepBetweenRequests() {
                try {
                    Thread.sleep(RATE_LIMIT_DELAY);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            private void sleepBetweenAttempts(int attempt) {
                try {
                    Thread.sleep(RATE_LIMIT_DELAY * (attempt + 1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            protected void process(List<LocationSearchResult> results) {
                results.forEach(AddBatchNodePopup.this::updateTableRow);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                statusLabel.setText("Location search completed");
            }
        }.execute();
    }

    private void updateTableRow(LocationSearchResult result) {
        if (result.error != null) {
            tableModel.setValueAt("Error: " + result.error.getMessage(), result.row, 3);
            return;
        }
        if (result.coordinates != null && result.coordinates.length == 2) {
            tableModel.setValueAt(String.format("%.6f", result.coordinates[1]), result.row, 1);
            tableModel.setValueAt(String.format("%.6f", result.coordinates[0]), result.row, 2);
            tableModel.setValueAt("Found", result.row, 3);
        } else {
            tableModel.setValueAt("Not Found", result.row, 3);
        }
    }

    private void addAllNodes() {
        List<LocationNode> nodes = collectValidNodes();
        if (nodes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid nodes to add", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        GraphResult<Integer> result = service.addNodes(nodes);
        handleGraphResult(result);
    }

    private List<LocationNode> collectValidNodes() {
        List<LocationNode> nodes = new ArrayList<>();
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
        return nodes;
    }

    private void handleGraphResult(GraphResult<Integer> result) {
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, String.format("Added %d nodes successfully", result.getData()), "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add nodes: " + result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Value
    private static class LocationSearchResult {

        int row;
        double[] coordinates;
        Exception error;
    }

    public static void showPopup(JFrame parent, MainFrameService service) {
        new AddBatchNodePopup(parent, service).setVisible(true);
    }
}
