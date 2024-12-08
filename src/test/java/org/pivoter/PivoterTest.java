package org.pivoter;

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
    void testValidate_throwsIfValueLabelIsMissing() {
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
    void testConvert_convertsToPivotRowsWithSortedLabels() {
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
    void testConvert_convertsSortedPivotRowsWithNonSortedLabels() {
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
    void testPivot_average() {
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
        System.out.println(pivoter.getPivotTree());

        // when
        String queryLabel1 = "blue";
        List<String> queryLabels = new ArrayList<>(List.of(queryLabel1));
        Double result = pivoter.query(queryLabels, PivoterUtils::average);

        // then
        System.out.println(queryLabels + " average: " + result);
    }
}
