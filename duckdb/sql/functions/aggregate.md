# aggregate
在常规的聚合函数之外，duckdb 支持更多。  
通常 聚合函数和 group by 一起使用，group by 之后也可以跟 having 来对聚合列做 过滤，也可以通过 filter 对参与聚合的行做过滤。  
syntax:

${aggregator}(DISTINCT? {COLUMN} ORDER BY) FILTER ({filter_expr})

```sql
-- 支持 filter
select count() FILTER age > 20, sum(amount), count(distinct address) from person;

-- 也支持 having
SELECT region
FROM sales
GROUP BY region
HAVING sum(amount) > 100;

-- 返回 region 的 list
SELECT list(region ORDER BY amount DESC)
FROM sales;
```
聚合函数也可以返回 list
```js
duckdb> from test;
┌───┐
│ i │
╞═══╡
│ 0 │
│ 1 │
│ 2 │
│ 3 │
│ 4 │
│ 5 │
│ 6 │
│ 7 │
│ 8 │
│ 9 │
└───┘

// 拿到当前 group 里的所有行 的 i, 返回 list
duckdb> select list(i order by i desc) as ids from test;
┌────────────────────────────────┐
│ ids                            │
╞════════════════════════════════╡
│ [9, 8, 7, 6, 5, 4, 3, 2, 1, 0] │
└────────────────────────────────┘
// 返回第一行
duckdb> select first(i order by i desc) from test;
┌────────────────────────────┐
│ "first"(i ORDER BY i DESC) │
╞════════════════════════════╡
│                          9 │
└────────────────────────────┘
```
一般来说，大部分聚合函数都不处理 NULL 值，除了 list, array_agg, first, last, arbitracy. 如果想在这些聚合函数里排除 NULL, 可以使用 FILTER.   
对于空的 group, 大部分聚合函数会返回 NULL, 比如 sum 不返回 0， list 不返回空数组，string_agg 不返回空字符串，而是都返回 NULL. count 除外。  

## general 聚合函数
* any_value(col), 返回 group 中第一个 non-NULL 值，顺序敏感
* arbitrary(col), 返回第一个值，nullable. 顺序敏感
* arg_max(arg, val), 返回在 val 表达式取得最大值的行所对应的 arg 表达式的值. 同 max_by.
* arg_max(arg, val, n), 对 top n 行的数据都进行 arg 表达式, 返回值是 list. 一般的应用场景为 top-N 算法
```js
   ...> SELECT * FROM sales;
┌────────────┬──────────────┬──────────┬─────────┐
│ sale_date  ┆ product_name ┆ quantity ┆ revenue │
╞════════════╪══════════════╪══════════╪═════════╡
│ 2024-01-01 ┆ Widget A     ┆       10 ┆  150.00 │
│ 2024-01-02 ┆ Widget B     ┆        5 ┆  200.00 │
│ 2024-01-03 ┆ Widget A     ┆       15 ┆  225.00 │
│ 2024-01-04 ┆ Widget B     ┆        8 ┆  180.00 │
│ 2024-01-05 ┆ Widget C     ┆       12 ┆  180.00 │
└────────────┴──────────────┴──────────┴─────────┘
// 查询在 revenue 最大值的那一行所对应的 product name
duckdb> SELECT 
   ...>     arg_max(lower(product_name), revenue) as highest_revenue_product
┌─────────────────────────┐
│ highest_revenue_product │
╞═════════════════════════╡
│ widget a                │
└─────────────────────────┘

duckdb> SELECT 
   ...>     arg_max(lower(product_name), revenue,2) as highest_revenue_product
┌─────────────────────────┐
│ highest_revenue_product │
╞═════════════════════════╡
│ [widget a, widget b]    │
└─────────────────────────┘

// 如果作用于 group by
duckdb> from employees;
┌────────┬───────────────────┬─────────────┬──────────┬────────────┐
│ emp_id ┆ emp_name          ┆ department  ┆ salary   ┆ hire_date  │
╞════════╪═══════════════════╪═════════════╪══════════╪════════════╡
│      1 ┆ Beavis Blakefield ┆ Sales       ┆ 75000.00 ┆ 2022-01-15 │
│      2 ┆ Mahavishnu Mars   ┆ Sales       ┆ 82000.00 ┆ 2021-06-20 │
│      3 ┆ Veralda Vett      ┆ Engineering ┆ 95000.00 ┆ 2020-03-10 │
│      4 ┆ Hank Heckler      ┆ Engineering ┆ 70000.00 ┆ 2021-07-11 │
│      5 ┆ Slash Slater      ┆ Engineering ┆ 98000.00 ┆ 2019-11-25 │
│      6 ┆ Amy Amish         ┆ Engineering ┆ 87000.00 ┆ 2019-12-14 │
│      7 ┆ Bon Butler        ┆ Marketing   ┆ 70000.00 ┆ 2023-02-01 │
│      8 ┆ Dave Danker       ┆ Marketing   ┆ 72000.00 ┆ 2022-09-15 │
│      9 ┆ Sally Schmit      ┆ Marketing   ┆ 82000.00 ┆ 2017-10-07 │
└────────┴───────────────────┴─────────────┴──────────┴────────────┘

duckdb> SELECT 
   ...>     department,
   ...>     arg_max(emp_name, salary, 2) as top_employees
   ...> FROM employees
   ...> GROUP BY department;
┌─────────────┬──────────────────────────────────────┐
│ department  ┆ top_employees                        │
╞═════════════╪══════════════════════════════════════╡
│ Sales       ┆ [Mahavishnu Mars, Beavis Blakefield] │
│ Engineering ┆ [Slash Slater, Veralda Vett]         │
│ Marketing   ┆ [Sally Schmit, Dave Danker]          │
└─────────────┴──────────────────────────────────────┘
```
* arg_min, 和 arg_max 是类似的， tong min_by
* array_agg, 返回当前 group 中一列的所有 value, 同 list
* bool_and, 返回 true 如果 column 中所有值都是 true, 否则返回 false
* bool_or
* count(), 返回 group 的 records 数量，count(arg), 返回 列所对应的 non-null 行的数量， countif(arg) 返回 arg 为 true 的行的数量
* histogram(arg), 返回一个 Map, key 是 column value, value 是 value count
* string_agg(arg, sep?), concatenates column values with sep(default is comma)

还有一些其它的统计聚合函数，可以参考 ![https://duckdb.org/docs/stable/sql/functions/aggregates]