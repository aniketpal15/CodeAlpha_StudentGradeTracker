package com.app.gradetracker;

import com.app.gradetracker.cli.ConsoleUI;
import com.app.gradetracker.gui.GradeTrackerGUI;
import com.app.gradetracker.model.GradeTracker;

import javax.swing.*;
import java.awt.*;

/**
 * Main application launcher for Student Grade Tracker.
 * Supports both Graphical User Interface (GUI) and Command-Line Interface (CLI).
 */
public class Main {

    public static void main(String[] args) {
        GradeTracker tracker = new GradeTracker();

        // Check for CLI argument flags
        boolean forceCli = false;
        boolean forceGui = false;

        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg) || "--console".equalsIgnoreCase(arg)) {
                forceCli = true;
            } else if ("--gui".equalsIgnoreCase(arg) || "-g".equalsIgnoreCase(arg)) {
                forceGui = true;
            } else if ("--help".equalsIgnoreCase(arg) || "-h".equalsIgnoreCase(arg)) {
                printHelp();
                return;
            }
        }

        if (forceCli) {
            launchCLI(tracker);
            return;
        }

        if (forceGui) {
            launchGUI(tracker);
            return;
        }

        // Default behavior: Check if GUI is supported in current environment
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[INFO] Headless environment detected. Launching Console Interface...");
            launchCLI(tracker);
        } else {
            System.out.println("==========================================================================");
            System.out.println("            JAVA PROGRAMMING INTERNSHIP - TASK 1: STUDENT GRADE TRACKER          ");
            System.out.println("==========================================================================");
            System.out.println("[INFO] Launching Swing Graphical User Interface (GUI)...");
            System.out.println("[TIP]  To launch Console (CLI) mode instead, run: java -jar app.jar --cli");
            System.out.println("==========================================================================");
            launchGUI(tracker);
        }
    }

    private static void launchGUI(GradeTracker tracker) {
        // Pre-populate sample data for immediate visual review
        tracker.loadSampleData();

        SwingUtilities.invokeLater(() -> {
            try {
                // Try System Look and Feel, fallback to Nimbus
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                try {
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Exception ignored2) {}
            }

            GradeTrackerGUI gui = new GradeTrackerGUI(tracker);
            gui.setVisible(true);
        });
    }

    private static void launchCLI(GradeTracker tracker) {
        ConsoleUI console = new ConsoleUI(tracker);
        console.start();
    }

    private static void printHelp() {
        System.out.println("Student Grade Tracker");
        System.out.println("Usage:");
        System.out.println("  java -cp bin com.app.gradetracker.Main [OPTIONS]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --gui, -g       Launch the Graphical User Interface (Swing)");
        System.out.println("  --cli, -c       Launch the interactive Console Command-Line Interface");
        System.out.println("  --help, -h      Display this help message");
    }
}
