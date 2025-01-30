package com.coffeecode;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.coffeecode.ui.MainFrame;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    private static void setupGraphStream() {
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    }

    private static void setupLookAndFeel() throws Exception {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            log.error("Uncaught exception in thread {}", thread.getName(), throwable);
            System.exit(1);
        });

        log.info("Starting Graph Algorithm Visualizer...");

        try {
            setupGraphStream();
            setupLookAndFeel();

            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);

                    // Add shutdown hook
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        log.info("Shutting down application...");
                        mainFrame.dispose();
                    }));

                    log.info("Application started successfully");

                } catch (Exception e) {
                    log.error("Failed to initialize application", e);
                    System.exit(1);
                }
            });

        } catch (Exception e) {
            log.error("Failed to start application", e);
            System.exit(1);
        }
    }
}
