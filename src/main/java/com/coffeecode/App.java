package com.coffeecode;

import com.coffeecode.ui.MainFrame;

import lombok.extern.slf4j.Slf4j;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@Slf4j
public class App {

    public static void main(String[] args) {
        log.info("Starting Graph Algorithm Visualizer...");

        try {
            // Set system properties
            System.setProperty("org.graphstream.ui", "swing");

            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            log.info("UI Look and Feel initialized");

            // Launch UI
            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                    log.info("Main window displayed");
                } catch (Exception e) {
                    log.error("Failed to initialize main window", e);
                }
            });

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            log.error("Application startup failed", e);
        }
    }
}
