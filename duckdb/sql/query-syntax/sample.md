# Sample
duckdb 支持在 query 的结果上进行 sampling.
```js
SELECT *
FROM addresses
USING SAMPLE 1%; // 可以是百分比，也可以是指定 N rows. 还可以指定 采样方法
```