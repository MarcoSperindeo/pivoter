package org.pivoter;

import org.pivoter.model.PivotTree;
import org.pivoter.model.Row;
import org.pivoter.model.PivotTreeNode;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Pivoter {

    private PivotTree pivotTree;
    private Comparator<String> pivotOrder;

    public Pivoter() {
        this.pivotTree = new PivotTree();
        this.pivotOrder = Comparator.naturalOrder();
    }

    // make it generic
    public PivotTreeNode pivot(List<Map<String, String>> data,
                               Comparator<String> pivotOrder) {
        // validate data rowᵢ = (L₁ᵢ, L₂ᵢ, L₃ᵢ, ..., Lₙᵢ, Vᵢ)
        // build data rows sorting labels by pivot order
        // build pivot tree
        return null;
    }

    // make it generic
    public Double query(PivotTree pivotTree, List<String> queryLabels) {
        // sort query labels by pivot order
        // query tree by labels
        return null;
    }


}
