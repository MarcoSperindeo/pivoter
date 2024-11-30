package org.pivoter;

import org.pivoter.model.PivotTree;
import org.pivoter.model.Row;
import org.pivoter.model.PivotTreeNode;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Pivoter {

    PivotTree pivotTree = new PivotTree();

    // make it generic
    public PivotTreeNode pivot(List<Map<String, String>> data,
                               Function<Double, List<Double>> pivotFunction,
                               Comparator<String> pivotOrder) {
        // validate data rowᵢ = (L₁ᵢ, L₂ᵢ, L₃ᵢ, ..., Lₙᵢ, Vᵢ)
        // build data rows sorting labels by pivot order
        // build pivot tree aggregating by pivot function
        // return pivot tree
        return null;
    }

    // make it generic
    public Double query(PivotTree pivotTree, List<String> queryLabels, Comparator<String> pivotOrder) {
        // sort query labels by pivot order
        // query that MFing tree bitch
        // return query result
        return null;
    }


}
