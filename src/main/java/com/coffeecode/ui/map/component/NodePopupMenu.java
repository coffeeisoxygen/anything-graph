package com.coffeecode.ui.map.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import lombok.Getter;

@Getter
public class NodePopupMenu extends JPopupMenu {

    private final JTextField locationField;
    private final JButton addButton;
    private final JButton setStartButton;
    private final JButton setEndButton;

    public NodePopupMenu() {
        locationField = new JTextField(20);
        addButton = new JButton("Add Node");
        setStartButton = new JButton("Set as Start");
        setEndButton = new JButton("Set as End");

        setupLayout();
    }

    private void setupLayout() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Location panel
        JPanel locationPanel = new JPanel(new BorderLayout());
        locationPanel.add(new JLabel("Location: "), BorderLayout.WEST);
        locationPanel.add(locationField, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(setStartButton);
        buttonPanel.add(setEndButton);

        panel.add(locationPanel);
        panel.add(buttonPanel);
        add(panel);
    }
}
