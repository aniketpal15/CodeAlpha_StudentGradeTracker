package com.app.gradetracker.gui;

import com.app.gradetracker.model.GradeTracker;
import com.app.gradetracker.model.Student;
import com.app.gradetracker.util.GradeUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Modern Swing Graphical User Interface for the Student Grade Tracker.
 * Features live KPI statistical cards, searchable JTable, CRUD management,
 * grade visualization, and report generation/export.
 */
public class GradeTrackerGUI extends JFrame {

    private final GradeTracker tracker;

    // Table components
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // KPI Stat Card Labels
    private JLabel totalStudentsLabel;
    private JLabel classAverageLabel;
    private JLabel highestScoreLabel;
    private JLabel lowestScoreLabel;
    private JLabel passRateLabel;

    // Form Fields
    private JTextField idField;
    private JTextField nameField;
    private JTextField gradesField;
    private JTextField searchField;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(24, 90, 188);
    private static final Color SUCCESS_COLOR = new Color(34, 139, 34);
    private static final Color DANGER_COLOR = new Color(200, 40, 40);
    private static final Color CARD_BG = new Color(245, 247, 250);
    private static final Color HEADER_BG = new Color(20, 32, 48);

    public GradeTrackerGUI(GradeTracker tracker) {
        this.tracker = tracker;
        initUI();
        refreshTableAndStats();
    }

    private void initUI() {
        setTitle("Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 720);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(null);

        // Main layout container
        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        rootPanel.setBackground(Color.WHITE);

        // 1. Top Section: Header & Stat Cards
        JPanel topContainer = new JPanel(new BorderLayout(0, 10));
        topContainer.setOpaque(false);
        topContainer.add(createHeaderPanel(), BorderLayout.NORTH);
        topContainer.add(createStatsPanel(), BorderLayout.CENTER);
        rootPanel.add(topContainer, BorderLayout.NORTH);

        // 2. Center Section: Search Bar & Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setOpaque(false);
        centerPanel.add(createSearchPanel(), BorderLayout.NORTH);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);
        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Section: Form Inputs & Action Buttons
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 8));
        bottomContainer.setOpaque(false);
        bottomContainer.add(createFormPanel(), BorderLayout.NORTH);
        bottomContainer.add(createToolbarPanel(), BorderLayout.SOUTH);
        rootPanel.add(bottomContainer, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel titleLabel = new JLabel("STUDENT GRADE TRACKER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Task 1: Grade Management & Performance Analytics System");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(200, 215, 235));

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subLabel, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        statsPanel.setOpaque(false);

        totalStudentsLabel = new JLabel("0", SwingConstants.CENTER);
        classAverageLabel = new JLabel("0.0%", SwingConstants.CENTER);
        highestScoreLabel = new JLabel("0.0", SwingConstants.CENTER);
        lowestScoreLabel = new JLabel("0.0", SwingConstants.CENTER);
        passRateLabel = new JLabel("0.0%", SwingConstants.CENTER);

        statsPanel.add(buildStatCard("Total Students", totalStudentsLabel, new Color(41, 128, 185)));
        statsPanel.add(buildStatCard("Class Average", classAverageLabel, new Color(39, 174, 96)));
        statsPanel.add(buildStatCard("Highest Score", highestScoreLabel, new Color(142, 68, 173)));
        statsPanel.add(buildStatCard("Lowest Score", lowestScoreLabel, new Color(230, 126, 34)));
        statsPanel.add(buildStatCard("Pass Rate", passRateLabel, new Color(22, 160, 133)));

        return statsPanel;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 224, 230), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel titleLbl = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLbl.setForeground(new Color(110, 120, 135));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(" Search Records: ");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setToolTipText("Filter by student ID or Name...");

        JButton clearSearchBtn = new JButton("Clear");
        clearSearchBtn.addActionListener(e -> searchField.setText(""));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        panel.add(lbl, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(clearSearchBtn, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {
                "Student ID", "Student Name", "Recorded Grades", "Count",
                "Average", "Highest", "Lowest", "Grade", "Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only directly in table cells
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.setRowHeight(26);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setShowVerticalLines(true);
        studentTable.setGridColor(new Color(230, 235, 240));

        rowSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(rowSorter);

        JTableHeader tableHeader = studentTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.setBackground(new Color(240, 243, 247));

        // Column Renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        studentTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Count
        studentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Average
        studentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Highest
        studentTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Lowest
        studentTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Letter Grade

        // Status Renderer with Badges
        studentTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = value == null ? "" : value.toString();
                if ("PASSED".equals(val)) {
                    setForeground(isSelected ? Color.WHITE : SUCCESS_COLOR);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("FAILED".equals(val)) {
                    setForeground(isSelected ? Color.WHITE : DANGER_COLOR);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(isSelected ? Color.WHITE : Color.GRAY);
                }
                return c;
            }
        });

        // Set column widths
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(65);
        studentTable.getColumnModel().getColumn(6).setPreferredWidth(65);
        studentTable.getColumnModel().getColumn(7).setPreferredWidth(60);
        studentTable.getColumnModel().getColumn(8).setPreferredWidth(80);

        // Selection listener to populate input fields
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() != -1) {
                int modelRow = studentTable.convertRowIndexToModel(studentTable.getSelectedRow());
                String id = (String) tableModel.getValueAt(modelRow, 0);
                String name = (String) tableModel.getValueAt(modelRow, 1);
                String grades = (String) tableModel.getValueAt(modelRow, 2);

                idField.setText(id);
                nameField.setText(name);
                gradesField.setText(grades.replace("[", "").replace("]", ""));
            }
        });

        return new JScrollPane(studentTable);
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Labels
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.2;
        form.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        form.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.45;
        form.add(new JLabel("Scores / Grades (comma-separated, 0-100):"), gbc);

        // Row 2: Inputs
        gbc.gridy = 1;
        gbc.gridx = 0;
        idField = new JTextField();
        form.add(idField, gbc);

        gbc.gridx = 1;
        nameField = new JTextField();
        form.add(nameField, gbc);

        gbc.gridx = 2;
        gradesField = new JTextField();
        gradesField.setToolTipText("Example: 85, 92, 78.5");
        form.add(gradesField, gbc);

        // Row 3: Action Buttons
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnPanel.setOpaque(false);

        JButton addBtn = new JButton("Add Student");
        addBtn.setBackground(PRIMARY_COLOR);
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.addActionListener(e -> handleAddStudent());

        JButton updateBtn = new JButton("Update Selected");
        updateBtn.addActionListener(e -> handleUpdateStudent());

        JButton deleteBtn = new JButton("Delete Student");
        deleteBtn.setForeground(DANGER_COLOR);
        deleteBtn.addActionListener(e -> handleDeleteStudent());

        JButton clearBtn = new JButton("Clear Inputs");
        clearBtn.addActionListener(e -> clearInputs());

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        form.add(btnPanel, gbc);
        return form;
    }

    private JPanel createToolbarPanel() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setOpaque(false);

        JButton summaryBtn = new JButton("View Summary Report");
        summaryBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        summaryBtn.addActionListener(e -> showSummaryReportDialog());

        JButton demoBtn = new JButton("Load Demo Data");
        demoBtn.addActionListener(e -> {
            tracker.loadSampleData();
            refreshTableAndStats();
            JOptionPane.showMessageDialog(this, "Sample student records successfully loaded!",
                    "Demo Data", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton importCsvBtn = new JButton("Import CSV");
        importCsvBtn.addActionListener(e -> handleImportCSV());

        JButton exportCsvBtn = new JButton("Export CSV");
        exportCsvBtn.addActionListener(e -> handleExportCSV());

        JButton clearAllBtn = new JButton("Clear All");
        clearAllBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to clear all student records?",
                    "Confirm Reset", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tracker.clear();
                clearInputs();
                refreshTableAndStats();
            }
        });

        toolbar.add(summaryBtn);
        toolbar.add(demoBtn);
        toolbar.add(importCsvBtn);
        toolbar.add(exportCsvBtn);
        toolbar.add(clearAllBtn);

        return toolbar;
    }

    private void handleAddStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and Name cannot be empty.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tracker.getStudentById(id).isPresent()) {
            JOptionPane.showMessageDialog(this, "A student with ID '" + id + "' already exists.",
                    "Duplicate ID", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student student = new Student(id, name);
        List<Double> parsedGrades = parseGradesField();
        for (Double g : parsedGrades) {
            student.addGrade(g);
        }

        tracker.addStudent(student);
        clearInputs();
        refreshTableAndStats();
    }

    private void handleUpdateStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or provide a student ID and Name.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Student> optStudent = tracker.getStudentById(id);
        if (optStudent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No student found with ID: " + id,
                    "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student student = optStudent.get();
        student.setName(name);
        student.clearGrades();
        List<Double> parsedGrades = parseGradesField();
        for (Double g : parsedGrades) {
            student.addGrade(g);
        }

        refreshTableAndStats();
        JOptionPane.showMessageDialog(this, "Student '" + student.getName() + "' updated successfully.",
                "Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDeleteStudent() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter or select a Student ID to delete.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete student ID: " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean removed = tracker.removeStudent(id);
            if (removed) {
                clearInputs();
                refreshTableAndStats();
            } else {
                JOptionPane.showMessageDialog(this, "Student ID not found: " + id,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<Double> parseGradesField() {
        List<Double> result = new ArrayList<>();
        String raw = gradesField.getText().trim();
        if (raw.isEmpty()) return result;

        String[] tokens = raw.split("[,\\s]+");
        for (String t : tokens) {
            try {
                double val = Double.parseDouble(t);
                if (GradeUtils.isValidGrade(val)) {
                    result.add(val);
                } else {
                    JOptionPane.showMessageDialog(this, "Grade " + val + " is outside the range 0-100 and was skipped.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }

    private void clearInputs() {
        idField.setText("");
        nameField.setText("");
        gradesField.setText("");
        studentTable.clearSelection();
    }

    public void refreshTableAndStats() {
        tableModel.setRowCount(0);
        for (Student s : tracker.getAllStudents()) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getGrades().toString(),
                    s.getGradeCount(),
                    s.getGradeCount() > 0 ? String.format("%.2f", s.getAverageScore()) : "-",
                    s.getGradeCount() > 0 ? String.format("%.2f", s.getHighestScore()) : "-",
                    s.getGradeCount() > 0 ? String.format("%.2f", s.getLowestScore()) : "-",
                    s.getGradeCount() > 0 ? s.getLetterGrade() : "-",
                    s.getGradeCount() > 0 ? (s.isPassing() ? "PASSED" : "FAILED") : "NO DATA"
            });
        }

        // Update Stat Cards
        totalStudentsLabel.setText(String.valueOf(tracker.getStudentCount()));
        classAverageLabel.setText(String.format("%.2f%%", tracker.calculateClassAverage()));
        highestScoreLabel.setText(String.format("%.2f", tracker.getClassHighestScore()));
        lowestScoreLabel.setText(String.format("%.2f", tracker.getClassLowestScore()));
        passRateLabel.setText(String.format("%.2f%%", tracker.getPassRatePercentage()));
    }

    private void showSummaryReportDialog() {
        JDialog dialog = new JDialog(this, "Academic Summary Report", true);
        dialog.setSize(750, 580);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea(tracker.generateSummaryReport());
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Report to File");
        saveBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("ClassGradeReport.txt"));
            if (chooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    tracker.exportReportToFile(chooser.getSelectedFile());
                    JOptionPane.showMessageDialog(dialog, "Report successfully saved to:\n"
                            + chooser.getSelectedFile().getAbsolutePath(), "Export Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Failed to save file: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());

        bottom.add(saveBtn);
        bottom.add(closeBtn);

        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleExportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("students.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                tracker.exportToCSV(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Exported successfully to:\n"
                        + chooser.getSelectedFile().getAbsolutePath(), "Export CSV", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleImportCSV() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int count = tracker.importFromCSV(chooser.getSelectedFile());
                refreshTableAndStats();
                JOptionPane.showMessageDialog(this, "Successfully imported " + count + " student record(s).",
                        "Import CSV", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to import file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
