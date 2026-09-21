package com.app.gradetracker.model;

import com.app.gradetracker.util.GradeUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Core manager class that holds an ArrayList of students, computes class-wide
 * statistics (average, highest, lowest, distribution), generates reports,
 * and handles CSV data import and export.
 */
public class GradeTracker {

    private final ArrayList<Student> students;

    public GradeTracker() {
        this.students = new ArrayList<>();
    }

    /**
     * Adds a student to the tracker.
     * Enforces unique student ID.
     */
    public boolean addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        if (getStudentById(student.getId()).isPresent()) {
            return false; // Student ID already exists
        }
        return students.add(student);
    }

    /**
     * Removes a student by student ID.
     */
    public boolean removeStudent(String studentId) {
        if (studentId == null) return false;
        return students.removeIf(s -> s.getId().equalsIgnoreCase(studentId.trim()));
    }

    /**
     * Finds a student by exact ID (case-insensitive).
     */
    public Optional<Student> getStudentById(String studentId) {
        if (studentId == null) return Optional.empty();
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(studentId.trim())) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    /**
     * Searches students by matching substring in ID or Name (case-insensitive).
     */
    public ArrayList<Student> searchStudents(String query) {
        ArrayList<Student> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        String q = query.trim().toLowerCase();
        for (Student s : students) {
            if (s.getId().toLowerCase().contains(q) || s.getName().toLowerCase().contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Returns a copy of all students in the tracker.
     */
    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    /**
     * Returns total number of registered students.
     */
    public int getStudentCount() {
        return students.size();
    }

    /**
     * Clears all students from the tracker.
     */
    public void clear() {
        students.clear();
    }

    /**
     * Calculates the class average based on each student's average score.
     */
    public double calculateClassAverage() {
        if (students.isEmpty()) {
            return 0.0;
        }
        int count = 0;
        double sum = 0.0;
        for (Student s : students) {
            if (s.getGradeCount() > 0) {
                sum += s.getAverageScore();
                count++;
            }
        }
        if (count == 0) return 0.0;
        return GradeUtils.round(sum / count, 2);
    }

    /**
     * Calculates the overall average of every single grade score recorded across the entire class.
     */
    public double calculateOverallGradeAverage() {
        int totalGrades = 0;
        double totalSum = 0.0;
        for (Student s : students) {
            for (Double g : s.getGrades()) {
                totalSum += g;
                totalGrades++;
            }
        }
        if (totalGrades == 0) return 0.0;
        return GradeUtils.round(totalSum / totalGrades, 2);
    }

    /**
     * Finds the single highest individual score recorded across all students.
     * Returns 0.0 if no grades are available.
     */
    public double getClassHighestScore() {
        double highest = -1.0;
        for (Student s : students) {
            for (Double g : s.getGrades()) {
                if (g > highest) {
                    highest = g;
                }
            }
        }
        return highest < 0 ? 0.0 : GradeUtils.round(highest, 2);
    }

    /**
     * Returns the student who achieved the class highest average score.
     */
    public Optional<Student> getTopPerformingStudent() {
        Student top = null;
        double maxAvg = -1.0;
        for (Student s : students) {
            if (s.getGradeCount() > 0 && s.getAverageScore() > maxAvg) {
                maxAvg = s.getAverageScore();
                top = s;
            }
        }
        return Optional.ofNullable(top);
    }

    /**
     * Finds the single lowest individual score recorded across all students.
     * Returns 0.0 if no grades are available.
     */
    public double getClassLowestScore() {
        double lowest = Double.MAX_VALUE;
        boolean hasGrades = false;
        for (Student s : students) {
            for (Double g : s.getGrades()) {
                hasGrades = true;
                if (g < lowest) {
                    lowest = g;
                }
            }
        }
        return hasGrades ? GradeUtils.round(lowest, 2) : 0.0;
    }

    /**
     * Returns the student who has the lowest average score.
     */
    public Optional<Student> getLowestPerformingStudent() {
        Student lowestStudent = null;
        double minAvg = Double.MAX_VALUE;
        for (Student s : students) {
            if (s.getGradeCount() > 0 && s.getAverageScore() < minAvg) {
                minAvg = s.getAverageScore();
                lowestStudent = s;
            }
        }
        return Optional.ofNullable(lowestStudent);
    }

    /**
     * Counts how many students passed (average >= 60.0).
     */
    public int getPassingStudentsCount() {
        int count = 0;
        for (Student s : students) {
            if (s.isPassing()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts how many students with recorded grades failed (average < 60.0).
     */
    public int getFailingStudentsCount() {
        int count = 0;
        for (Student s : students) {
            if (s.getGradeCount() > 0 && !s.isPassing()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the pass rate percentage (passed / total students with grades * 100).
     */
    public double getPassRatePercentage() {
        int gradedStudents = 0;
        for (Student s : students) {
            if (s.getGradeCount() > 0) {
                gradedStudents++;
            }
        }
        if (gradedStudents == 0) return 0.0;
        return GradeUtils.round(((double) getPassingStudentsCount() / gradedStudents) * 100.0, 2);
    }

    /**
     * Computes the distribution of letter grades ('A', 'B', 'C', 'D', 'F').
     */
    public Map<String, Integer> getGradeDistribution() {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("A", 0);
        distribution.put("B", 0);
        distribution.put("C", 0);
        distribution.put("D", 0);
        distribution.put("F", 0);

        for (Student s : students) {
            if (s.getGradeCount() > 0) {
                String letter = s.getLetterGrade();
                distribution.put(letter, distribution.getOrDefault(letter, 0) + 1);
            }
        }
        return distribution;
    }

    /**
     * Generates a comprehensive, beautifully formatted ASCII summary report.
     */
    public String generateSummaryReport() {
        StringBuilder sb = new StringBuilder();
        String divider = "---------------------------------------------------------------------------------------------------------";
        String doubleDivider = "=========================================================================================================";

        sb.append(doubleDivider).append("\n");
        sb.append("                            STUDENT GRADE TRACKER REPORT                             \n");
        sb.append(doubleDivider).append("\n");

        if (students.isEmpty()) {
            sb.append("No student records currently stored in the system.\n");
            sb.append(doubleDivider).append("\n");
            return sb.toString();
        }

        // Student Table Header
        sb.append(String.format("| %-8s | %-18s | %-24s | %-7s | %-7s | %-7s | %-6s | %-7s |\n",
                "ID", "NAME", "GRADES RECORDED", "AVG", "HIGH", "LOW", "GRADE", "STATUS"));
        sb.append(divider).append("\n");

        // Student Rows
        for (Student s : students) {
            String gradesStr = s.getGrades().isEmpty() ? "None" : s.getGrades().toString();
            if (gradesStr.length() > 24) {
                gradesStr = gradesStr.substring(0, 21) + "...";
            }
            sb.append(String.format("| %-8s | %-18s | %-24s | %7.2f | %7.2f | %7.2f | %-6s | %-7s |\n",
                    s.getId(),
                    truncate(s.getName(), 18),
                    gradesStr,
                    s.getAverageScore(),
                    s.getHighestScore(),
                    s.getLowestScore(),
                    s.getGradeCount() > 0 ? s.getLetterGrade() : "N/A",
                    s.getGradeCount() > 0 ? (s.isPassing() ? "PASSED" : "FAILED") : "NO DATA"
            ));
        }
        sb.append(divider).append("\n\n");

        // Statistical Summary Section
        sb.append("------------------------------------------ CLASS STATISTICS ---------------------------------------------\n");
        sb.append(String.format(" Total Students Registered    : %d\n", students.size()));
        sb.append(String.format(" Class Average Score          : %.2f%%\n", calculateClassAverage()));
        sb.append(String.format(" Class Highest Score          : %.2f\n", getClassHighestScore()));
        sb.append(String.format(" Class Lowest Score           : %.2f\n", getClassLowestScore()));

        Optional<Student> topStudent = getTopPerformingStudent();
        topStudent.ifPresent(student -> sb.append(String.format(" Top Performing Student       : %s (%s) with Average %.2f%%\n",
                student.getName(), student.getId(), student.getAverageScore())));

        Optional<Student> lowStudent = getLowestPerformingStudent();
        lowStudent.ifPresent(student -> sb.append(String.format(" Lowest Performing Student    : %s (%s) with Average %.2f%%\n",
                student.getName(), student.getId(), student.getAverageScore())));

        sb.append(String.format(" Passing Students (>= 60%%)    : %d\n", getPassingStudentsCount()));
        sb.append(String.format(" Failing Students (< 60%%)     : %d\n", getFailingStudentsCount()));
        sb.append(String.format(" Overall Pass Rate            : %.2f%%\n", getPassRatePercentage()));

        sb.append("\n Grade Distribution:\n");
        Map<String, Integer> dist = getGradeDistribution();
        for (Map.Entry<String, Integer> entry : dist.entrySet()) {
            sb.append(String.format("   - Grade %s : %2d student(s)  %s\n",
                    entry.getKey(), entry.getValue(), getProgressBar(entry.getValue(), students.size())));
        }
        sb.append(doubleDivider).append("\n");

        return sb.toString();
    }

    private String getProgressBar(int count, int total) {
        if (total == 0) return "";
        int bars = (int) Math.round(((double) count / total) * 20);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 20; i++) {
            if (i < bars) sb.append("#");
            else sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }

    /**
     * Loads sample student data for demonstration and testing.
     */
    public void loadSampleData() {
        clear();
        Student s1 = new Student("S101", "Emma Watson");
        s1.addGrades(95.0, 88.5, 92.0, 98.0, 91.5);

        Student s2 = new Student("S102", "James Smith");
        s2.addGrades(78.0, 82.5, 69.0, 74.0, 85.0);

        Student s3 = new Student("S103", "Sophia Miller");
        s3.addGrades(92.0, 96.0, 89.5, 94.0, 99.0);

        Student s4 = new Student("S104", "Liam Brown");
        s4.addGrades(55.0, 62.0, 58.5, 61.0, 50.0);

        Student s5 = new Student("S105", "Olivia Davis");
        s5.addGrades(84.0, 88.0, 81.5, 90.0, 86.5);

        Student s6 = new Student("S106", "Noah Wilson");
        s6.addGrades(42.0, 50.0, 48.0, 59.0, 51.5);

        addStudent(s1);
        addStudent(s2);
        addStudent(s3);
        addStudent(s4);
        addStudent(s5);
        addStudent(s6);
    }

    /**
     * Exports all student data into a CSV file.
     * Format: StudentID,Name,Grade1,Grade2,Grade3,...
     */
    public void exportToCSV(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("StudentID,Name,Grades");
            writer.newLine();
            for (Student s : students) {
                StringBuilder line = new StringBuilder();
                line.append(escapeCSV(s.getId())).append(",");
                line.append(escapeCSV(s.getName())).append(",");
                List<Double> grades = s.getGrades();
                for (int i = 0; i < grades.size(); i++) {
                    line.append(grades.get(i));
                    if (i < grades.size() - 1) {
                        line.append(";"); // Separate multiple grades with semicolon
                    }
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }

    /**
     * Imports student records from a CSV file.
     */
    public int importFromCSV(File file) throws IOException {
        int importedCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    // Skip header line if detected
                    if (line.toLowerCase().contains("student") || line.toLowerCase().contains("name")) {
                        continue;
                    }
                }

                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String id = parts[0].trim().replace("\"", "");
                    String name = parts[1].trim().replace("\"", "");

                    Student student;
                    Optional<Student> existing = getStudentById(id);
                    if (existing.isPresent()) {
                        student = existing.get();
                        student.setName(name);
                    } else {
                        student = new Student(id, name);
                        addStudent(student);
                    }

                    if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                        String gradesPart = parts[2].trim().replace("\"", "");
                        // Semicolon or space or comma separated
                        String[] gradeTokens = gradesPart.split("[;\\s]+");
                        for (String token : gradeTokens) {
                            if (!token.trim().isEmpty()) {
                                try {
                                    double score = Double.parseDouble(token.trim());
                                    if (GradeUtils.isValidGrade(score)) {
                                        student.addGrade(score);
                                    }
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                    }
                    importedCount++;
                }
            }
        }
        return importedCount;
    }

    /**
     * Saves the summary report string into a specified text file.
     */
    public void exportReportToFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(generateSummaryReport());
        }
    }

    private String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
