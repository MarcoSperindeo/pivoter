package org.pivoter.model;

import java.util.List;

public class PivotRow {

    private final List<String> labels;
    private final Double value; // Value

    public PivotRow(List<String> labels, Double value) {
        this.labels = labels;
        this.value = value;
    }

    public List<String> getLabels() {
        return labels;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("Row{labels=%s, value=%.2f}", labels, value);
    }
}
