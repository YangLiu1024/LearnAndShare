# Set operations
## union
union 就是上下拼接， union by default 是会去重的，union all 则不会
```js
duckdb> SELECT * FROM range(2) t1(x)
   ...> UNION
   ...> SELECT * FROM range(3) t2(x);
┌───┐
│ x │
╞═══╡
│ 0 │
│ 1 │
│ 2 │
└───┘
// union all 不会去重
duckdb> SELECT * FROM range(2) t1(x)
   ...> UNION ALL
┌───┐.> SELECT * FROM range(3) t2(x);
│ x │
╞═══╡
│ 0 │
│ 1 │
│ 0 │
│ 1 │
│ 2 │
└───┘

// union 支持按 column name union, 这样上下 table 就不需要有相同数量的 columns
// 对于不匹配的 columns, 则填充 NULL
duckdb> SELECT * FROM range(2) t1(x)
   ...> UNION BY NAME
   ...> SELECT * FROM values (1,2),(2,3),(1,2) t2(x,y);
┌───┬───┐
│ x ┆ y │
╞═══╪═══╡
│ 0 ┆   │
│ 1 ┆   │
│ 1 ┆ 2 │
│ 2 ┆ 3 │
└───┴───┘
```

## intersect
intersect 是取交集，且对交集去重，intersect all 则对交集不去重
## except
except 则是仅返回在左边 table 出现的 行。 except all 的行为比较奇特，它的定义是如果左边的一行只会被右边相同的一行抵消一次。  
比如左边有 values (2), (2), 右边有 values(2), 那么 仍然存在 except all 的结果里
```js
duckdb> SELECT unnest([5, 5, 6, 6, 6, 6, 7, 8]) AS x
   ...> EXCEPT ALL
   ...> SELECT unnest([5, 6, 6, 7, 7, 9]);
┌───┐
│ x │
╞═══╡
│ 6 │
│ 6 │
│ 8 │
│ 5 │
└───┘
```