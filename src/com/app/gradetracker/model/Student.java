package com.app.gradetracker.model;

import com.app.gradetracker.util.GradeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a student with an ID, name, and an ArrayList of grades.
 * Provides statistical calculations such as average, highest, and lowest scores.
 */
public class Student {

    private String id;
    private String name;
    private ArrayList<Double> grades;

    /**
     * Constructs a student with ID and name, initializing an empty grades list.
     */
    public Student(String id, String name) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be null or empty.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.grades = new ArrayList<>();
    }

    /**
     * Constructs a student with ID, name, and an initial list of grades.
     */
    public Student(String id, String name, List<Double> initialGrades) {
        this(id, name);
        if (initialGrades != null) {
            for (Double grade : initialGrades) {
                if (grade != null) {
                    addGrade(grade);
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty.");
        }
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    /**
     * Returns a copy of the grades list to preserve encapsulation.
     */
    public ArrayList<Double> getGrades() {
        return new ArrayList<>(grades);
    }

    /**
     * Adds a single grade score (between 0.0 and 100.0).
     */
    public void addGrade(double score) {
        if (!GradeUtils.isValidGrade(score)) {
            throw new IllegalArgumentException("Grade must be between 0.0 and 100.0. Given: " + score);
        }
        grades.add(GradeUtils.round(score, 2));
    }

    /**
     * Adds multiple grades at once.
     */
    public void addGrades(double... scores) {
        if (scores != null) {
            for (double score : scores) {
                addGrade(score);
            }
        }
    }

    /**
     * Removes a grade by its index in the list.
     */
    public boolean removeGrade(int index) {
        if (index >= 0 && index < grades.size()) {
            grades.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Clears all recorded grades for this student.
     */
    public void clearGrades() {
        grades.clear();
    }

    /**
     * Returns total number of grades recorded.
     */
    public int getGradeCount() {
        return grades.size();
    }

    /**
     * Calculates the sum total of all grades.
     */
    public double getTotalScore() {
        double total = 0.0;
        for (double g : grades) {
            total += g;
        }
        return GradeUtils.round(total, 2);
    }

    /**
     * Calculates the average score for this student.
     * Returns 0.0 if no grades have been entered yet.
     */
    public double getAverageScore() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (double g : grades) {
            sum += g;
        }
        return GradeUtils.round(sum / grades.size(), 2);
    }

    /**
     * Returns the highest score among this student's grades.
     * Returns 0.0 if no grades are available.
     */
    public double getHighestScore() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double highest = grades.get(0);
        for (int i = 1; i < grades.size(); i++) {
            if (grades.get(i) > highest) {
                highest = grades.get(i);
            }
        }
        return GradeUtils.round(highest, 2);
    }

    /**
     * Returns the lowest score among this student's grades.
     * Returns 0.0 if no grades are available.
     */
    public double getLowestScore() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double lowest = grades.get(0);
        for (int i = 1; i < grades.size(); i++) {
            if (grades.get(i) < lowest) {
                lowest = grades.get(i);
            }
        }
        return GradeUtils.round(lowest, 2);
    }

    /**
     * Returns the letter grade based on the student's average score.
     */
    public String getLetterGrade() {
        return GradeUtils.toLetterGrade(getAverageScore());
    }

    /**
     * Returns the GPA (on a 4.0 scale) based on average score.
     */
    public double getGPA() {
        return GradeUtils.toGPA(getAverageScore());
    }

    /**
     * Checks if the student passes (average >= 60.0).
     */
    public boolean isPassing() {
        return !grades.isEmpty() && GradeUtils.isPassing(getAverageScore());
    }

    /**
     * Formats grades into a readable string (e.g., "[85.0, 92.5, 78.0]").
     */
    public String getFormattedGrades() {
        if (grades.isEmpty()) {
            return "No grades recorded";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < grades.size(); i++) {
            sb.append(grades.get(i));
            if (i < grades.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Student[ID=%s, Name=%s, Grades=%s, Avg=%.2f, Letter=%s, Status=%s]",
                id, name, getFormattedGrades(), getAverageScore(), getLetterGrade(),
                (isPassing() ? "Passed" : "Failed"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id.toLowerCase(), student.id.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.toLowerCase());
    }
}
