package org.pivoter.model;

import java.util.List;

public class PivotTree {

    private PivotTreeNode root;

    public PivotTree() {
        this.root = null;
    }

    public PivotTreeNode getRoot() {
        return root;
    }

    public void build(List<Row> rows) {
        PivotTreeNode root = new PivotTreeNode("root");

        for (Row row : rows) {
            PivotTreeNode node = root;

            List<String> sortedLabels = row.getLabels(); //
            sortedLabels.add(0, "root"); // ensure that "root" is the starting label

            while (!sortedLabels.isEmpty()) {

                String currentLabel = sortedLabels.get(0);
                // if current node does not have label, set it
                if (node.getLabel() == null)
                    node.setLabel(currentLabel);

                // apply aggregate function
                node.setValue(node.getValue() + row.getValue());

                if (sortedLabels.size() > 1) { // ensure leaf is not reached
                    String nextLabel = sortedLabels.get(1);
                    // set child as current node for next iteration
                    node = node.getOrAddChild(nextLabel);
                }

                // remove first element from sorted labels
                sortedLabels.remove(0);
            }
        }

        this.root = root;
    }

    public void buildRecursive(List<Row> rows) {
        PivotTreeNode root = new PivotTreeNode("root");

        for (Row row : rows) {
            List<String> sortedLabels = row.getLabels();
            sortedLabels.add(0, "root"); // ensure that "root" is the starting label

            buildRecursive(root, sortedLabels, row);
        }

        this.root = root;
    }

    private void buildRecursive(PivotTreeNode node, List<String> sortedLabels, Row row) {
        // apply aggregate function
        node.setValue(node.getValue() + row.getValue());

        // leaf is reached
        if (sortedLabels.size() == 1) { // termination condition
            return;
        }

        String currentLabel = sortedLabels.get(0);
        // if current node des not have label, set it
        if (node.getLabel() == null) {
            node.setLabel(currentLabel);
        }

        String nextLabel = sortedLabels.get(1);
        PivotTreeNode child = node.getOrAddChild(nextLabel);

        // recur
        buildRecursive(child, sortedLabels.subList(1, sortedLabels.size()), row);
    }

    public Double query(List<String> sortedLabels) {
        if (root == null || sortedLabels == null || sortedLabels.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return queryRecursive(root, sortedLabels);
    }

    private Double queryRecursive(PivotTreeNode node, List<String> sortedLabels) {
        if (node == null) { // node does not exist
            return 0.0;
        }

        if (sortedLabels.isEmpty()) { // termination condition
            return node.getValue();
        }

        return queryRecursive(
                node.getChild(sortedLabels.get(0)),
                sortedLabels.subList(1, sortedLabels.size()));
    }

    @Override
    public String toString() {
        return toStringRecursive(this.root, "");
    }

    private String toStringRecursive(PivotTreeNode node, String indent) {
        if (node == null) {
            return "";
        }

        StringBuilder tree = new StringBuilder();
        tree.append("\n");
        tree.append(indent).append(node.getLabel())
                .append(" (").append(node.getValue()).append(")");

        for (PivotTreeNode child : node.getChildren().values()) {
            tree.append(toStringRecursive(child, indent + "  "));
        }

        return tree.toString();
    }
}
