package com.app.gradetracker;

import com.app.gradetracker.model.GradeTracker;
import com.app.gradetracker.model.Student;
import com.app.gradetracker.util.GradeUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Automated test suite for Student Grade Tracker.
 * Runs independently without requiring external testing libraries.
 */
public class GradeTrackerTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("       RUNNING STUDENT GRADE TRACKER TESTS      ");
        System.out.println("==========================================================");

        testStudentCreation();
        testStudentGradeCalculations();
        testGradeValidation();
        testGradeTrackerClassStats();
        testGradeDistribution();
        testSearchAndFilter();
        testCsvExportImport();

        System.out.println("==========================================================");
        System.out.printf("Test Results: %d Passed | %d Failed\n", testsPassed, testsFailed);
        System.out.println("==========================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println(" [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println(" [FAIL] " + testName);
            testsFailed++;
        }
    }

    private static void assertEquals(String testName, double expected, double actual, double delta) {
        boolean match = Math.abs(expected - actual) <= delta;
        if (match) {
            System.out.println(" [PASS] " + testName + " (expected=" + expected + ", actual=" + actual + ")");
            testsPassed++;
        } else {
            System.err.println(" [FAIL] " + testName + " (expected=" + expected + ", actual=" + actual + ")");
            testsFailed++;
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            System.out.println(" [PASS] " + testName + " (" + actual + ")");
            testsPassed++;
        } else {
            System.err.println(" [FAIL] " + testName + " (expected=" + expected + ", actual=" + actual + ")");
            testsFailed++;
        }
    }

    private static void testStudentCreation() {
        System.out.println("\n--- Test: Student Creation ---");
        Student s = new Student("S101", "Alice Wonder");
        assertEquals("Student ID set correctly", "S101", s.getId());
        assertEquals("Student Name set correctly", "Alice Wonder", s.getName());
        assertEquals("Initial grade count is 0", 0, s.getGradeCount());
        assertEquals("Initial average is 0.0", 0.0, s.getAverageScore(), 0.001);
    }

    private static void testStudentGradeCalculations() {
        System.out.println("\n--- Test: Student Grade Calculations ---");
        Student s = new Student("S102", "Bob Builder");
        s.addGrades(80.0, 90.0, 70.0);

        assertEquals("Grade count is 3", 3, s.getGradeCount());
        assertEquals("Total score is 240.0", 240.0, s.getTotalScore(), 0.01);
        assertEquals("Average score is 80.0", 80.0, s.getAverageScore(), 0.01);
        assertEquals("Highest score is 90.0", 90.0, s.getHighestScore(), 0.01);
        assertEquals("Lowest score is 70.0", 70.0, s.getLowestScore(), 0.01);
        assertEquals("Letter grade is B", "B", s.getLetterGrade());
        assertEquals("GPA is 3.0", 3.0, s.getGPA(), 0.01);
        assertTrue("Student is passing", s.isPassing());

        // Test failing student
        Student failStudent = new Student("S103", "Charlie Brown");
        failStudent.addGrades(45.0, 52.0);
        assertEquals("Failing student average", 48.5, failStudent.getAverageScore(), 0.01);
        assertEquals("Failing letter grade is F", "F", failStudent.getLetterGrade());
        assertTrue("Student is not passing", !failStudent.isPassing());
    }

    private static void testGradeValidation() {
        System.out.println("\n--- Test: Grade Range Validation ---");
        Student s = new Student("S104", "David Tester");

        boolean threwNegative = false;
        try {
            s.addGrade(-5.0);
        } catch (IllegalArgumentException e) {
            threwNegative = true;
        }
        assertTrue("Rejects negative score (<0)", threwNegative);

        boolean threwOver100 = false;
        try {
            s.addGrade(105.0);
        } catch (IllegalArgumentException e) {
            threwOver100 = true;
        }
        assertTrue("Rejects score exceeding 100 (>100)", threwOver100);

        s.addGrade(0.0);
        s.addGrade(100.0);
        assertEquals("Accepts boundary values 0 and 100", 2, s.getGradeCount());
    }

    private static void testGradeTrackerClassStats() {
        System.out.println("\n--- Test: Class Statistics ---");
        GradeTracker tracker = new GradeTracker();

        Student s1 = new Student("S1", "Alice");
        s1.addGrades(90.0, 100.0); // avg 95.0

        Student s2 = new Student("S2", "Bob");
        s2.addGrades(70.0, 80.0);  // avg 75.0

        Student s3 = new Student("S3", "Charlie");
        s3.addGrades(50.0, 40.0);  // avg 45.0 (failing)

        tracker.addStudent(s1);
        tracker.addStudent(s2);
        tracker.addStudent(s3);

        assertEquals("Total students is 3", 3, tracker.getStudentCount());
        // Class average = (95 + 75 + 45) / 3 = 215 / 3 = 71.67
        assertEquals("Class average score", 71.67, tracker.calculateClassAverage(), 0.01);
        assertEquals("Class highest score", 100.0, tracker.getClassHighestScore(), 0.01);
        assertEquals("Class lowest score", 40.0, tracker.getClassLowestScore(), 0.01);

        assertTrue("Top student is Alice", tracker.getTopPerformingStudent().isPresent()
                && tracker.getTopPerformingStudent().get().getName().equals("Alice"));

        assertTrue("Lowest student is Charlie", tracker.getLowestPerformingStudent().isPresent()
                && tracker.getLowestPerformingStudent().get().getName().equals("Charlie"));

        assertEquals("Passing students count is 2", 2, tracker.getPassingStudentsCount());
        assertEquals("Failing students count is 1", 1, tracker.getFailingStudentsCount());
        // Pass rate = 2 / 3 * 100 = 66.67%
        assertEquals("Pass rate percentage", 66.67, tracker.getPassRatePercentage(), 0.01);
    }

    private static void testGradeDistribution() {
        System.out.println("\n--- Test: Grade Distribution ---");
        GradeTracker tracker = new GradeTracker();

        Student s1 = new Student("1", "A-Student"); s1.addGrade(95.0);
        Student s2 = new Student("2", "B-Student"); s2.addGrade(85.0);
        Student s3 = new Student("3", "C-Student"); s3.addGrade(75.0);
        Student s4 = new Student("4", "D-Student"); s4.addGrade(65.0);
        Student s5 = new Student("5", "F-Student"); s5.addGrade(45.0);

        tracker.addStudent(s1);
        tracker.addStudent(s2);
        tracker.addStudent(s3);
        tracker.addStudent(s4);
        tracker.addStudent(s5);

        Map<String, Integer> dist = tracker.getGradeDistribution();
        assertEquals("Count of A grades is 1", Integer.valueOf(1), dist.get("A"));
        assertEquals("Count of B grades is 1", Integer.valueOf(1), dist.get("B"));
        assertEquals("Count of C grades is 1", Integer.valueOf(1), dist.get("C"));
        assertEquals("Count of D grades is 1", Integer.valueOf(1), dist.get("D"));
        assertEquals("Count of F grades is 1", Integer.valueOf(1), dist.get("F"));
    }

    private static void testSearchAndFilter() {
        System.out.println("\n--- Test: Search and Filter ---");
        GradeTracker tracker = new GradeTracker();
        tracker.addStudent(new Student("STU01", "Harry Potter"));
        tracker.addStudent(new Student("STU02", "Hermione Granger"));
        tracker.addStudent(new Student("STU03", "Ron Weasley"));

        ArrayList<Student> found = tracker.searchStudents("Hermione");
        assertEquals("Search by name returns 1 result", 1, found.size());
        assertEquals("Found correct student", "Hermione Granger", found.get(0).getName());

        ArrayList<Student> foundById = tracker.searchStudents("STU03");
        assertEquals("Search by ID returns Ron", "Ron Weasley", foundById.get(0).getName());

        tracker.removeStudent("STU03");
        assertEquals("Student count after removal is 2", 2, tracker.getStudentCount());
    }

    private static void testCsvExportImport() {
        System.out.println("\n--- Test: CSV Export and Import ---");
        GradeTracker tracker = new GradeTracker();
        Student s1 = new Student("CSV1", "Luna Lovegood");
        s1.addGrades(88.0, 92.5);
        tracker.addStudent(s1);

        File tempFile = null;
        try {
            tempFile = File.createTempFile("gradetracker_test", ".csv");
            tracker.exportToCSV(tempFile);
            assertTrue("CSV file exists", tempFile.exists() && tempFile.length() > 0);

            GradeTracker importedTracker = new GradeTracker();
            int count = importedTracker.importFromCSV(tempFile);
            assertEquals("Imported 1 student", 1, count);
            assertTrue("Imported student ID matches", importedTracker.getStudentById("CSV1").isPresent());
            Student importedStudent = importedTracker.getStudentById("CSV1").get();
            assertEquals("Imported student name matches", "Luna Lovegood", importedStudent.getName());
            assertEquals("Imported student grades count", 2, importedStudent.getGradeCount());
            assertEquals("Imported student average", 90.25, importedStudent.getAverageScore(), 0.01);
        } catch (IOException e) {
            assertTrue("CSV Export/Import exception: " + e.getMessage(), false);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
