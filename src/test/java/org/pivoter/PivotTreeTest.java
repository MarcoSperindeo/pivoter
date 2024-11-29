package org.pivoter;

import org.junit.jupiter.api.Test;
import org.pivoter.model.PivotTree;
import org.pivoter.model.Row;

import java.util.*;

class PivotTreeTest {

    private final PivotTree pivotTree = new PivotTree();

    @Test
    void testBuildPivotTree() {
        //given
        List<List<String>> labelsRow1 = Arrays.asList(
                List.of("eyes", "brown"),
                List.of("hair", "dark"),
                List.of("nation", "italy")
        );
        List<List<String>> labelsRow2 = Arrays.asList(
                List.of("eyes", "brown"),
                List.of("hair", "blonde"),
                List.of("nation", "italy")
        );

        Row row1 = buildRow(labelsRow1, 10.0);
        Row row2 = buildRow(labelsRow2, 10.0);
        List<Row> rows = List.of(row1, row2);

        //when
        pivotTree.buildPivotTree(rows);

        //then
    }

    @Test
    void testBuildPivotTreeRecursive() {
        //given
        List<List<String>> labelsRow1 = Arrays.asList(
                List.of("eyes", "brown"),
                List.of("hair", "dark"),
                List.of("nation", "italy")
        );
        List<List<String>> labelsRow2 = Arrays.asList(
                List.of("eyes", "brown"),
                List.of("hair", "blonde"),
                List.of("nation", "italy")
        );

        Row row1 = buildRow(labelsRow1, 10.0);
        Row row2 = buildRow(labelsRow2, 10.0);
        List<Row> rows = List.of(row1, row2);

        //when
        pivotTree.buildPivotTreeRecursive(rows);

        //then
    }

    private Row buildRow(List<List<String>> labelsRow, Double value) {
        // linked hash-map guarantees the keys insertion order is kept
        Map<String, String> sortedLabels = new LinkedHashMap<>();

        for (List<String> labels : labelsRow) {
            sortedLabels.put(labels.get(0), labels.get(1));
        }

        Row row = new Row(sortedLabels, value);
        return row;
    }
}
