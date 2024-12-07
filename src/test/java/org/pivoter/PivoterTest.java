package org.pivoter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class PivoterTest {

    Pivoter pivoter = new Pivoter();

    @Test
    void testBuildPivotRows_throwsIfDataRowsIsNull() {
        // given-when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.buildPivotRows(null))
                .withMessage("Input data rows cannot be null or empty. Ensure that you provide a list of data rows.");
    }

    @Test
    void testBuildPivotRows_throwsIfDataRowsIsEmpty() {
        // given-when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.buildPivotRows(Collections.emptyList()))
                .withMessage("Input data rows cannot be null or empty. Ensure that you provide a list of data rows.");
    }

    @Test
    void testBuildPivotRows_throwsIfValueLabelIsNotANumber() {
        // given
        Double dataRowValue1 = 10.0;
        String dataRowValue2 = "M";
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue2),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
        // when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.buildPivotRows(dataRows))
                .withMessage("Invalid numerical value for label '#': '" + dataRowValue2 + "'. The value must be a valid Double.");
    }

    @Test
    void testBuildPivotRows_throwsIfLabelsSizeIsNotConstant() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "brown", "hair", "dark", "#", dataRowValue2.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
        // when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.buildPivotRows(dataRows))
                .withMessageContaining("Inconsistent number of labels in the data row. Expected 4 labels, but found 3:");
    }

    @Test
    void testBuildPivotRows_throwsIfValueLabelIsMissing() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "sex", "M"),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
        // when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.buildPivotRows(dataRows))
                .withMessage("Each data row must contain a label '#' for the numerical value.");
    }

    @Test
    void testBuildPivotRows_throwsIfEmptyOrBlankLabel() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "brown", "hair", "dark", "", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
        // when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.buildPivotRows(dataRows))
                .withMessageContaining("Data row contains empty or blank labels:");
    }

    @Test
    void testBuildPivotRows_throwsIfInconsistentLabels() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "brown", "sex", "dark", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
        // when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.buildPivotRows(dataRows))
                .withMessageContaining("does not match the consistent set of labels:");
    }

    @Test
    void testBuildPivotRows_buildsPivotRowsWithNaturalOrderSortedLabels() {
        // given
        double dataRowValue1 = 10.0;
        double dataRowValue2 = 20.0;
        double dataRowValue3 = 30.0;
        List<Map<String, String>> dataRows = buildValidDataWithNaturalOrderSortedLabels(dataRowValue1, dataRowValue2, dataRowValue3);

        // when
        List<PivotRow> pivotRows = pivoter.buildPivotRows(dataRows);

        // then
        assertThat(pivotRows).isNotNull();
        assertThat(pivotRows.size()).isZero();
    }

    @Test
    void testPivot_buildsPivotTreeWithNaturalOrderSortedLabels() {
        // given
        double dataRowValue1 = 10.0;
        double dataRowValue2 = 20.0;
        double dataRowValue3 = 30.0;
        List<Map<String, String>> dataRows = buildValidDataWithNaturalOrderSortedLabels(dataRowValue1, dataRowValue2, dataRowValue3);

        // when
        PivotTree pivotTree = pivoter.pivot(dataRows);

        // then
        assertThat(pivotTree).isNotNull();
        assertThat(pivotTree.getRoot()).isNotNull();
        assertThat(pivotTree.getRoot().getChildren()).isNotNull();
        assertThat(pivotTree.getRoot().getChildren().size()).isZero();
        assertThat(pivotTree.getRoot().getValues()).isNotNull();
        assertThat(pivotTree.getRoot().getValues().size()).isNotNull().isZero();
    }

    private List<Map<String, String>> buildValidDataWithNaturalOrderSortedLabels(Double dataRowValue1, Double dataRowValue2, Double dataRowValue3) {
        return Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
    }
}
