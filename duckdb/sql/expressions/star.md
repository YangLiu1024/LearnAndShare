# star
*\** 被用来选取 FROM 中的所有列，而且也可以接在 table_name 后面， such as *test.\**, 表示只选取 test 里面的所有列
```sql
SELECT table_name.*
FROM table_name
JOIN other_table_name USING (id);
```
有的时候，还需要排除掉某些列，这个时候就可以使用 *EXCLUDE*, 用于在 * 表达式后，去掉某一些 columns
```sql
SELECT * EXCLUDE (col) FROM tbl;

-- 有时候可能不是排除掉，而是需要替换，这个时候就可以使用 replace
select * replace(col1 / 1000 as col1, col2 / 1000 as col2) from tbl;

-- 使用 rename 可以简单重命名 query
SELECT * RENAME (col1 AS height, col2 AS width) FROM tbl;

-- 对 columns 进行过滤
select * LIKE 'col%' from tbl;
```
## COLUMNS
columns 是对 * 的扩展，除了支持 对 * 表达式的一些操作，比如 exclude, replace 等，还支持一些额外的功能
```js
duckdb> CREATE TABLE numbers (id INTEGER, number INTEGER);
   ...> INSERT INTO numbers VALUES (1, 10), (2, 20), (3, NULL);
   // 对所有列求 min 和 count
   ...> SELECT min(COLUMNS(*)), count(COLUMNS(*)) FROM numbers;
┌────┬────────┬────┬────────┐
│ id ┆ number ┆ id ┆ number │
╞════╪════════╪════╪════════╡
│  1 ┆     10 ┆  3 ┆      2 │
└────┴────────┴────┴────────┘

duckdb> SELECT
   // 在 columns 表达式里对 * apply replace 和 exclude
   ...>     min(COLUMNS(* REPLACE (number + id AS number))),
   ...>     count(COLUMNS(* EXCLUDE (number)))
   ...> FROM numbers;
┌────┬──────────────────────────────┬────┐
│ id ┆ min(number := (number + id)) ┆ id │
╞════╪══════════════════════════════╪════╡
│  1 ┆                           11 ┆  3 │
└────┴──────────────────────────────┴────┘

// columns 里面也支持正则表达式，且匹配的 group 可以用来 rename \0 表示原始 column name, \1 表示第一个 group
SELECT COLUMNS('(\w{3}).*') AS '\1' FROM numbers;

duckdb> SELECT min(COLUMNS(*)) AS "min_\0" FROM numbers;
┌────────┬────────────┐
│ min_id ┆ min_number │
╞════════╪════════════╡
│      1 ┆         10 │
└────────┴────────────┘

// 还可以传入 lambda 来进行过滤
SELECT COLUMNS(c -> c LIKE '%num%') FROM numbers;

// 传入一个 list 也可以
SELECT COLUMNS(['id', 'number']) FROM numbers;
```
