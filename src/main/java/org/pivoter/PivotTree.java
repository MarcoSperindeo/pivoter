package org.pivoter;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class PivotTree {

    private final PivotTreeNode root;

    public PivotTree() {
        this.root = new PivotTreeNode("root");
    }

    public PivotTreeNode getRoot() {
        return root;
    }

    void build(List<PivotRow> pivotRows) {
        if (pivotRows == null) {
            throw new IllegalArgumentException("Input pivot rows cannot be null.");
        }

        for (PivotRow pivotRow : pivotRows) {
            List<String> sortedLabels = pivotRow.getLabels();
            sortedLabels.add(0, "root"); // ensure that "root" is the starting label

            PivotTreeNode node = root;

            while (!sortedLabels.isEmpty()) {
                String currentLabel = sortedLabels.get(0);
                // if current node does not have label, set it
                if (node.getLabel() == null) node.setLabel(currentLabel);

                node.addValue(pivotRow.getValue());

                if (sortedLabels.size() > 1) { // ensure leaf is not reached
                    String nextLabel = sortedLabels.get(1);
                    // set child as current node for next iteration
                    node = node.getOrAddChild(nextLabel);
                }
                // remove first element from sorted labels
                sortedLabels.remove(0);
            }
        }
    }

    void buildRecursive(List<PivotRow> pivotRows) {
        if (pivotRows == null) {
            throw new IllegalArgumentException("Input pivot rows cannot be null.");
        }

        for (PivotRow pivotRow : pivotRows) {
            List<String> sortedLabels = pivotRow.getLabels();
            sortedLabels.add(0, "root"); // ensure that "root" is the starting label

            buildRecursive(root, sortedLabels, pivotRow);
        }
    }

    private void buildRecursive(PivotTreeNode node, List<String> sortedLabels, PivotRow pivotRow) {
        node.addValue(pivotRow.getValue());

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
        buildRecursive(child, sortedLabels.subList(1, sortedLabels.size()), pivotRow);
    }

    Double query(List<String> queryLabels, Function<Collection<Double>, Double> pivotFunction) {
        if (queryLabels == null) {
            throw new IllegalArgumentException("Input query labels cannot be null.");
        }

        return queryRecursive(root, queryLabels, pivotFunction);
    }

    private Double queryRecursive(PivotTreeNode node, List<String> sortedQueryLabels, Function<Collection<Double>, Double> pivotFunction) {
        if (node == null) { // node does not exist
            return 0.0;
        }

        if (sortedQueryLabels.isEmpty()) { // termination condition
            return pivotFunction.apply(node.getValues());
        }

        return queryRecursive(
                node.getChild(sortedQueryLabels.get(0)),
                sortedQueryLabels.subList(1, sortedQueryLabels.size()),
                pivotFunction);
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
                .append(" (").append(node.getValues()).append(")");

        for (PivotTreeNode child : node.getChildren().values()) {
            tree.append(toStringRecursive(child, indent + "  "));
        }

        return tree.toString();
    }
}
