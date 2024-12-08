package org.pivoter;

import java.util.ArrayList;
import java.util.List;

public class PivotRow {

    private final List<String> labels;
    private Double value;

    public PivotRow() {
        this.labels = new ArrayList<>();
        value = null;
    }

    public PivotRow(Double value) {
        this.labels = new ArrayList<>();
        this.value = value;
    }

    PivotRow(List<String> labels, Double value) {
        this.labels = labels;
        this.value = value;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void addLabel(String labelValue) {
        this.labels.add(labelValue);
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Row{labels=%s, value=%.2f}", labels, value);
    }
}
