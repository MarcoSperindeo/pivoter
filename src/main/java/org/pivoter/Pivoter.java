package org.pivoter;

import org.pivoter.annotations.NotForUse;
import org.pivoter.utils.PivoterUtils;

import java.util.*;
import java.util.function.Function;

/**
 * <p>The {@code Pivoter} class provides functionality to create and query pivot trees
 * from structured data.
 * A pivot tree is a hierarchical representation of data
 * aggregated based on specified labels or hierarchy levels.</p>
 *
 * <p>The {@code Pivoter} class leverages the {@link PivotTree} class to construct
 * and traverse pivot trees. {@link PivotTree} represents the hierarchical data structure
 * and supports recursive querying and aggregation of data values.
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Builds a {@link PivotTree} from input data rows with either a natural order
 *       or a custom hierarchy.</li>
 *   <li>Validates input data rows to ensure consistency and correctness.</li>
 *   <li>Supports querying of aggregated values within the pivot tree using custom
 *       aggregation functions.</li>
 *   <li>Allows customization of hierarchy levels via a user-defined comparator.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Pivoter pivoter = new Pivoter();
 *
 * // Sample data rows
 * List<Map<String, String>> dataRows = List.of(
 *     Map.of("Region", "North", "Product", "A", "#", "100"),
 *     Map.of("Region", "South", "Product", "A", "#", "200"),
 *     Map.of("Region", "North", "Product", "B", "#", "150")
 * );
 *
 * // Build the pivot tree with a natural order hierarchy
 * pivoter.pivot(dataRows);
 *
 * // Query aggregated values
 * List<String> queryLabels = List.of("Region", "North");
 * Double totalValue = pivoter.query(queryLabels, values -> values.stream().mapToDouble(Double::doubleValue).sum());
 *
 * System.out.println("Total value for Region 'North': " + totalValue);
 * }</pre>
 *
 * <h2>Relationship with {@code PivotTree}</h2>
 * {@code Pivoter} serves as a higher-level API for constructing and interacting with
 * {@link PivotTree}. While {@code Pivoter} focuses on data transformation, validation,
 * and hierarchy management, {@link PivotTree} handles the underlying tree structure
 * and aggregation logic.
 *
 * <h2>Performance</h2>
 * <ul>
 *   <li>Tree building: O(m * n) complexity, where n is the number of data rows and
 *       m is the number of labels per row.</li>
 *   <li>Query execution: O(m * log(n)) complexity for traversing the tree and applying
 *       the aggregation functions.</li>
 * </ul>
 *
 * @see PivotTree
 * @see PivotTreeNode
 */
public class Pivoter {

    private PivotTree pivotTree;
    private Comparator<String> pivotHierarchyComparator;

    public Pivoter() {
        this.pivotTree = new PivotTree();
        this.pivotHierarchyComparator = Comparator.naturalOrder();
    }

    public PivotTree getPivotTree() {
        return pivotTree;
    }

    public void setPivotHierarchyComparator(Comparator<String> pivotHierarchyComparator) {
        this.pivotHierarchyComparator = pivotHierarchyComparator;
    }

    /**
     * Builds a pivot tree from the provided data rows using a natural order hierarchy.
     *
     * @param dataRows a list of data rows where each row is represented as a map of label-value pairs.
     * @return the resulting pivot tree.
     * @throws IllegalArgumentException if the input data rows are invalid.
     */
    public void pivot(List<Map<String, String>> dataRows) {
        this.pivotTree = new PivotTree();
        this.pivotHierarchyComparator = Comparator.naturalOrder();

        validateDataRows(dataRows);
        pivotTree.build(convert(dataRows)); // O(m * n) complexity, where n = #rows, m = #labels
    }

    /**
     * Builds a pivot tree from the provided data rows using the specified hierarchy.
     *
     * @param dataRows       a list of data rows where each row is represented as a map of label-value pairs.
     * @param pivotHierarchy a set of strings to specify the hierarchy of pivot labels.
     * @return the resulting pivot tree.
     * @throws IllegalArgumentException if the input data rows or pivot hierarchy are invalid.
     */
    public void pivot(List<Map<String, String>> dataRows,
                      List<String> pivotHierarchy) {
        this.pivotTree = new PivotTree();

        validateDataRows(dataRows);
        validatePivotHierarchy(pivotHierarchy, dataRows.get(0));
        setPivotHierarchyComparator(getHierarchyComparator(new ArrayList<>(pivotHierarchy)));
        pivotTree.build(convert(dataRows)); // O(m * n) complexity, where n = #rows, m = #labels
    }

    /**
     * Queries the pivot tree with the provided labels and aggregation function.
     *
     * @param queryLabels   the labels to query the pivot tree.
     * @param pivotFunction the aggregation function to apply on the queried data.
     * @return the result of the query.
     * @throws IllegalArgumentException if the input query labels are null.
     */
    public Double query(List<String> queryLabels,
                        Function<Collection<Double>, Double> pivotFunction) {
        validateQueryLabels(queryLabels);
        List<String> deepQueryLabels = new ArrayList<>(queryLabels);
        deepQueryLabels.sort(this.pivotHierarchyComparator);
        return pivotTree.query(deepQueryLabels, pivotFunction); // O(m * n) complexity, where n = #rows, m = #labels
    }

    void validateDataRows(List<Map<String, String>> dataRows) {
        if (dataRows == null || dataRows.isEmpty())
            throw new IllegalArgumentException("dataRows cannot be null or empty. Ensure that you provide a valid list of dataRows.");

        List<String> labels = new ArrayList<>(dataRows.get(0).keySet());
        int labelsSize = labels.size();

        for (Map<String, String> dataRow : dataRows) {
            validateDataRow(dataRow, labelsSize);

            for (String label : dataRow.keySet())
                validateDataRowLabel(label, labels, dataRow);
        }
    }

    private void validateDataRow(Map<String, String> dataRow, int labelsSize) { // map prevents duplicated labels
        if (labelsSize != dataRow.keySet().size())
            throw new IllegalArgumentException(String.format(
                    "Inconsistent number of labels in dataRow. Expected %d labels, but found %d: %s",
                    labelsSize, dataRow.keySet().size(), dataRow));

        if (!dataRow.containsKey("#"))
            throw new IllegalArgumentException("Each dataRow must contain a label '#' for the numerical value.");

        if (!PivoterUtils.isDouble(dataRow.get("#")))
            throw new IllegalArgumentException(String.format(
                    "Invalid numerical value for label '#': '%s'. The value must be a valid Double.", dataRow.get("#")));
    }

    private void validateDataRowLabel(String label, List<String> labels, Map<String, String> dataRow) {
        if (label.isEmpty() || label.isBlank())
            throw new IllegalArgumentException(String.format(
                    "dataRow contains empty or blank labels: %s. Labels must be non-empty strings.", dataRow));

        if (dataRow.get(label) == null)
            throw new IllegalArgumentException(String.format(
                    "dataRow %s contains a null value for label '%s'. All labels must have non-null values.", dataRow, label));

        if (!"#".equals(label) && !labels.contains(label))
            throw new IllegalArgumentException(String.format(
                    "Label '%s' in dataRow %s does not match the consistent set of labels: %s", label, dataRow, labels));
    }

    private void validatePivotHierarchy(List<String> pivotHierarchy, Map<String, String> dataRow) {
        Set<Object> seen = new HashSet<>();
        for (String label : pivotHierarchy) {
            if (!dataRow.containsKey(label))
                throw new IllegalArgumentException("pivotHierarchy '" + label + "' is not consistent with the provided dataRow.");

            if ("#".equals(label))
                throw new IllegalArgumentException("pivotHierarchy label '" + label + "' is not valid.");

            if (!seen.add(label))
                throw new IllegalArgumentException("pivotHierarchy cannot contain duplicates");
        }
    }

    private void validateQueryLabels(List<String> queryLabels) {
        if (queryLabels == null)
            throw new IllegalArgumentException("queryLabels cannot be null.");
    }

    private Comparator<String> getHierarchyComparator(List<String> pivotHierarchy) {
        return (s1, s2) -> {
            pivotHierarchy.add("#");
            // ensure both strings are valid
            if (!pivotHierarchy.contains(s1))
                throw new IllegalArgumentException("Invalid String: " + s1);

            if (!pivotHierarchy.contains(s2))
                throw new IllegalArgumentException("Invalid String: " + s2);

            // compare based on the pivot hierarchy
            return Integer.compare(pivotHierarchy.indexOf(s1), pivotHierarchy.indexOf(s2));
        };
    }

    List<PivotRow> convert(List<Map<String, String>> dataRows) {
        List<PivotRow> pivotRows = new ArrayList<>();

        for (Map<String, String> dataRow : dataRows) {
            List<String> sortedLabels = dataRow.keySet().stream().sorted(this.pivotHierarchyComparator).toList();

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
     * Does not adhere to SRP, but is more efficient,
     * having O(n * log(m) * m) complexity rather than 2 * (O(n * log(m) * m)) complexity,
     * where n = #rows, m = #labels
     */
    @NotForUse(reason = "Does not adhere to Single Responsibility Principle")
    List<PivotRow> validateAndConvert(List<Map<String, String>> dataRows, List<String> pivotHierarchy) {
        if (dataRows == null || dataRows.isEmpty())
            throw new IllegalArgumentException("dataRows cannot be null or empty. Ensure that you provide a valid list of dataRows.");

        List<String> labels = new ArrayList<>(dataRows.get(0).keySet());
        int labelsSize = labels.size();

        List<PivotRow> pivotRows = new ArrayList<>();

        for (Map<String, String> dataRow : dataRows) {
            validateDataRow(dataRow, labelsSize);
            List<String> sortedLabels = dataRow.keySet().stream().sorted(this.pivotHierarchyComparator).toList();

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
                throw new IllegalArgumentException("pivotHierarchy " + hierarchy + " is not consistent with the provided dataRows.");

        return pivotRows;
    }
}
