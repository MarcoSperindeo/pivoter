# Pivot Table Report

The goal for this assignment is to create a **Java API**, the kind of API that can be used as a library and not the REST API kind. The only functionality to expose is a **pivot functionality**.

### API Input
The API input is composed of:
1. **Data**: A collection of rows, each composed of a numerical value and a tuple of labels:

```
rowᵢ = (L₁ᵢ, L₂ᵢ, L₃ᵢ, ..., Lₙᵢ, Vᵢ)
```

Here, `Lⱼᵢ` belongs to Domain `j`.

2. **Aggregation Function**: A function that takes a collection of numerical values as input and returns a single value.

3. **Aggregation Order**: The description of the aggregation hierarchy.

### API Output
The output is an **aggregation tree**:
- This tree can be queried to obtain the value of aggregations at any level, for any labeling group.

---

### Deliverable
- The deliverable must be a Maven/Gradle project complete with **unit tests** to verify the correct implementation of the library.
- The library must accept **custom aggregation functions** defined by the user (e.g., average, median).

---

### Example

#### Data:
| Nation  | Eyes  | Hair   | #   |
|---------|-------|--------|-----|
| Germany | Green | Brown  | 168 |
| Spain   | Green | Brown  | 359 |
| Germany | Blue  | Brown  | 389 |
| Germany | Dark  | Black  | 468 |
| Germany | Dark  | Brown  | 103 |
| France  | Blue  | Black  | 506 |
| Italy   | Dark  | Black  | 148 |
| Spain   | Brown | Red    | 778 |
| Germany | Green | Red    | 536 |
| France  | Green | Blonde | 288 |
| France  | Green | Black  | 857 |
| Spain   | Dark  | Black  | 907 |
| Germany | Green | Red    | 906 |
| Germany | Brown | Red    | 753 |
| Spain   | Blue  | Black  | 852 |
| France  | Blue  | Black  | 498 |

---

#### Aggregation Function:
`SUM`

#### Aggregation Order:
`Nation -> Eyes -> Hair`

#### Result:
| Nation  | Eyes  | Hair   | SUM of # |
|---------|-------|--------|----------|
| France  | Blue  | Black  | 1004     |
|         | Blue  | Total  | 1004     |
|         | Green | Black  | 857      |
|         |       | Blonde | 288      |
|         | Green | Total  | 1145     |
| **France Total**         |          | 2149     |
| Germany | Blue  | Brown  | 389      |
|         | Blue  | Total  | 389      |
|         | Brown | Total  | 753      |
|         | Dark  | Black  | 468      |
|         |       | Brown  | 103      |
|         | Dark  | Total  | 571      |
|         | Green | Brown  | 168      |
|         |       | Red    | 1442     |
|         | Green | Total  | 1610     |
| **Germany Total**        |          | 3323     |
| Italy   | Dark  | Black  | 148      |
|         | Dark  | Total  | 148      |
| **Italy Total**          |          | 148      |
| Spain   | Blue  | Black  | 852      |
|         | Blue  | Total  | 852      |
|         | Brown | Total  | 778      |
|         | Dark  | Black  | 907      |
|         | Dark  | Total  | 907      |
|         | Green | Brown  | 359      |
|         | Green | Total  | 359      |
| **Spain Total**          |          | 2896     |
| **Grand Total**          |          | 8516     |
