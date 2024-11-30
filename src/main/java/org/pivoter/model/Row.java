package org.pivoter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Row {

    private final Map<String, String> labels; // Labels with domain names as keys
    private final Double value; // Value

    public Row(Map<String, String> labels, Double value) {
        this.labels = labels;
        this.value = value;
    }

    public List<String> getLabels() {
        return new ArrayList<>(labels.values());
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("Row{labels=%s, value=%.2f}", labels, value);
    }
}
