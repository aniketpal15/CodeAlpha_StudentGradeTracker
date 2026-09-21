package com.app.gradetracker.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for grade calculations, letter grade conversions,
 * GPA mapping, and formatting helpers.
 */
public final class GradeUtils {

    private GradeUtils() {
        // Prevent instantiation
    }

    /**
     * Rounds a double value to specified decimal places.
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Places must be >= 0");
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Converts a numerical percentage score (0-100) into a standard letter grade.
     *
     * 90 - 100 : A
     * 80 - 89.9: B
     * 70 - 79.9: C
     * 60 - 69.9: D
     * Below 60 : F
     */
    public static String toLetterGrade(double score) {
        if (score >= 90.0) {
            return "A";
        } else if (score >= 80.0) {
            return "B";
        } else if (score >= 70.0) {
            return "C";
        } else if (score >= 60.0) {
            return "D";
        } else {
            return "F";
        }
    }

    /**
     * Converts a numerical score into a standard 4.0 GPA scale.
     */
    public static double toGPA(double score) {
        if (score >= 90.0) {
            return 4.0;
        } else if (score >= 80.0) {
            return 3.0;
        } else if (score >= 70.0) {
            return 2.0;
        } else if (score >= 60.0) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    /**
     * Determines whether a score meets the passing threshold (60.0).
     */
    public static boolean isPassing(double score) {
        return score >= 60.0;
    }

    /**
     * Validates whether a grade is within the acceptable range [0.0, 100.0].
     */
    public static boolean isValidGrade(double score) {
        return score >= 0.0 && score <= 100.0;
    }
}
