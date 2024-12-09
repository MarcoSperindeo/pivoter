package org.pivoter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.pivoter.utils.PivoterUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PivoterTest {

    Pivoter pivoter = new Pivoter();

    @Test
    void testValidate_throwsIfDataRowsIsNull() {
        // given-when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.validate(null))
                .withMessage("Input data rows cannot be null or empty. Ensure that you provide a list of data rows.");
    }

    @Test
    void testValidate_throwsIfDataRowsIsEmpty() {
        // given-when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.validate(Collections.emptyList()))
                .withMessage("Input data rows cannot be null or empty. Ensure that you provide a list of data rows.");
    }

    @Test
    void testValidate_throwsIfLabelsSizeIsNotFixed() {
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
                .isThrownBy(() -> pivoter.validate(dataRows))
                .withMessageContaining("Inconsistent number of labels in the data row. Expected 4 labels, but found 3:");
    }

    @Test
    void testValidate_throwsIfValueLabelIsNotANumber() {
        // given
        String dataRowValue1 = "M";
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
        // when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.validate(dataRows))
                .withMessage("Invalid numerical value for label '#': '" + dataRowValue1 + "'. The value must be a valid Double.");
    }

    @Test
    void testValidate_throwsIfHashLabelIsMissing() {
        // given
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "sex", "M"),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
        // when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.validate(dataRows))
                .withMessage("Each data row must contain a label '#' for the numerical value.");
    }

    @Test
    void testValidate_throwsIfEmptyOrBlankLabel() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
        // when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.validate(dataRows))
                .withMessageContaining("Data row contains empty or blank labels:");
    }

    @Test
    void testValidate_throwsIfNullLabelValue() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        HashMap<String, String> dataRow1 = new HashMap<>();
        dataRow1.put("eyes", "brown");
        dataRow1.put("hair", "dark");
        dataRow1.put("nation", null);
        dataRow1.put("#", dataRowValue1.toString());

        List<Map<String, String>> dataRows = Arrays.asList(
                dataRow1,
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );
        // when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.validate(dataRows))
                .withMessageContaining("All labels must have non-null values.");
    }

    @Test
    void testValidate_throwsIfInconsistentLabels() {
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
                .isThrownBy(() -> pivoter.validate(dataRows))
                .withMessageContaining("does not match the consistent set of labels:");
    }

    @Test
    void testConvert_convertsToSortedPivotRowsWithSortedLabels() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        // when
        List<PivotRow> pivotRows = pivoter.convert(dataRows);

        // then
        assertThat(pivotRows).isNotNull().hasSize(3);

        assertThat(pivotRows.get(0)).isNotNull();
        assertThat(pivotRows.get(0).getValue()).isNotNull().isEqualTo(dataRowValue1);
        assertThat(pivotRows.get(0).getLabels()).isNotNull().hasSize(3);

        assertThat(pivotRows.get(0).getLabels().get(0)).isNotNull().isEqualTo("brown");
        assertThat(pivotRows.get(0).getValue()).isNotNull().isEqualTo(dataRowValue1);
        assertThat(pivotRows.get(0).getLabels().get(1)).isNotNull().isEqualTo("dark");
        assertThat(pivotRows.get(0).getValue()).isNotNull().isEqualTo(dataRowValue1);
        assertThat(pivotRows.get(0).getLabels().get(2)).isNotNull().isEqualTo("italy");
        assertThat(pivotRows.get(0).getValue()).isNotNull().isEqualTo(dataRowValue1);

        assertThat(pivotRows.get(1)).isNotNull();
        assertThat(pivotRows.get(1).getValue()).isNotNull().isEqualTo(dataRowValue2);
        assertThat(pivotRows.get(1).getLabels()).isNotNull().hasSize(3);

        assertThat(pivotRows.get(1).getLabels().get(0)).isNotNull().isEqualTo("brown");
        assertThat(pivotRows.get(1).getValue()).isNotNull().isEqualTo(dataRowValue2);
        assertThat(pivotRows.get(1).getLabels().get(1)).isNotNull().isEqualTo("dark");
        assertThat(pivotRows.get(1).getValue()).isNotNull().isEqualTo(dataRowValue2);
        assertThat(pivotRows.get(1).getLabels().get(2)).isNotNull().isEqualTo("italy");
        assertThat(pivotRows.get(1).getValue()).isNotNull().isEqualTo(dataRowValue2);

        assertThat(pivotRows.get(2)).isNotNull();
        assertThat(pivotRows.get(2).getValue()).isNotNull().isEqualTo(dataRowValue3);
        assertThat(pivotRows.get(2).getLabels()).isNotNull().hasSize(3);

        assertThat(pivotRows.get(2).getLabels().get(0)).isNotNull().isEqualTo("brown");
        assertThat(pivotRows.get(2).getValue()).isNotNull().isEqualTo(dataRowValue3);
        assertThat(pivotRows.get(2).getLabels().get(1)).isNotNull().isEqualTo("dark");
        assertThat(pivotRows.get(2).getValue()).isNotNull().isEqualTo(dataRowValue3);
        assertThat(pivotRows.get(2).getLabels().get(2)).isNotNull().isEqualTo("italy");
        assertThat(pivotRows.get(2).getValue()).isNotNull().isEqualTo(dataRowValue3);
    }

    @Test
    void testConvert_convertsToSortedPivotRowsWithNonSortedLabels() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("hair", "dark", "eyes", "brown", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("hair", "dark", "eyes", "brown", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("hair", "dark", "eyes", "brown", "nation", "italy", "#", dataRowValue3.toString())
        );

        // when
        List<PivotRow> pivotRows = pivoter.convert(dataRows);

        // then
        assertThat(pivotRows).isNotNull().hasSize(3);

        assertThat(pivotRows.get(0)).isNotNull();
        assertThat(pivotRows.get(0).getValue()).isNotNull().isEqualTo(dataRowValue1);
        assertThat(pivotRows.get(0).getLabels()).isNotNull().hasSize(3);

        assertThat(pivotRows.get(0).getLabels().get(0)).isNotNull().isEqualTo("brown");
        assertThat(pivotRows.get(0).getValue()).isNotNull().isEqualTo(dataRowValue1);
        assertThat(pivotRows.get(0).getLabels().get(1)).isNotNull().isEqualTo("dark");
        assertThat(pivotRows.get(0).getValue()).isNotNull().isEqualTo(dataRowValue1);
        assertThat(pivotRows.get(0).getLabels().get(2)).isNotNull().isEqualTo("italy");
        assertThat(pivotRows.get(0).getValue()).isNotNull().isEqualTo(dataRowValue1);

        assertThat(pivotRows.get(1)).isNotNull();
        assertThat(pivotRows.get(1).getValue()).isNotNull().isEqualTo(dataRowValue2);
        assertThat(pivotRows.get(1).getLabels()).isNotNull().hasSize(3);

        assertThat(pivotRows.get(1).getLabels().get(0)).isNotNull().isEqualTo("brown");
        assertThat(pivotRows.get(1).getValue()).isNotNull().isEqualTo(dataRowValue2);
        assertThat(pivotRows.get(1).getLabels().get(1)).isNotNull().isEqualTo("dark");
        assertThat(pivotRows.get(1).getValue()).isNotNull().isEqualTo(dataRowValue2);
        assertThat(pivotRows.get(1).getLabels().get(2)).isNotNull().isEqualTo("italy");
        assertThat(pivotRows.get(1).getValue()).isNotNull().isEqualTo(dataRowValue2);

        assertThat(pivotRows.get(2)).isNotNull();
        assertThat(pivotRows.get(2).getValue()).isNotNull().isEqualTo(dataRowValue3);
        assertThat(pivotRows.get(2).getLabels()).isNotNull().hasSize(3);

        assertThat(pivotRows.get(2).getLabels().get(0)).isNotNull().isEqualTo("brown");
        assertThat(pivotRows.get(2).getValue()).isNotNull().isEqualTo(dataRowValue3);
        assertThat(pivotRows.get(2).getLabels().get(1)).isNotNull().isEqualTo("dark");
        assertThat(pivotRows.get(2).getValue()).isNotNull().isEqualTo(dataRowValue3);
        assertThat(pivotRows.get(2).getLabels().get(2)).isNotNull().isEqualTo("italy");
        assertThat(pivotRows.get(2).getValue()).isNotNull().isEqualTo(dataRowValue3);
    }

    @Test
    void testPivot_averageWithNaturalOrderHierarchy() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        // when
        pivoter.pivot(dataRows);

        // then
        String queryLabel1 = "blue";
        List<String> queryLabels = List.of(queryLabel1);
        Double result = pivoter.query(queryLabels, PivoterUtils::average);

        assertThat(result).isNotNull().isEqualTo((dataRowValue2 + dataRowValue3)/2);
    }

    @Test
    void validatePivotHierarchy_throwsIfNotValidAgainstDataRows() {
    }

    @Test
    void validatePivotHierarchy_throwsIfContainsHash() {
    }

    @Test
    void validatePivotHierarchy_throwsIfDuplicatedElement() {
    }

    @Test
    void testPivot_averageWithCustomHierarchyA() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows1 = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        List<String> pivotHierarchy1 = List.of("hair", "eyes", "nation");

        // when
        pivoter.pivot(dataRows1, pivotHierarchy1);

        System.out.println(pivoter.getPivotTree());

        // then
        String queryLabel11 = "dark";
        List<String> queryLabels1 = List.of(queryLabel11);
        Double result1 = pivoter.query(queryLabels1, PivoterUtils::average);

        assertThat(result1).isNotNull().isEqualTo((dataRowValue1 + dataRowValue3)/2);
    }

    @Test
    void testPivot_averageWithCustomHierarchyB() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows2 = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        List<String> pivotHierarchy2 = List.of("nation", "hair", "eyes");

        // when
        pivoter.pivot(dataRows2, pivotHierarchy2);

        System.out.println(pivoter.getPivotTree());

        // then
        String queryLabel12 = "italy";
        List<String> queryLabels2 = List.of(queryLabel12);
        Double result2 = pivoter.query(queryLabels2, PivoterUtils::average);

        assertThat(result2).isNotNull().isEqualTo((dataRowValue1 + dataRowValue2 + dataRowValue3)/3);
    }
}
