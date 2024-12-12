package org.pivoter;

import org.junit.jupiter.api.Test;
import org.pivoter.utils.PivoterUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PivoterTest {

    Pivoter pivoter = new Pivoter();

    @Test
    void testValidateDataRows_throwsIfDataRowsIsNull() {
        // given-when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.validateDataRows(null))
                .withMessage("dataRows cannot be null or empty. Ensure that you provide a valid list of dataRows.");
    }

    @Test
    void testValidateDataRows_throwsIfDataRowsIsEmpty() {
        // given-when-then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.validateDataRows(Collections.emptyList()))
                .withMessage("dataRows cannot be null or empty. Ensure that you provide a valid list of dataRows.");
    }

    @Test
    void testValidateDataRows_throwsIfLabelsSizeIsNotFixed() {
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
                .isThrownBy(() -> pivoter.validateDataRows(dataRows))
                .withMessageContaining("Inconsistent number of labels in dataRow. Expected 4 labels, but found 3:");
    }

    @Test
    void testValidateDataRows_throwsIfValueLabelIsNotANumber() {
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
                .isThrownBy(() -> pivoter.validateDataRows(dataRows))
                .withMessage("Invalid numerical value for label '#': '" + dataRowValue1 + "'. The value must be a valid Double.");
    }

    @Test
    void testValidateDataRows_throwsIfHashLabelIsMissing() {
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
                .isThrownBy(() -> pivoter.validateDataRows(dataRows))
                .withMessage("Each dataRow must contain a label '#' for the numerical value.");
    }

    @Test
    void testValidateDataRows_throwsIfEmptyOrBlankLabel() {
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
                .isThrownBy(() -> pivoter.validateDataRows(dataRows))
                .withMessageContaining("dataRow contains empty or blank labels:");
    }

    @Test
    void testValidateDataRows_throwsIfNullLabelValue() {
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
                .isThrownBy(() -> pivoter.validateDataRows(dataRows))
                .withMessageContaining("All labels must have non-null values.");
    }

    @Test
    void testValidateDataRows_throwsIfInconsistentLabels() {
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
                .isThrownBy(() -> pivoter.validateDataRows(dataRows))
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
    void testPivot_throwsIfPivotHierarchyNotConsistentWithDataRows() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows1 = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        List<String> pivotHierarchy1 = List.of("height", "eyes", "nation");

        // when
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.pivot(dataRows1, pivotHierarchy1))
                .withMessageContaining("not consistent with the provided dataRow.");

        // given
        List<String> pivotHierarchy2 = List.of("hair", "eyes", "nation", "height");

        // when
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.pivot(dataRows1, pivotHierarchy2))
                .withMessageContaining("not consistent with the provided dataRow.");
    }

    @Test
    void testPivot_throwsIfPivotHierarchyContainsHashLabel() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        List<String> pivotHierarchy = List.of("hair", "eyes", "#");

        // when
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.pivot(dataRows, pivotHierarchy))
                .withMessageContaining("pivotHierarchy label '#' is not valid.");
    }

    @Test
    void testPivot_throwsIfDuplicatedElementInPivotHierarchy() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        List<String> pivotHierarchy = List.of("hair", "eyes", "hair");

        // when
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.pivot(dataRows, pivotHierarchy))
                .withMessageContaining("pivotHierarchy cannot contain duplicates");
    }

    @Test
    void testPivotAndQuery_sumWithCustomHierarchy() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        List<String> pivotHierarchy = List.of("hair", "eyes", "nation");

        // when
        pivoter.pivot(dataRows, pivotHierarchy);

        String queryLabel1 = "dark";
        List<String> queryLabels1 = List.of(queryLabel1);

        Double result1 = pivoter.query(queryLabels1, PivoterUtils::sum);

        // then
        assertThat(result1).isNotNull().isEqualTo((dataRowValue1 + dataRowValue3));
    }

    @Test
    void testPivotAndQuery_averageWithNaturalOrderHierarchy() {
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

        String queryLabel1 = "blue";
        List<String> queryLabels = List.of(queryLabel1);

        Double result = pivoter.query(queryLabels, PivoterUtils::average);

        // then
        assertThat(result).isNotNull().isEqualTo((dataRowValue2 + dataRowValue3) / 2);
    }

    @Test
    void testPivotAndQuery_averageWithCustomHierarchy() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        List<String> pivotHierarchy = List.of("nation", "hair", "eyes");

        // when
        pivoter.pivot(dataRows, pivotHierarchy);

        String queryLabel1 = "italy";
        List<String> queryLabels = List.of(queryLabel1);

        Double result2 = pivoter.query(queryLabels, PivoterUtils::average);

        // then
        assertThat(result2).isNotNull().isEqualTo((dataRowValue1 + dataRowValue2 + dataRowValue3) / 3);
    }

    @Test
    void testPivotAndQuery_throwsIfQueryLabelsAreNull() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        List<String> pivotHierarchy = List.of("hair", "eyes", "nation");

        // when
        pivoter.pivot(dataRows, pivotHierarchy);


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> pivoter.query(null, PivoterUtils::sum))
                .withMessageContaining("queryLabels cannot be null.");
    }

    @Test
    void testPivotTwiceAndQuery_pivotHierarchyIsReset() {
        // given
        Double dataRowValue1 = 10.0;
        Double dataRowValue2 = 20.0;
        Double dataRowValue3 = 30.0;

        List<Map<String, String>> dataRows = Arrays.asList(
                Map.of("eyes", "brown", "hair", "dark", "nation", "italy", "#", dataRowValue1.toString()),
                Map.of("eyes", "blue", "hair", "blonde", "nation", "italy", "#", dataRowValue2.toString()),
                Map.of("eyes", "blue", "hair", "dark", "nation", "italy", "#", dataRowValue3.toString())
        );

        List<String> pivotHierarchy = List.of("nation", "hair", "eyes");

        // when
        pivoter.pivot(dataRows, pivotHierarchy);

        String queryLabel1 = "italy";
        List<String> queryLabels = List.of(queryLabel1);

        Double result = pivoter.query(queryLabels, PivoterUtils::average);

        // then
        assertThat(result).isNotNull().isEqualTo((dataRowValue1 + dataRowValue2 + dataRowValue3) / 3);

        // when
        pivoter.pivot(dataRows);

        String queryLabel2 = "blue";
        List<String> queryLabels2 = List.of(queryLabel2);

        Double result2 = pivoter.query(queryLabels2, PivoterUtils::average);

        // then
        assertThat(result2).isNotNull().isEqualTo((dataRowValue2 + dataRowValue3) / 2);
    }
}
