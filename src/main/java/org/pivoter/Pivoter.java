package org.pivoter;

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
        return pivotTree;
    }

    /**
     * Builds a pivot tree using the specified order for pivot labels.
     *
     * @param data       a list of data rows where each row is represented as a map of label-value pairs.
     * @param pivotOrder a comparator to specify the order of pivot labels.
     * @return the constructed pivot tree.
     * @throws IllegalArgumentException if the input data rows are invalid.
     */
    public PivotTree pivot(List<Map<String, String>> data,
                           Comparator<String> pivotOrder) {
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

    List<PivotRow> buildPivotRows(List<Map<String, String>> dataRows) {
        validateDataRows(dataRows);
        // build pivot rows
        return Collections.emptyList();
    }

    List<PivotRow> buildPivotRows(List<Map<String, String>> dataRows,
                                  Comparator<String> pivotOrder) {
        return Collections.emptyList();
    }

    private void validateDataRows(List<Map<String, String>> dataRows) {
        if (dataRows == null || dataRows.isEmpty()) {
            throw new IllegalArgumentException("Input data rows cannot be null or empty. Ensure that you provide a list of data rows.");
        }

        Set<String> labels = dataRows.get(0).keySet();
        int labelsSize = labels.size();

        for (Map<String, String> dataRow : dataRows) {
            validateDataRow(dataRow, labelsSize);
            validateDataRowLabels(dataRow, labels);
        }
    }

    private void validateDataRow(Map<String, String> dataRow, int labelsSize) {
        if (labelsSize != dataRow.keySet().size()) {
            throw new IllegalArgumentException(String.format(
                    "Inconsistent number of labels in the data row. Expected %d labels, but found %d: %s",
                    labelsSize, dataRow.keySet().size(), dataRow));
        }
        if (!dataRow.containsKey("#")) {
            throw new IllegalArgumentException("Each data row must contain a label '#' for the numerical value.");
        }
        if (!isDouble(dataRow.get("#"))) {
            throw new IllegalArgumentException(String.format(
                    "Invalid numerical value for label '#': '%s'. The value must be a valid Double.", dataRow.get("#")));
        }
    }

    private void validateDataRowLabels(Map<String, String> dataRow, Set<String> labels) {
        for (String label : dataRow.keySet()) {
            if (label.isEmpty() || label.isBlank()) {
                throw new IllegalArgumentException(String.format(
                        "Data row contains empty or blank labels: %s. Labels must be non-empty strings.", dataRow));
            }
            if (dataRow.get(label) == null) {
                throw new IllegalArgumentException(String.format(
                        "Data row %s contains a null value for label '%s'. All labels must have non-null values.", dataRow, label));
            }
            if (!labels.contains(label)) {
                throw new IllegalArgumentException(String.format(
                        "Label '%s' in data row %s does not match the consistent set of labels: %s", label, dataRow, labels));
            }
        }
    }

    private boolean isDouble(String str) {
        if (str == null || str.isEmpty()) {
            return false; // Null or empty strings are not valid Doubles
        }
        try {
            Double.parseDouble(str);
            return true; // Parsing successful
        } catch (NumberFormatException e) {
            return false; // Parsing failed
        }
    }
}
