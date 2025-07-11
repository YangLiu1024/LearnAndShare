# filter
对数据有过滤作用的 syntax 现在有 *WHERE*, *HAVING*, *QUALIFY*, 以及 *FILTER*. 不同的 clause 有不同的使用场景
* where, 用于正常的 query, such as *select * from cities where population > 1000*
* having, 是作用于 group by 之后的数据的，*select name, sum(population) as total from cities group by name having total > 2000*. 如果要在这里添加 where, 只能作用于 group by 之前
```js
duckdb> select name, sum(population) as total from cities where country = 'US' group by name having total > 2000;
┌───────────────┬───────┐
│ name          ┆ total │
╞═══════════════╪═══════╡
│ New York City ┆ 24962 │
└───────────────┴───────┘
```
* qualify, 是作用于 window function 的，对 window function 生成的列，不能直接使用 where, 需要使用 qualify
```js
duckdb> select name, year, population,row_number() over(partition by name order by population desc) as row_index from cities qualify row_index <=2;
┌───────────────┬──────┬────────────┬───────────┐
│ name          ┆ year ┆ population ┆ row_index │
╞═══════════════╪══════╪════════════╪═══════════╡
│ Seattle       ┆ 2020 ┆        738 ┆         1 │
│ Seattle       ┆ 2010 ┆        608 ┆         2 │
│ Amsterdam     ┆ 2020 ┆       1158 ┆         1 │
│ Amsterdam     ┆ 2010 ┆       1065 ┆         2 │
│ New York City ┆ 2020 ┆       8772 ┆         1 │
│ New York City ┆ 2010 ┆       8175 ┆         2 │
└───────────────┴──────┴────────────┴───────────┘
```
* filter, 作用于 select 语句中的聚合函数，用来限制参与该聚合函数的数据
```sql
SELECT
    count() AS total_rows,
    count() FILTER (i <= 5) AS lte_five,
    count() FILTER (i % 2 = 1) AS odds
FROM generate_series(1, 10) tbl(i);
```
filter 和 case when 相比还有一些不同, 对于 first/last aggregtion, filter 可以过滤掉 NULL, case when 不会
```js
CREATE TEMP TABLE stacked_data AS
    SELECT
        i,
        CASE WHEN i <= rows * 0.25  THEN 2022
             WHEN i <= rows * 0.5   THEN 2023
             WHEN i <= rows * 0.75  THEN 2024
             WHEN i <= rows * 0.875 THEN 2025
             ELSE NULL
             END AS year
    FROM (
        SELECT
            i,
            count(*) OVER () AS rows
        FROM generate_series(1, 20) tbl(i)
    ) tbl;

duckdb> from stacked_data;
┌────┬──────┐
│ i  ┆ year │
╞════╪══════╡
│  1 ┆ 2022 │
│  2 ┆ 2022 │
│  3 ┆ 2022 │
│  4 ┆ 2022 │
│  5 ┆ 2022 │
│  6 ┆ 2023 │
│  7 ┆ 2023 │
│  8 ┆ 2023 │
│  9 ┆ 2023 │
│ 10 ┆ 2023 │
│ 11 ┆ 2024 │
│ 12 ┆ 2024 │
│ 13 ┆ 2024 │
│ 14 ┆ 2024 │
│ 15 ┆ 2024 │
│ 16 ┆ 2025 │
│ 17 ┆ 2025 │
│ 18 ┆      │
│ 19 ┆      │
│ 20 ┆      │
└────┴──────┘

// filter 的效果是预期的
duckdb> SELECT
   ...>     first(i) FILTER (year = 2022) AS "2022",
   ...>     first(i) FILTER (year = 2023) AS "2023",
   ...>     first(i) FILTER (year = 2024) AS "2024",
   ...>     first(i) FILTER (year = 2025) AS "2025",
   ...>     first(i) FILTER (year IS NULL) AS "NULLs"
   ...> FROM stacked_data;
┌──────┬──────┬──────┬──────┬───────┐
│ 2022 ┆ 2023 ┆ 2024 ┆ 2025 ┆ NULLs │
╞══════╪══════╪══════╪══════╪═══════╡
│    1 ┆    6 ┆   11 ┆   16 ┆    18 │
└──────┴──────┴──────┴──────┴───────┘

// 但是 case when 的效果有不同，因为 case when 不会 ignore NULL
duckdb> SELECT
   ...>     first(CASE WHEN year = 2022 THEN i END) AS "2022",
   ...>     first(CASE WHEN year = 2023 THEN i END) AS "2023",
   ...>     first(CASE WHEN year = 2024 THEN i END) AS "2024",
   ...>     first(CASE WHEN year = 2025 THEN i END) AS "2025",
   ...>     first(CASE WHEN year IS NULL THEN i END) AS "NULLs"
   ...> FROM stacked_data;
┌──────┬──────┬──────┬──────┬───────┐
│ 2022 ┆ 2023 ┆ 2024 ┆ 2025 ┆ NULLs │
╞══════╪══════╪══════╪══════╪═══════╡
│    1 ┆      ┆      ┆      ┆       │
└──────┴──────┴──────┴──────┴───────┘

duckdb> select case when year=2023 then i end as "2023" from stacked_data;
┌──────┐
│ 2023 │
╞══════╡
│      │
│      │
│      │
│      │
│      │
│    6 │
│    7 │
│    8 │
│    9 │
│   10 │
│      │
│      │
│      │
│      │
│      │
│      │
│      │
│      │
│      │
│      │
└──────┘
```