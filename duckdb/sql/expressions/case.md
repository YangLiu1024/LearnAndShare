# case
case when 是经典的 sql 中做 switch 的表达式
```js
duckdb> CREATE OR REPLACE TABLE integers AS SELECT unnest([1, 2, 3]) AS i;
   ...> SELECT i, CASE WHEN i > 2 THEN 1 ELSE 0 END AS test
   ...> FROM integers;
┌───┬──────┐
│ i ┆ test │
╞═══╪══════╡
│ 1 ┆    0 │
│ 2 ┆    0 │
│ 3 ┆    1 │
└───┴──────┘

// when 是可以串联的，最后的 else 也是 optional, 如果前面的 when 都没有命中，则最后直接返回 NULL
// 此例中，如果不写 ELSE, 则 i=3 时返回 NULL
duckdb> CREATE OR REPLACE TABLE integers AS SELECT unnest([1, 2, 3]) AS i;
   ...> SELECT i, CASE WHEN i = 1 THEN 10 WHEN i = 2 THEN 20 ELSE 0 END AS test
   ...> FROM integers;
┌───┬──────┐
│ i ┆ test │
╞═══╪══════╡
│ 1 ┆   10 │
│ 2 ┆   20 │
│ 3 ┆    0 │
└───┴──────┘

// 如果只有一个 when, 后跟 then, else, 可以简单的使用 IF
duckdb> SELECT i, IF(i > 2, 1, 0) AS test
   ...> FROM integers;
┌───┬──────┐
│ i ┆ test │
╞═══╪══════╡
│ 1 ┆    0 │
│ 2 ┆    0 │
│ 3 ┆    1 │
└───┴──────┘

// case 后也可以直接跟表达式，这样 WHEN 后面就可以简写
duckdb> CREATE OR REPLACE TABLE integers AS SELECT unnest([1, 2, 3]) AS i;
   ...> SELECT i, CASE i WHEN 1 THEN 10 WHEN 2 THEN 20 WHEN 3 THEN 30 END AS test
   ...> FROM integers;
┌───┬──────┐
│ i ┆ test │
╞═══╪══════╡
│ 1 ┆   10 │
│ 2 ┆   20 │
│ 3 ┆   30 │
└───┴──────┘
```