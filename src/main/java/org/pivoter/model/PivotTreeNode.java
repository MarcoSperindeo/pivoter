package org.pivoter.model;

import java.util.HashMap;
import java.util.Map;

public class PivotTreeNode {

    private String label;
    private Double value;
    private Map<String, PivotTreeNode> children;

    public PivotTreeNode(String label) {
        children = new HashMap<>();
        this.label = label;
        this.value = 0.0;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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

    public void addChildIfNotPresent(String label, PivotTreeNode child) {
        if (children.containsKey(label)) {
            return;
        }
        children.put(label, child);
    }

    @Override
    public String toString() {
        return "PivotTreeNode{" +
                "label='" + label + '\'' +
                ", value=" + value +
                ", children=" + children +
                '}';
    }
}
