package com.coffeecode;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.coffeecode.ui.MainFrame;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            log.error("Uncaught exception in thread {}", thread.getName(), throwable);
            System.exit(1);
        });

        log.info("Starting Graph Algorithm Visualizer...");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);

                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        log.info("Shutting down application...");
                        mainFrame.dispose();
                        System.exit(0);
                    }));

                    log.info("Application started successfully");

                } catch (Exception e) {
                    log.error("Failed to initialize application", e);
                    System.exit(1);
                }
            });

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            log.error("Failed to start application", e);
            System.exit(1);
        }
    }
}
