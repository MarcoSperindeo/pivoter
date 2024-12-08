package org.pivoter;

import org.pivoter.annotations.NotForUse;
import org.pivoter.utils.PivoterUtils;

import java.util.*;
import java.util.function.Function;

public class Pivoter {

    private final PivotTree pivotTree;
    private Comparator<String> pivotHierarchy;

    public Pivoter() {
        this.pivotTree = new PivotTree();
        this.pivotHierarchy = Comparator.naturalOrder();
    }

    public PivotTree getPivotTree() {
        return pivotTree;
    }

    public Comparator<String> getPivotHierarchy() {
        return pivotHierarchy;
    }

    public void setPivotHierarchy(Comparator<String> pivotHierarchy) {
        this.pivotHierarchy = pivotHierarchy;
    }

    /**
     * Builds a pivot tree from the provided data rows using a natural order hierarchy.
     *
     * @param dataRows a list of data rows where each row is represented as a map of label-value pairs.
     * @return the resulting pivot tree.
     * @throws IllegalArgumentException if the input data rows are invalid.
     */
    public void pivot(List<Map<String, String>> dataRows) {
        validate(dataRows);
        pivotTree.build(convert(dataRows));
    }

    /**
     * Builds a pivot tree from the provided data rows using the specified hierarchy.
     *
     * @param dataRows       a list of data rows where each row is represented as a map of label-value pairs.
     * @param pivotHierarchy a list of strings to specify the hierarchy of pivot labels.
     * @return the resulting pivot tree.
     * @throws IllegalArgumentException if the input data rows or pivot hierarchy are invalid.
     */
    public void pivot(List<Map<String, String>> dataRows,
                      List<String> pivotHierarchy) {
        // validate data rows
        validate(dataRows);
        // validate pivot hierarchy against data rows
        validatePivotHierarchy(pivotHierarchy, dataRows.get(0));
        // builds pivot hierarchy comparator
        // set pivot hierarchy
        setPivotHierarchy(PivoterUtils.getHierarchyComparator(pivotHierarchy));
        // convert data rows in pivot rows w/ pivot order
        // build pivot tree from pivot rows
        pivotTree.build(convert(dataRows));

    }

    /**
     * Queries the pivot tree with the provided labels and aggregation function.
     *
     * @param queryLabels   the labels to query the pivot tree.
     * @param pivotFunction the aggregation function to apply on the queried data.
     * @return the result of the query.
     */
    public Double query(List<String> queryLabels,
                        Function<Collection<Double>, Double> pivotFunction) {
        queryLabels.sort(this.pivotHierarchy);
        return pivotTree.query(queryLabels, pivotFunction);
    }

    void validate(List<Map<String, String>> dataRows) {
        if (dataRows == null || dataRows.isEmpty()) {
            throw new IllegalArgumentException("Input data rows cannot be null or empty. Ensure that you provide a list of data rows.");
        }

        List<String> labels = new ArrayList<>(dataRows.get(0).keySet());
        int labelsSize = labels.size();

        for (Map<String, String> dataRow : dataRows) {
            validateDataRow(dataRow, labelsSize);

            for (String label : dataRow.keySet())
                validateDataRowLabel(label, labels, dataRow);
        }
    }

    List<PivotRow> convert(List<Map<String, String>> dataRows) {
        List<PivotRow> pivotRows = new ArrayList<>();

        for (Map<String, String> dataRow : dataRows) {
            List<String> sortedLabels = dataRow.keySet().stream().sorted(this.pivotHierarchy).toList();

            PivotRow pivotRow = new PivotRow();
            for (String label : sortedLabels) {
                String labelValue = dataRow.get(label);

                if ("#".equals(label)) pivotRow.setValue(Double.parseDouble(labelValue));
                else pivotRow.addLabel(labelValue);
            }
            pivotRows.add(pivotRow);
        }
        return pivotRows;
    }

    /**
     * does not adhere to SRP, but is more efficient,
     * having O(n * log(m) * m) complexity rather than 2 * (O(n * log(m) * m)) complexity,
     * where n = #rows, m = #labels
     */
    @NotForUse(reason = "Does not adhere to Single Responsibility Principle")
    private List<PivotRow> validateAndConvert(List<Map<String, String>> dataRows, List<String> pivotHierarchy) {
        if (dataRows == null || dataRows.isEmpty()) {
            throw new IllegalArgumentException("Input data rows cannot be null or empty. Ensure that you provide a list of data rows.");
        }

        List<String> labels = new ArrayList<>(dataRows.get(0).keySet());
        int labelsSize = labels.size();

        List<PivotRow> pivotRows = new ArrayList<>();

        for (Map<String, String> dataRow : dataRows) {
            validateDataRow(dataRow, labelsSize);
            List<String> sortedLabels = dataRow.keySet().stream().sorted(this.pivotHierarchy).toList();

            PivotRow pivotRow = new PivotRow();
            for (String label : sortedLabels) {
                validateDataRowLabel(label, labels, dataRow);

                String labelValue = dataRow.get(label);
                if ("#".equals(label)) pivotRow.setValue(Double.parseDouble(labelValue));
                else pivotRow.addLabel(labelValue);
            }

            pivotRows.add(pivotRow);
        }

        for (String hierarchy : pivotHierarchy)
            if (!dataRows.get(0).containsKey(hierarchy))
                throw new IllegalArgumentException("Pivot hierarchy " + hierarchy + " is not valid against the provided data rows.");

        return pivotRows;
    }

    private void validatePivotHierarchy(List<String> pivotHierarchy, Map<String, String> dataRow) {
        for (String hierarchy : pivotHierarchy)
            if (!dataRow.containsKey(hierarchy))
                throw new IllegalArgumentException("Pivot hierarchy " + hierarchy + " is not valid against the provided data rows.");
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
