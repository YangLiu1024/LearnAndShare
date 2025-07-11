# Order by
order by 正常来说，就是指定排序。有一个特殊在于，还可以指定 NULL value 的排序方式。
```js
// 注意 NULLS LAST 之前没有 comma
order by expr NULLS LAST;// NULLS 可以是 LAST, FIRST, 

// 当然也可以直接设置 duckdb 的 nulls 排序默认设置
SET default_null_order = 'NULLS_FIRST';
```