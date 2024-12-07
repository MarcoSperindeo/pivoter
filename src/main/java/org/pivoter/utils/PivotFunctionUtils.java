package org.pivoter.model;
public class PivotFunctionHelper{
public PivotFunctionHelper() {
}
private double sum(java.util.Collection<java.lang.Double> values) {
        double res = 0.0;
        for (java.lang.Double value : values) {
            res += value;
        }
        return res;
    }private double average(java.util.Collection<java.lang.Double> values) {
        double sum = 0.0;
        int i = 0;
        for (java.lang.Double value : values) {
            sum += value;
            i++;
        }
        return sum / i;
    }private double mode(java.util.Collection<java.lang.Double> values) {
        java.util.Map<java.lang.Double,java.lang.Integer> occurrences = new java.util.HashMap<java.lang.Double,java.lang.Integer>();
        for (java.lang.Double value : values) {
            occurrences.put(value, occurrences.getOrDefault(value, 0) + 1);
        }
        double res = 0.0;
        double max = java.lang.Double.MIN_VALUE;
        for (java.lang.Double occurrence : occurrences.keySet()) {
            if (occurrences.get(occurrence) > max) {
                max = occurrences.get(occurrence);
                res = occurrence;
            }
        }
        return res;
    }}