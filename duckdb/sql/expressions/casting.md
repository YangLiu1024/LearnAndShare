# casting
数据的类型转换也是必要的
```sql
-- 将 i 的 column type 显式转换为 varchar
select cast(i as varchar) as i from test;

-- 等价于
select i::varchar from test
```
但 并不是所有的 cast 都是 可行的，比如 *'hello':integer* 就会抛错，如果想不抛异常，则可以使用 *TRY_CAST*
```sql
-- 如果发现不能转换，则不会抛错，而是返回 NULL
SELECT TRY_CAST('hello' AS INTEGER) AS i;
```