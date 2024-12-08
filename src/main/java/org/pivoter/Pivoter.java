package org.pivoter;

import org.pivoter.utils.PivoterUtils;

import java.util.*;
import java.util.function.Function;

public class Pivoter {

    private final PivotTree pivotTree;
    private final Comparator<String> pivotOrder;

    public Pivoter() {
        this.pivotTree = new PivotTree();
        this.pivotOrder = Comparator.naturalOrder();
    }

    public Comparator<String> getPivotOrder() {
        return pivotOrder;
    }

    /**
     * Builds a pivot tree from the provided data rows.
     *
     * @param dataRows a list of data rows where each row is represented as a map of label-value pairs.
     * @return the constructed pivot tree.
     * @throws IllegalArgumentException if the input data rows are invalid.
     */
    public PivotTree pivot(List<Map<String, String>> dataRows) {
        // build pivot rows from data rows
        // validate data rows
        // convert data rows in pivot rows
        // build pivot tree from pivot rows
        return pivotTree;
    }

    /**
     * Builds a pivot tree using the specified order for pivot labels.
     *
     * @param dataRows       a list of data rows where each row is represented as a map of label-value pairs.
     * @param pivotOrder a comparator to specify the order of pivot labels.
     * @return the constructed pivot tree.
     * @throws IllegalArgumentException if the input data rows are invalid.
     */
    public PivotTree pivot(List<Map<String, String>> dataRows,
                           Comparator<String> pivotOrder) {
        // validate pivot order against data rows
        // validate data rows
        // convert data rows in pivot rows w/ pivot order
        // build pivot tree from pivot rows
        return pivotTree;
    }

    /**
     * Queries the pivot tree with the provided labels and function.
     *
     * @param queryLabels   the labels to query the pivot tree.
     * @param pivotFunction the function to apply on the queried data.
     * @return the result of the query.
     */
    public Double query(List<String> queryLabels,
                        Function<Collection<Double>, Double> pivotFunction) {
        return null;
    }

    void validate(List<Map<String, String>> dataRows) {
        if (dataRows == null || dataRows.isEmpty()) {
            throw new IllegalArgumentException("Input data rows cannot be null or empty. Ensure that you provide a list of data rows.");
        }

        List<String> labels = new ArrayList<>(dataRows.get(0).keySet());
        int labelsSize = labels.size();

        for (Map<String, String> dataRow : dataRows) {
            validateDataRow(dataRow, labelsSize);

            for (String label : dataRow.keySet()) {
                validateDataRowLabel(label, labels, dataRow);
            }
        }
    }

    List<PivotRow> convert(List<Map<String, String>> dataRows) {
        List<PivotRow> pivotRows = new ArrayList<>();

        for (Map<String, String> dataRow : dataRows) {
            List<String> sortedLabels = dataRow.keySet().stream().sorted().toList();

            PivotRow pivotRow = new PivotRow();
            for (String label : sortedLabels) {
                String labelValue = dataRow.get(label);
                if ("#".equals(label)) {
                    pivotRow.setValue(Double.parseDouble(labelValue));
                } else {
                    pivotRow.addLabel(labelValue);
                }
            }
            pivotRows.add(pivotRow);
        }
        return pivotRows;
    }

    // does not adhere to SRP, but it is more efficient,
    // having O(n * log m * m) rather than 2 * (O(n * log m * m)) complexity,
    // where n = #rows, m = #labels
    @Deprecated
    private List<PivotRow> validateAndConvert(List<Map<String, String>> dataRows) {
        if (dataRows == null || dataRows.isEmpty()) {
            throw new IllegalArgumentException("Input data rows cannot be null or empty. Ensure that you provide a list of data rows.");
        }

        List<String> labels = new ArrayList<>(dataRows.get(0).keySet());
        int labelsSize = labels.size();

        List<PivotRow> pivotRows = new ArrayList<>();

        for (Map<String, String> dataRow : dataRows) {

            validateDataRow(dataRow, labelsSize);

            List<String> sortedLabels = dataRow.keySet().stream().sorted().toList();

            PivotRow pivotRow = new PivotRow();

            for (String label : sortedLabels) {

                validateDataRowLabel(label, labels, dataRow);

                String labelValue = dataRow.get(label);
                if ("#".equals(label))
                    pivotRow.setValue(Double.parseDouble(labelValue));
                else
                    pivotRow.addLabel(labelValue);
            }

            pivotRows.add(pivotRow);
        }
        return pivotRows;
    }

    private void validateDataRow(Map<String, String> dataRow, int labelsSize) { // map prevents duplicated labels

        if (labelsSize != dataRow.keySet().size()) {
            throw new IllegalArgumentException(String.format(
                    "Inconsistent number of labels in the data row. Expected %d labels, but found %d: %s",
                    labelsSize, dataRow.keySet().size(), dataRow));
        }
        if (!dataRow.containsKey("#")) {
            throw new IllegalArgumentException("Each data row must contain a label '#' for the numerical value.");
        }
        if (!PivoterUtils.isDouble(dataRow.get("#"))) {
            throw new IllegalArgumentException(String.format(
                    "Invalid numerical value for label '#': '%s'. The value must be a valid Double.", dataRow.get("#")));
        }
    }

    private void validateDataRowLabel(String label, List<String> labels, Map<String, String> dataRow) {
        if (label.isEmpty() || label.isBlank()) {
            throw new IllegalArgumentException(String.format(
                    "Data row contains empty or blank labels: %s. Labels must be non-empty strings.", dataRow));
        }
        if (dataRow.get(label) == null) {
            throw new IllegalArgumentException(String.format(
                    "Data row %s contains a null value for label '%s'. All labels must have non-null values.", dataRow, label));
        }
        if (!"#".equals(label) && !labels.contains(label)) {
            throw new IllegalArgumentException(String.format(
                    "Label '%s' in data row %s does not match the consistent set of labels: %s", label, dataRow, labels));
        }
    }
}
