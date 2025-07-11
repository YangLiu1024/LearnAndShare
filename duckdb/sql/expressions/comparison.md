# Comparison
数据的常规比较, duckdb 都是支持的，一个特殊的在于 如果 NULL 参与了比较，则返回值都是 NULL
```sql
-- 返回 true
 1 < 2
 -- 返回 NULL
 1 < NULL

 -- 如果想对 NULL 做判断，可以使用 IS NULL, IS NOT NULL
 select IF(i is NULL, 0, 1) as n from test;

 -- 还有一种比较就是 BETWEEN, a between x and y 需要 a,x,y 三者类型相同
 select i from test where i between 0 and 5;
```