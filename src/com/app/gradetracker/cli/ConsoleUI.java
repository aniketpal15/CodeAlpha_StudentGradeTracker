package com.app.gradetracker.cli;

import com.app.gradetracker.model.GradeTracker;
import com.app.gradetracker.model.Student;
import com.app.gradetracker.util.GradeUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Interactive Console Interface for the Student Grade Tracker.
 * Provides user-friendly menus, robust input validation, and detailed report displays.
 */
public class ConsoleUI {

    private final GradeTracker tracker;
    private final Scanner scanner;

    public ConsoleUI(GradeTracker tracker) {
        this.tracker = tracker;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the interactive command-line application loop.
     */
    public void start() {
        boolean running = true;
        printWelcomeBanner();

        while (running) {
            printMainMenu();
            int choice = readIntPrompt("Select an option (0-9): ", 0, 9);
            System.out.println();

            switch (choice) {
                case 1 -> handleAddStudent();
                case 2 -> handleAddGradeToStudent();
                case 3 -> handleRemoveStudent();
                case 4 -> handleViewAllStudents();
                case 5 -> handleViewSummaryReport();
                case 6 -> handleSearchStudent();
                case 7 -> handleExportReport();
                case 8 -> handleCsvMenu();
                case 9 -> handleLoadSampleData();
                case 0 -> {
                    System.out.println("Thank you for using Student Grade Tracker! Goodbye.");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }

            if (running) {
                pausePrompt();
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println("==========================================================================");
        System.out.println("             JAVA PROGRAMMING INTERNSHIP - TASK 1: STUDENT GRADE TRACKER        ");
        System.out.println("             Manage Student Grades | Compute Statistics | Generate Reports ");
        System.out.println("==========================================================================");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("+------------------------------------------------------------------------+");
        System.out.println("|                               MAIN MENU                                |");
        System.out.println("+------------------------------------------------------------------------+");
        System.out.println("| [1] Add New Student                                                    |");
        System.out.println("| [2] Add Grades to Existing Student                                     |");
        System.out.println("| [3] Remove Student                                                     |");
        System.out.println("| [4] View All Students List                                             |");
        System.out.println("| [5] Display Class Summary Report (Average, Highest, Lowest, etc.)      |");
        System.out.println("| [6] Search Student (by ID or Name)                                     |");
        System.out.println("| [7] Export Summary Report to Text File                                 |");
        System.out.println("| [8] CSV Data Management (Export / Import)                              |");
        System.out.println("| [9] Load Sample Demo Data                                              |");
        System.out.println("| [0] Exit Application                                                   |");
        System.out.println("+------------------------------------------------------------------------+");
    }

    private void handleAddStudent() {
        System.out.println("--- Add New Student ---");
        String id = readNonEmptyString("Enter Student ID (e.g. S101): ");

        if (tracker.getStudentById(id).isPresent()) {
            System.out.println("[ERROR] A student with ID '" + id + "' already exists.");
            return;
        }

        String name = readNonEmptyString("Enter Student Full Name: ");
        Student student = new Student(id, name);

        System.out.println("Enter initial grades separated by spaces or commas (or leave blank to skip):");
        String gradesLine = scanner.nextLine().trim();
        if (!gradesLine.isEmpty()) {
            String[] tokens = gradesLine.split("[,\\s]+");
            for (String token : tokens) {
                try {
                    double score = Double.parseDouble(token);
                    if (GradeUtils.isValidGrade(score)) {
                        student.addGrade(score);
                    } else {
                        System.out.printf("[WARNING] Grade %.2f is out of bounds (0-100) and was skipped.%n", score);
                    }
                } catch (NumberFormatException e) {
                    System.out.printf("[WARNING] '%s' is not a valid number and was skipped.%n", token);
                }
            }
        }

        tracker.addStudent(student);
        System.out.printf("[SUCCESS] Student '%s' (ID: %s) added with %d grade(s).%n",
                student.getName(), student.getId(), student.getGradeCount());
    }

    private void handleAddGradeToStudent() {
        System.out.println("--- Add Grades to Student ---");
        String id = readNonEmptyString("Enter Student ID: ");
        Optional<Student> optStudent = tracker.getStudentById(id);

        if (optStudent.isEmpty()) {
            System.out.println("[ERROR] No student found with ID: " + id);
            return;
        }

        Student student = optStudent.get();
        System.out.printf("Selected Student: %s (Current Grades: %s)%n", student.getName(), student.getFormattedGrades());

        System.out.println("Enter one or more grades separated by spaces or commas:");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("[INFO] No grades entered.");
            return;
        }

        String[] tokens = input.split("[,\\s]+");
        int added = 0;
        for (String token : tokens) {
            try {
                double score = Double.parseDouble(token);
                if (GradeUtils.isValidGrade(score)) {
                    student.addGrade(score);
                    added++;
                } else {
                    System.out.printf("[WARNING] Score %.2f ignored (must be 0-100).%n", score);
                }
            } catch (NumberFormatException e) {
                System.out.printf("[WARNING] Non-numeric value '%s' ignored.%n", token);
            }
        }

        System.out.printf("[SUCCESS] Added %d grade(s). Updated average for %s: %.2f%%%n",
                added, student.getName(), student.getAverageScore());
    }

    private void handleRemoveStudent() {
        System.out.println("--- Remove Student ---");
        String id = readNonEmptyString("Enter Student ID to remove: ");
        Optional<Student> optStudent = tracker.getStudentById(id);

        if (optStudent.isEmpty()) {
            System.out.println("[ERROR] No student found with ID: " + id);
            return;
        }

        Student s = optStudent.get();
        System.out.printf("Are you sure you want to remove '%s' (ID: %s)? (yes/no): ", s.getName(), s.getId());
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            tracker.removeStudent(id);
            System.out.println("[SUCCESS] Student removed successfully.");
        } else {
            System.out.println("[CANCELLED] Student was not removed.");
        }
    }

    private void handleViewAllStudents() {
        ArrayList<Student> list = tracker.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("[INFO] No students currently stored. Choose option [1] to add or [9] to load demo data.");
            return;
        }

        System.out.println("=========================================================================================");
        System.out.printf("| %-8s | %-20s | %-8s | %-8s | %-8s | %-6s | %-8s |\n",
                "ID", "NAME", "AVERAGE", "HIGHEST", "LOWEST", "GRADE", "STATUS");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Student s : list) {
            System.out.printf("| %-8s | %-20s | %7.2f%% | %7.2f  | %7.2f  | %-6s | %-8s |\n",
                    s.getId(),
                    truncate(s.getName(), 20),
                    s.getAverageScore(),
                    s.getHighestScore(),
                    s.getLowestScore(),
                    s.getGradeCount() > 0 ? s.getLetterGrade() : "N/A",
                    s.getGradeCount() > 0 ? (s.isPassing() ? "PASSED" : "FAILED") : "NO DATA");
        }
        System.out.println("=========================================================================================");
        System.out.printf("Total: %d student(s)%n", list.size());
    }

    private void handleViewSummaryReport() {
        System.out.println(tracker.generateSummaryReport());
    }

    private void handleSearchStudent() {
        System.out.println("--- Search Student ---");
        String query = readNonEmptyString("Enter search query (ID or Name): ");
        ArrayList<Student> matches = tracker.searchStudents(query);

        if (matches.isEmpty()) {
            System.out.println("[INFO] No students found matching: " + query);
            return;
        }

        System.out.printf("[FOUND] %d student(s) matching '%s':%n", matches.size(), query);
        for (Student s : matches) {
            System.out.println("--------------------------------------------------");
            System.out.println(" Student ID    : " + s.getId());
            System.out.println(" Full Name     : " + s.getName());
            System.out.println(" Grades        : " + s.getFormattedGrades());
            System.out.println(" Grade Count   : " + s.getGradeCount());
            System.out.printf(" Average Score : %.2f%%\n", s.getAverageScore());
            System.out.printf(" Highest Score : %.2f\n", s.getHighestScore());
            System.out.printf(" Lowest Score  : %.2f\n", s.getLowestScore());
            System.out.println(" Letter Grade  : " + (s.getGradeCount() > 0 ? s.getLetterGrade() : "N/A"));
            System.out.println(" Status        : " + (s.getGradeCount() > 0 ? (s.isPassing() ? "PASSED" : "FAILED") : "NO DATA"));
        }
        System.out.println("--------------------------------------------------");
    }

    private void handleExportReport() {
        System.out.println("--- Export Summary Report to Text File ---");
        System.out.print("Enter output file path (or press Enter for 'GradeReport.txt'): ");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) {
            path = "GradeReport.txt";
        }

        try {
            File file = new File(path);
            tracker.exportReportToFile(file);
            System.out.println("[SUCCESS] Report exported successfully to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to write report file: " + e.getMessage());
        }
    }

    private void handleCsvMenu() {
        System.out.println("--- CSV Data Management ---");
        System.out.println("[1] Export students to CSV file");
        System.out.println("[2] Import students from CSV file");
        int choice = readIntPrompt("Choose (1-2): ", 1, 2);

        if (choice == 1) {
            System.out.print("Enter target CSV file path (default: 'students_export.csv'): ");
            String path = scanner.nextLine().trim();
            if (path.isEmpty()) path = "students_export.csv";
            try {
                File f = new File(path);
                tracker.exportToCSV(f);
                System.out.println("[SUCCESS] Exported " + tracker.getStudentCount() + " students to " + f.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to export CSV: " + e.getMessage());
            }
        } else {
            System.out.print("Enter source CSV file path: ");
            String path = scanner.nextLine().trim();
            try {
                File f = new File(path);
                if (!f.exists()) {
                    System.out.println("[ERROR] File does not exist: " + path);
                    return;
                }
                int imported = tracker.importFromCSV(f);
                System.out.println("[SUCCESS] Imported " + imported + " student record(s) from " + f.getName());
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to import CSV: " + e.getMessage());
            }
        }
    }

    private void handleLoadSampleData() {
        tracker.loadSampleData();
        System.out.println("[SUCCESS] Loaded 6 realistic sample students with multiple grades!");
        System.out.printf("Class Average is now %.2f%%. Use option [4] or [5] to view details.%n",
                tracker.calculateClassAverage());
    }

    // Helper utilities for user input
    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[WARNING] Input cannot be blank. Please try again.");
        }
    }

    private int readIntPrompt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("[WARNING] Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[WARNING] Invalid input. Please enter a valid number.");
            }
        }
    }

    private void pausePrompt() {
        System.out.print("\nPress [Enter] to return to the menu...");
        scanner.nextLine();
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}
