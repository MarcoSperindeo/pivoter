package org.pivoter;

import org.junit.jupiter.api.Test;
import org.pivoter.model.PivotTree;
import org.pivoter.model.PivotTreeNode;
import org.pivoter.model.Row;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class PivotTreeTest {

    private final PivotTree pivotTree = new PivotTree();

    @Test
    void testBuild() {
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

        double valueRow1 = 10.0;
        double valueRow2 = 20.0;

        Row row1 = buildRow(labelsRow1, valueRow1);
        Row row2 = buildRow(labelsRow2, valueRow2);
        List<Row> rows = List.of(row1, row2);

        //when
        pivotTree.build(rows);

        //then
        PivotTreeNode root = pivotTree.getRoot();

        // assert root label, value, children
        assertThat(root).isNotNull();
        assertThat(root.getLabel()).isEqualTo("root");
        assertThat(root.getValue()).isEqualTo(valueRow1 + valueRow2);
        assertThat(root.getChildren())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        PivotTreeNode brown = root.getChild("brown");
        assertThat(brown).isNotNull();

        // assert brown label, value, children
        assertThat(brown.getLabel()).isEqualTo("brown");
        assertThat(brown.getValue()).isEqualTo(valueRow1 + valueRow2);
        assertThat(brown.getChildren())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);
        PivotTreeNode dark = brown.getChild("dark");
        assertThat(dark).isNotNull();
        PivotTreeNode blonde = brown.getChild("blonde");
        assertThat(blonde).isNotNull();

        // assert dark label, value, children
        assertThat(dark.getLabel()).isEqualTo("dark");
        assertThat(dark.getValue()).isEqualTo(valueRow1);
        assertThat(dark.getChildren())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        PivotTreeNode darkItaly = dark.getChild("italy");
        assertThat(darkItaly).isNotNull();

        // assert blonde label, value, children
        assertThat(blonde.getLabel()).isEqualTo("blonde");
        assertThat(blonde.getValue()).isEqualTo(valueRow2);
        assertThat(blonde.getChildren())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        PivotTreeNode blondeItaly = blonde.getChild("italy");
        assertThat(blondeItaly).isNotNull();

        // assert dark-italy label, value, children
        assertThat(darkItaly.getLabel()).isEqualTo("italy");
        assertThat(darkItaly.getValue()).isEqualTo(valueRow1);
        assertThat(darkItaly.getChildren()).isEmpty();

        // assert blonde-italy label, value, children
        assertThat(blondeItaly.getLabel()).isEqualTo("italy");
        assertThat(blondeItaly.getValue()).isEqualTo(valueRow2);
        assertThat(blondeItaly.getChildren()).isEmpty();
    }

    @Test
    void testBuildRecursive() {
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

        double valueRow1 = 10.0;
        double valueRow2 = 20.0;
        Row row1 = buildRow(labelsRow1, valueRow1);
        Row row2 = buildRow(labelsRow2, valueRow2);
        List<Row> rows = List.of(row1, row2);

        //when
        pivotTree.buildRecursive(rows);

        //then
        PivotTreeNode root = pivotTree.getRoot();

        // assert root label, value, children
        assertThat(root).isNotNull();
        assertThat(root.getLabel()).isEqualTo("root");
        assertThat(root.getValue()).isEqualTo(valueRow1 + valueRow2);
        assertThat(root.getChildren())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        PivotTreeNode brown = root.getChild("brown");
        assertThat(brown).isNotNull();

        // assert brown label, value, children
        assertThat(brown.getLabel()).isEqualTo("brown");
        assertThat(brown.getValue()).isEqualTo(valueRow1 + valueRow2);
        assertThat(brown.getChildren())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);
        PivotTreeNode dark = brown.getChild("dark");
        assertThat(dark).isNotNull();
        PivotTreeNode blonde = brown.getChild("blonde");
        assertThat(blonde).isNotNull();

        // assert dark label, value, children
        assertThat(dark.getLabel()).isEqualTo("dark");
        assertThat(dark.getValue()).isEqualTo(valueRow1);
        assertThat(dark.getChildren())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        PivotTreeNode darkItaly = dark.getChild("italy");
        assertThat(darkItaly).isNotNull();

        // assert blonde label, value, children
        assertThat(blonde.getLabel()).isEqualTo("blonde");
        assertThat(blonde.getValue()).isEqualTo(valueRow2);
        assertThat(blonde.getChildren())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        PivotTreeNode blondeItaly = blonde.getChild("italy");
        assertThat(blondeItaly).isNotNull();

        // assert dark-italy label, value, children
        assertThat(darkItaly.getLabel()).isEqualTo("italy");
        assertThat(darkItaly.getValue()).isEqualTo(valueRow1);
        assertThat(darkItaly.getChildren()).isEmpty();

        // assert blonde-italy label, value, children
        assertThat(blondeItaly.getLabel()).isEqualTo("italy");
        assertThat(blondeItaly.getValue()).isEqualTo(valueRow2);
        assertThat(blondeItaly.getChildren()).isEmpty();
    }

    @Test
    void testToString() {
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

        double valueRow1 = 10.0;
        double valueRow2 = 20.0;

        Row row1 = buildRow(labelsRow1, valueRow1);
        Row row2 = buildRow(labelsRow2, valueRow2);
        List<Row> rows = List.of(row1, row2);

        pivotTree.build(rows);

        //when-then
        System.out.println(pivotTree);
    }

    @Test
    void testQuery() {
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

        double valueRow1 = 10.0;
        double valueRow2 = 20.0;

        Row row1 = buildRow(labelsRow1, valueRow1);
        Row row2 = buildRow(labelsRow2, valueRow2);
        List<Row> rows = List.of(row1, row2);

        pivotTree.build(rows);

        // when
        List<String> queryLabels1 = List.of("brown");

        Double result1 = pivotTree.query(queryLabels1);

        //then
        assertThat(result1)
                .isNotNull()
                .isEqualTo(valueRow1 + valueRow2);

        // when
        List<String> queryLabels2 = List.of("brown", "dark", "italy");

        Double result2 = pivotTree.query(queryLabels2);

        //then
        assertThat(result2)
                .isNotNull()
                .isEqualTo(valueRow1);

        // when
        List<String> queryLabels3 = List.of("brown", "blonde", "italy");

        Double result3 = pivotTree.query(queryLabels3);

        //then
        assertThat(result3)
                .isNotNull()
                .isEqualTo(valueRow2);
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
