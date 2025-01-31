package com.coffeecode.ui.toolbar;

import com.coffeecode.core.GraphResult;
import com.coffeecode.model.LocationNode;
import com.coffeecode.ui.service.MainFrameService;
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

    public AddBatchNodePopup(JFrame parent, MainFrameService service) {
        super(parent, "Add Multiple Nodes", true);
        this.service = service;

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

        // Table panel
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addRowButton = new JButton("Add Row");
        JButton removeRowButton = new JButton("Remove Selected");
        JButton importButton = new JButton("Import CSV");
        JButton addAllButton = new JButton("Add All");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(addRowButton);
        buttonPanel.add(removeRowButton);
        buttonPanel.add(importButton);
        buttonPanel.add(addAllButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        addRowButton.addActionListener(e -> addEmptyRow());
        removeRowButton.addActionListener(e -> removeSelectedRows());
        importButton.addActionListener(e -> importCSV());
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

    public static void showPopup(JFrame parent, MainFrameService service) {
        AddBatchNodePopup popup = new AddBatchNodePopup(parent, service);
        popup.setVisible(true);
    }
}
