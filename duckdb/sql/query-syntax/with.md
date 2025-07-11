# With
With 可以在 main query 之前，提前进行一些 query, 并把 query 结果作为 table 被 main query 使用，且不同的 cte table 可以互相引用(但不能构成 recursive，如果存在，则需要使用 WITH RECURSIVE)
```js
duckdb> WITH
   ...>     cte1 AS (SELECT 42 AS i),
   ...>     cte2 AS (SELECT unnest([1, 2]) as y, i * 100 AS x FROM cte1),
   ...>     cte3 AS (SELECT i, x, y FROM cte1, cte2)
   ...> SELECT * FROM cte3;
┌────┬──────┬───┐
│ i  ┆ x    ┆ y │
╞════╪══════╪═══╡
│ 42 ┆ 4200 ┆ 1 │
│ 42 ┆ 4200 ┆ 2 │
└────┴──────┴───┘
```
with 的另一个作用在于，如果该 cte table 执行了分组聚合，且被 引用多次，那么就会被 *materialized*, 多个引用其实就只会执行一次，从而提高性能
```js
WITH t(x) AS (complex_query)
SELECT *
FROM
    t AS t1,
    t AS t2,
    t AS t3;

// 如果不做 materialized, 则等价于将 with 在 main query 里展开三次
SELECT *
FROM
    (complex_query) AS t1(x),
    (complex_query) AS t2(x),
    (complex_query) AS t3(x);

// 如果做了 materialized, 则只会执行一次
WITH t(x) AS MATERIALIZED (complex_query)
SELECT *
FROM
    t AS t1,
    t AS t2,
    t AS t3;
```