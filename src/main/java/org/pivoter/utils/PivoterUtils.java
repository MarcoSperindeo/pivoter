package org.pivoter.utils;

import java.util.*;

public class PivoterUtils {

    public PivoterUtils() {
    }

    public static boolean isDouble(String str) {
        if (str == null || str.isEmpty()) {
            return false; // null or empty strings are not valid Doubles
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /********** pivot hierarchy **********/

    public static Comparator<String> getHierarchyComparator(List<String> pivotHierarchy) {
        return (s1, s2) -> {
            // ensure both strings are valid
            if (!pivotHierarchy.contains(s1)) {
                throw new IllegalArgumentException("Invalid string: " + s1);
            }
            if (!pivotHierarchy.contains(s2)) {
                throw new IllegalArgumentException("Invalid string: " + s2);
            }

            // compare based on the pivot hierarchy
            return Integer.compare(pivotHierarchy.indexOf(s1), pivotHierarchy.indexOf(s2));
        };
    }

    /********** custom aggregation functions **********/

    public static double sum(Collection<Double> values) {
        double res = 0.0;
        for (Double value : values) {
            res += value;
        }
        return res;
    }

    public static double average(Collection<Double> values) {
        double sum = 0.0;
        int i = 0;
        for (Double value : values) {
            sum += value;
            i++;
        }
        return sum / i;
    }

    public static double mode(Collection<Double> values) {
        Map<Double, Integer> occurrences = new HashMap<Double, Integer>();
        for (Double value : values) {
            occurrences.put(value, occurrences.getOrDefault(value, 0) + 1);
        }
        double res = 0.0;
        double max = Double.MIN_VALUE;
        for (Double occurrence : occurrences.keySet()) {
            if (occurrences.get(occurrence) > max) {
                max = occurrences.get(occurrence);
                res = occurrence;
            }
        }
        return res;
    }
}