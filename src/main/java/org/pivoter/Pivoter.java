package org.pivoter;

import org.pivoter.model.PivotTree;
import org.pivoter.model.Row;
import org.pivoter.model.PivotTreeNode;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class Pivoter {

    PivotTree pivotTree = new PivotTree();

    // make it generic
    public PivotTreeNode pivot(String dataCsvPath,
                               Function<Double, List<Double>> pivotFunction,
                               Comparator<String> pivotOrder) {
        // build data rows labels as a tree-map sorted by pivot order
        // build pivot tree aggregating by pivot function
        return null;
    }
}
