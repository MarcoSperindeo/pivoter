package org.pivoter.model;

import java.util.List;

public class PivotTree {

    private PivotTreeNode root;

    public void buildPivotTree(List<Row> rows) {

        PivotTreeNode root = new PivotTreeNode("root");

        for (Row row : rows) {
            PivotTreeNode parent = root;

            List<String> sortedLabels = row.getSortedLabels();
            sortedLabels.add(0, "root"); // ensure that "root" is the starting label

            while (!sortedLabels.isEmpty()) {

                String currentLabel = sortedLabels.get(0);
                // if current parent des not have label, set it
                if (parent.getLabel() == null)
                    parent.setLabel(currentLabel);

                // apply aggregate function
                parent.setValue(parent.getValue() + row.getValue());

                if (sortedLabels.size() > 1) { // ensure leaf is not reached
                    String nextLabel = sortedLabels.get(1);
                    PivotTreeNode child = parent.getOrAddChild(nextLabel);
                    // set child as current parent for next iteration
                    parent = child;
                }

                // remove first element from sorted labels
                sortedLabels.remove(0);
            }
        }

        this.root = root;
    }

    public void buildPivotTreeRecursive(List<Row> rows) {
        PivotTreeNode root = new PivotTreeNode("root");

        for (Row row : rows) {
            List<String> sortedLabels = row.getSortedLabels();
            sortedLabels.add(0, "root"); // ensure that "root" is the starting label

            buildTreeRecursive(root, sortedLabels, row);
        }

        this.root = root;
    }

    private void buildTreeRecursive(PivotTreeNode parent, List<String> sortedLabels, Row row) {
        if (sortedLabels.size() == 1) {
            return; // leaf is reached
        }

        String currentLabel = sortedLabels.get(0);
        // if current parent des not have label, set it
        if (parent.getLabel() == null) {
            parent.setLabel(currentLabel);
        }

        // apply aggregate function
        parent.setValue(parent.getValue() + row.getValue());

        String nextLabel = sortedLabels.get(1);
        PivotTreeNode child = parent.getOrAddChild(nextLabel);

        // recur
        buildTreeRecursive(child, sortedLabels.subList(1, sortedLabels.size()), row);

    }

    public PivotTreeNode getRoot() {
        return root;
    }

    // query pivot tree method

    // toString pivot tree method
}
