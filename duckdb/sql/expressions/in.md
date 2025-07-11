# IN
IN 表达式用来对值进行限制
```js
// 最简单的场景，就是 in (val1, val2) 的场景，如果值在 tuple 里，则返回 true, 如果不在，则返回 false
duckdb> SELECT 'Math' IN ('CS', 'Math');
┌────────────────────────────┐
│ ('Math' IN ('CS', 'Math')) │
╞════════════════════════════╡
│                       true │
└────────────────────────────┘

// 比较特殊的场景在于 tuple 里面包含 NULL 值
duckdb> SELECT 'Math' IN ('CS', 'Math', NULL);
┌──────────────────────────────────┐
│ ('Math' IN ('CS', 'Math', NULL)) │
╞══════════════════════════════════╡
│                             true │
└──────────────────────────────────┘
// 如果不在 tuple 里面，且 tuple 含有 NULL, 则会返回 NULL
duckdb> SELECT 'Chinese' IN ('CS', 'Math', NULL) as i;
┌───┐
│ i │
╞═══╡
│   │
└───┘

// 但是如果不是  tuple, 而是 list, 对 NULL 的处理不一样
duckdb> SELECT 'English' IN ['CS', 'Math', NULL] as i;
┌───────┐
│ i     │
╞═══════╡
│ false │
└───────┘

// 但同样的，如果操作值本身是 NULL, 则返回结果一直都是 NULL
duckdb> SELECT NULL IN ['CS', 'Math', NULL] as i;
┌───┐
│ i │
╞═══╡
│   │
└───┘

// IN 还可以作用于返回 single column 的 sub query, 如果 sub query 返回了超过两列的数据，则抛错
duckdb> SELECT 42 IN (SELECT unnest([32, 42, 52]) AS x) as x;
┌──────┐
│ x    │
╞══════╡
│ true │
└──────┘
```