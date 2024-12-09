package org.pivoter;

import org.junit.jupiter.api.Test;
import org.pivoter.utils.PivoterUtils;

import java.util.*;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PivotTreeTest {

    private final PivotTree pivotTree = new PivotTree();

    @Test
    void testBuild() {
        // given
        double valueRow1 = 10.0;
        double valueRow2 = 20.0;
        double valueRow3 = 30.0;
        List<PivotRow> pivotRows = buildRowsWithNaturalOrderSortedLabels(valueRow1, valueRow2, valueRow3);

        // when
        pivotTree.build(pivotRows);

        //then
        PivotTreeNode root = pivotTree.getRoot();

        // assert root label, value, children
        assertThat(root).isNotNull();
        assertThat(root.getLabel()).isEqualTo("root");
        assertThat(root.getValues()).isEqualTo(List.of(valueRow1, valueRow2, valueRow3));
        assertThat(root.getChildren()).isNotNull().isNotEmpty().hasSize(1);
        PivotTreeNode brown = root.getChild("brown");
        assertThat(brown).isNotNull();

        // assert brown label, value, children
        assertThat(brown.getLabel()).isEqualTo("brown");
        assertThat(brown.getValues()).isEqualTo(List.of(valueRow1, valueRow2, valueRow3));
        assertThat(brown.getChildren()).isNotNull().isNotEmpty().hasSize(2);
        PivotTreeNode dark = brown.getChild("dark");
        assertThat(dark).isNotNull();
        PivotTreeNode blonde = brown.getChild("blonde");
        assertThat(blonde).isNotNull();

        // assert dark label, value, children
        assertThat(dark.getLabel()).isEqualTo("dark");
        assertThat(dark.getValues()).isEqualTo(List.of(valueRow1, valueRow3));
        assertThat(dark.getChildren()).isNotNull().isNotEmpty().hasSize(1);

        PivotTreeNode darkItaly = dark.getChild("italy");
        assertThat(darkItaly).isNotNull();

        // assert dark-italy label, value, children
        assertThat(darkItaly.getLabel()).isEqualTo("italy");
        assertThat(darkItaly.getValues()).isEqualTo(List.of(valueRow1, valueRow3));
        assertThat(darkItaly.getChildren()).isNotNull().isEmpty();

        // assert blonde label, value, children
        assertThat(blonde.getLabel()).isEqualTo("blonde");
        assertThat(blonde.getValues()).isEqualTo(List.of(valueRow2));
        assertThat(blonde.getChildren()).isNotNull().isNotEmpty().hasSize(1);
        PivotTreeNode blondeItaly = blonde.getChild("italy");
        assertThat(blondeItaly).isNotNull();

        // assert blonde-italy label, value, children
        assertThat(blondeItaly.getLabel()).isEqualTo("italy");
        assertThat(blondeItaly.getValues()).isEqualTo(List.of(valueRow2));
        assertThat(blondeItaly.getChildren()).isEmpty();
    }

    @Test
    void testBuild_throwsExceptionWhenPivotRowsAreNull() {
        // given-when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivotTree.build(null));
    }

    @Test
    void testBuildRecursive() {
        // given
        double valueRow1 = 10.0;
        double valueRow2 = 20.0;
        double valueRow3 = 30.0;

        List<PivotRow> pivotRows = buildRowsWithNaturalOrderSortedLabels(valueRow1, valueRow2, valueRow3);

        // when
        pivotTree.buildRecursive(pivotRows);

        // then
        PivotTreeNode root = pivotTree.getRoot();

        // assert root label, value, children
        assertThat(root).isNotNull();
        assertThat(root.getLabel()).isEqualTo("root");
        assertThat(root.getValues()).isEqualTo(List.of(valueRow1, valueRow2, valueRow3));
        assertThat(root.getChildren()).isNotNull().isNotEmpty().hasSize(1);
        PivotTreeNode brown = root.getChild("brown");
        assertThat(brown).isNotNull();

        // assert brown label, value, children
        assertThat(brown.getLabel()).isEqualTo("brown");
        assertThat(brown.getValues()).isEqualTo(List.of(valueRow1, valueRow2, valueRow3));
        assertThat(brown.getChildren()).isNotNull().isNotEmpty().hasSize(2);
        PivotTreeNode dark = brown.getChild("dark");
        assertThat(dark).isNotNull();
        PivotTreeNode blonde = brown.getChild("blonde");
        assertThat(blonde).isNotNull();

        // assert dark label, value, children
        assertThat(dark.getLabel()).isEqualTo("dark");
        assertThat(dark.getValues()).isEqualTo(List.of(valueRow1, valueRow3));
        assertThat(dark.getChildren()).isNotNull().isNotEmpty().hasSize(1);

        PivotTreeNode darkItaly = dark.getChild("italy");
        assertThat(darkItaly).isNotNull();

        // assert dark-italy label, value, children
        assertThat(darkItaly.getLabel()).isEqualTo("italy");
        assertThat(darkItaly.getValues()).isEqualTo(List.of(valueRow1, valueRow3));
        assertThat(darkItaly.getChildren()).isNotNull().isEmpty();

        // assert blonde label, value, children
        assertThat(blonde.getLabel()).isEqualTo("blonde");
        assertThat(blonde.getValues()).isEqualTo(List.of(valueRow2));
        assertThat(blonde.getChildren()).isNotNull().isNotEmpty().hasSize(1);
        PivotTreeNode blondeItaly = blonde.getChild("italy");
        assertThat(blondeItaly).isNotNull();

        // assert blonde-italy label, value, children
        assertThat(blondeItaly.getLabel()).isEqualTo("italy");
        assertThat(blondeItaly.getValues()).isEqualTo(List.of(valueRow2));
        assertThat(blondeItaly.getChildren()).isEmpty();
    }

    @Test
    void testToString() {
        // given
        double valueRow1 = 10.0;
        double valueRow2 = 20.0;
        double valueRow3 = 30.0;

        List<PivotRow> pivotRows = buildRowsWithNaturalOrderSortedLabels(valueRow1, valueRow2, valueRow3);

        pivotTree.build(pivotRows);

        // when-then
        System.out.println(pivotTree);
    }

    @Test
    void testQuery_returnsZeroWhenQueryLabelsDoNotMatchAnyElement() {
        // given
        double valueRow1 = 10.0;
        double valueRow2 = 20.0;
        double valueRow3 = 30.0;

        List<PivotRow> pivotRows = buildRowsWithNaturalOrderSortedLabels(valueRow1, valueRow2, valueRow3);

        Function<Collection<Double>, Double> sum = PivoterUtils::sum;

        pivotTree.build(pivotRows);

        // when
        List<String> queryLabels1 = List.of("blue");

        Double result1 = pivotTree.query(queryLabels1, sum);

        // then
        assertThat(result1)
                .isNotNull()
                .isEqualTo(0.0);

        // when
        List<String> queryLabels2 = List.of("brown", "dark", "italy", "M");

        Double result2 = pivotTree.query(queryLabels2, sum);

        //then
        assertThat(result2)
                .isNotNull()
                .isEqualTo(0.0);
    }

    @Test
    void testQueryRoot_returnsRootWhenQueryLabelsAreEmpty() {
        // given
        double valueRow1 = 10.0;
        double valueRow2 = 20.0;
        double valueRow3 = 30.0;

        List<PivotRow> pivotRows = buildRowsWithNaturalOrderSortedLabels(valueRow1, valueRow2, valueRow3);

        Function<Collection<Double>, Double> sum = PivoterUtils::sum;

        pivotTree.build(pivotRows);

        // when
        Double result3 = pivotTree.query(Collections.emptyList(), sum);

        //then
        assertThat(result3)
                .isNotNull()
                .isEqualTo(valueRow1 + valueRow2 + valueRow3);
    }

    @Test
    void testQuery_throwsExceptionWhenQueryLabelsAreNull() {
        // given
        double valueRow1 = 10.0;
        double valueRow2 = 20.0;
        double valueRow3 = 30.0;

        List<PivotRow> pivotRows = buildRowsWithNaturalOrderSortedLabels(valueRow1, valueRow2, valueRow3);

        Function<Collection<Double>, Double> sum = PivoterUtils::sum;

        pivotTree.build(pivotRows);

        // when
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivotTree.query(null, sum));
    }

    @Test
    void testQuery_sum() {
        // given
        double valueRow1 = 10.0;
        double valueRow2 = 20.0;
        double valueRow3 = 30.0;

        List<PivotRow> pivotRows = buildRowsWithNaturalOrderSortedLabels(valueRow1, valueRow2, valueRow3);

        Function<Collection<Double>, Double> sum = PivoterUtils::sum;

        pivotTree.build(pivotRows);

        // when
        List<String> queryLabels1 = List.of("brown");

        Double result1 = pivotTree.query(queryLabels1, sum);

        // then
        assertThat(result1)
                .isNotNull()
                .isEqualTo(valueRow1 + valueRow2 + valueRow3);

        // when
        List<String> queryLabels2 = List.of("brown", "dark", "italy");

        Double result2 = pivotTree.query(queryLabels2, sum);

        //then
        assertThat(result2)
                .isNotNull()
                .isEqualTo(valueRow1 + valueRow3);

        // when
        List<String> queryLabels4 = List.of("brown", "blonde", "italy");

        Double result4 = pivotTree.query(queryLabels4, sum);

        //then
        assertThat(result4)
                .isNotNull()
                .isEqualTo(valueRow2);
    }

    @Test
    void testQuery_average() {
        // given
        double valueRow1 = 10.0;
        double valueRow2 = 20.0;
        double valueRow3 = 30.0;

        List<PivotRow> pivotRows = buildRowsWithNaturalOrderSortedLabels(valueRow1, valueRow2, valueRow3);

        Function<Collection<Double>, Double> avg = PivoterUtils::average;

        pivotTree.build(pivotRows);

        // when
        List<String> queryLabels1 = List.of("brown");

        Double result1 = pivotTree.query(queryLabels1, avg);

        //then
        assertThat(result1)
                .isNotNull()
                .isEqualTo((valueRow1 + valueRow2 + valueRow3) / 3);

        // when
        List<String> queryLabels2 = List.of("brown", "dark", "italy");

        Double result2 = pivotTree.query(queryLabels2, avg);

        // then
        assertThat(result2)
                .isNotNull()
                .isEqualTo((valueRow1 + valueRow3) / 2);

        // when
        List<String> queryLabels4 = List.of("brown", "blonde", "italy");

        Double result4 = pivotTree.query(queryLabels4, avg);

        // then
        assertThat(result4)
                .isNotNull()
                .isEqualTo(valueRow2);
    }

    @Test
    void testQuery_mode() {
        // given
        double valueRow1 = 10.0;
        double valueRow2 = 10.0;
        double valueRow3 = 30.0;

        List<PivotRow> pivotRows = buildRowsWithNaturalOrderSortedLabels(valueRow1, valueRow2, valueRow3);

        Function<Collection<Double>, Double> mode = PivoterUtils::mode;

        pivotTree.build(pivotRows);

        // when
        List<String> queryLabels1 = List.of("brown");

        Double result1 = pivotTree.query(queryLabels1, mode);

        // then
        assertThat(result1)
                .isNotNull()
                .isEqualTo(valueRow1);
    }

    private List<PivotRow> buildRowsWithNaturalOrderSortedLabels(double valueRow1, double valueRow2, double valueRow3) {
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

        List<List<String>> labelsRow3 = Arrays.asList(
                List.of("eyes", "brown"),
                List.of("hair", "dark"),
                List.of("nation", "italy")
        );

        PivotRow pivotRow1 = buildRow(labelsRow1, valueRow1);
        PivotRow pivotRow2 = buildRow(labelsRow2, valueRow2);
        PivotRow pivotRow3 = buildRow(labelsRow3, valueRow3);
        return List.of(pivotRow1, pivotRow2, pivotRow3);
    }

    private PivotRow buildRow(List<List<String>> labelsRow, Double value) {
        List<String> labels = new ArrayList<>();

        for (List<String> label : labelsRow) {
            labels.add(label.get(1));
        }

        return new PivotRow(labels, value);
    }
}
