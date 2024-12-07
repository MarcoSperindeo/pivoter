package org.pivoter.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PivotFunctionUtils {

    public PivotFunctionUtils() {
    }

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