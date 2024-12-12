# Why
The goal of this project was merely to practice **TDD** (Test Driven Development) while designing a robust and intuitive **API**.

# What

A **Java API** whose core functionality exposed is **pivoting**.
It is the so-called...

## Pivoter

### API Input
The API input is composed of:
1. **Data**: A collection of rows, each composed of a numerical value and a tuple of labels:

```
rowᵢ = (L₁ᵢ, L₂ᵢ, L₃ᵢ, ..., Lₙᵢ, Vᵢ)
```

Here, `Lⱼᵢ` belongs to Domain `j`.

2. **Aggregation Function**: A function that takes a collection of numerical values as input and returns a single value. The library must accept **custom aggregation functions** defined by the API consumer (*e.g.*, sum, average, mode, etc.).


3. **Aggregation Order**: The description of the aggregation hierarchy.

### API Output
The output is an **aggregation tree**. 
The tree can be queried to obtain the value of aggregations at any level, for any labeling group.

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