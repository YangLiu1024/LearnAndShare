# Qualify
qualify 和 where, having 类似，都是用于 filter, 区别在于 qualify 是作用于 window function. 且 qualify 的执行时机是在 where 和 having 之后。
```js
SELECT
    schema_name,
    function_name,
    row_number() OVER (PARTITION BY schema_name ORDER BY function_name) AS function_rank
FROM duckdb_functions()
QUALIFY
    function_rank < 3;

duckdb> from cities;
┌─────────┬───────────────┬──────┬────────────┐
│ country ┆ name          ┆ year ┆ population │
╞═════════╪═══════════════╪══════╪════════════╡
│ NL      ┆ Amsterdam     ┆ 2000 ┆       1005 │
│ NL      ┆ Amsterdam     ┆ 2010 ┆       1065 │
│ NL      ┆ Amsterdam     ┆ 2020 ┆       1158 │
│ US      ┆ Seattle       ┆ 2000 ┆        564 │
│ US      ┆ Seattle       ┆ 2010 ┆        608 │
│ US      ┆ Seattle       ┆ 2020 ┆        738 │
│ US      ┆ New York City ┆ 2000 ┆       8015 │
│ US      ┆ New York City ┆ 2010 ┆       8175 │
│ US      ┆ New York City ┆ 2020 ┆       8772 │
└─────────┴───────────────┴──────┴────────────┘

// apply window function
duckdb> select name, country,year, population, row_number() over(partition by name order by year) as row_index from cities; 
┌───────────────┬─────────┬──────┬────────────┬───────────┐
│ name          ┆ country ┆ year ┆ population ┆ row_index │
╞═══════════════╪═════════╪══════╪════════════╪═══════════╡
│ Seattle       ┆ US      ┆ 2000 ┆        564 ┆         1 │
│ Seattle       ┆ US      ┆ 2010 ┆        608 ┆         2 │
│ Seattle       ┆ US      ┆ 2020 ┆        738 ┆         3 │
│ Amsterdam     ┆ NL      ┆ 2000 ┆       1005 ┆         1 │
│ Amsterdam     ┆ NL      ┆ 2010 ┆       1065 ┆         2 │
│ Amsterdam     ┆ NL      ┆ 2020 ┆       1158 ┆         3 │
│ New York City ┆ US      ┆ 2000 ┆       8015 ┆         1 │
│ New York City ┆ US      ┆ 2010 ┆       8175 ┆         2 │
│ New York City ┆ US      ┆ 2020 ┆       8772 ┆         3 │
└───────────────┴─────────┴──────┴────────────┴───────────┘

// 对上述结果，如果我们只想对每一个 group 取第一行，我们不能直接用 where 对 row_index 做过滤， duckdb 会抛错
duckdb> select name, country,year, population, row_number() over(partition by name order by year) as row_index from cities where row_index < 2; 
Binder Error: WHERE clause cannot contain window functions!

LINE 1: select name, country,year, population, row_number() over(partition by name order by year) as row_i...

// 那么一个合适的方式，就是通过 with
duckdb> with t1 as (select name, country,year, population, row_number() over(partition by name order by year) as row_index from cities) select * from t1 where t1.row_index < 2;
┌───────────────┬─────────┬──────┬────────────┬───────────┐
│ name          ┆ country ┆ year ┆ population ┆ row_index │
╞═══════════════╪═════════╪══════╪════════════╪═══════════╡
│ Seattle       ┆ US      ┆ 2000 ┆        564 ┆         1 │
│ Amsterdam     ┆ NL      ┆ 2000 ┆       1005 ┆         1 │
│ New York City ┆ US      ┆ 2000 ┆       8015 ┆         1 │
└───────────────┴─────────┴──────┴────────────┴───────────┘

// 另一个更方便的方式，就是直接使用 qualify, qualify 可以直接对 window function 进行过滤
duckdb> select name, country,year, population, row_number() over(partition by name order by year) as row_index from cities qualify row_index < 2; 
┌───────────────┬─────────┬──────┬────────────┬───────────┐
│ name          ┆ country ┆ year ┆ population ┆ row_index │
╞═══════════════╪═════════╪══════╪════════════╪═══════════╡
│ Seattle       ┆ US      ┆ 2000 ┆        564 ┆         1 │
│ Amsterdam     ┆ NL      ┆ 2000 ┆       1005 ┆         1 │
│ New York City ┆ US      ┆ 2000 ┆       8015 ┆         1 │
└───────────────┴─────────┴──────┴────────────┴───────────┘
```
另一个比较直观的例子就是 D4A 里，我们支持一种叫做 Group & Pick 的 operation，先对数据分组，然后对每一组排序，最后选取每一组中的前几行。
```sql
-- 在使用 qualify 之前，需要先选取出 t1, 再在 t1 的基础上对 row_index 进行过滤，最后为了保持 original row order, 采用 inner join 的形式
CREATE TABLE "{new_table_name}" AS (
                     WITH t1 as (SELECT "{ROW_INDEX}", row_number() over({partitionSql}{orderSql}) as row_index from "{table_name}" WHERE {filterSql}),
                     t2 as (SELECT "{ROW_INDEX}" FROM t1 WHERE t1.row_index <= {count})
                     SELECT "{table_name}".* from "{table_name}" INNER JOIN t2 ON "{table_name}"."{ROW_INDEX}" = t2."{ROW_INDEX}")

-- 如果使用 qualify, 则只需要 with 一次。最后也为了保持 original row order, 采用 order by ROW_INDEX 的形式
CREATE TABLE "{new_table_name}" AS (
                     WITH t1 as (SELECT COLUMNS(*), row_number() over({partitionSql}{orderSql}) as row_index from "{table_name}" WHERE {filterSql} QUALIFY row_index <= {count}>)
                     SELECT COLUMNS(* EXCLUDE ('row_index') FROM t1 order by "{ROW_INDEX}"))
```