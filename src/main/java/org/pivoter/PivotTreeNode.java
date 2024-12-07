package org.pivoter;

import java.util.*;

public class PivotTreeNode {

    private String label;
    private final List<Double> values;
    private final Map<String, PivotTreeNode> children;

    public PivotTreeNode(String label) {
        this.children = new HashMap<>();
        this.values = new ArrayList<>();
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Double> getValues() {
        return values;
    }

    public void addValue(Double value) {
        this.values.add(value);
    }

    public Map<String, PivotTreeNode> getChildren() {
        return children;
    }

    public PivotTreeNode getChild(String label) {
        return children.getOrDefault(label, null);
    }

    public PivotTreeNode getOrAddChild(String label) {
        PivotTreeNode child = this.getChild(label);

        if (child == null) {
            child = new PivotTreeNode(label);
            this.addChildIfNotPresent(label, child);
        }
        return child;
    }

    private void addChildIfNotPresent(String label, PivotTreeNode child) {
        if (children.containsKey(label)) return;
        children.put(label, child);
    }

    @Override
    public String toString() {
        return "PivotTreeNode{" +
                "label='" + label + '\'' +
                ", value=" + values +
                ", children=" + children +
                '}';
    }
}
