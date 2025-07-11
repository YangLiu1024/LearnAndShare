# Select
select 后可以跟 column names, 也可以使用 '*', '*' 会推导  FROM 语句中所有可能的表达式，更进一步，可以使用 `COLUMNS('*')` 来选定 columns. *COLUMNS* 也支持正则表达式，比如 *COLUMNS('number\d+')*.  
有的时候，想简单替换其中某一列
```js
select * REPLACE (lower(city) as city) from address;
select * EXCLUDE city, lower(city) as city from address;

// 选择所有列的最小值
select min(COLUMNS('*')) from address;

// 选取 distinct value， 有一个 distinct on clause
select distinct city from address; // 选取所有 distinct city
// 注意这里 DISTINCT ON(city) 后不跟 comma
select distinct ON (city) name, age from address; // DISTINCT ON(COLUMNS) 只返回满足条件的一行
select distinct ON (city) name, age from address order by population; // 返回按照 city 分组后排序的第一行

// 对于存在聚合函数的查询，那么查询的 列要么是 group by, 要么就是聚合列
select dept, count('*') from address group by dept;
```

# From
from 后一般跟 table, 或者一些 selection node, 亦或者一些 data source
```js
select * from data;
select * from (select 10 as i, 'A' as j);
duckdb> select * from (select 10 as i, 'A' as j);
┌────┬───┐
│ i  ┆ j │
╞════╪═══╡
│ 10 ┆ A │
└────┴───┘

select * from 'test.csv';

select i from range(100) as t(i);// range(100) 生成了一个从 0 到 99 的序列，range(100) as t(i) 则是把这个序列作为有一个列名为 i 的 table

// table 还支持 sample
select * from data TABLESAMPLE 10%; // 也支持其它的 sample method
```

# Join
## Cross Join
一个最基础的 join 则是 cross join, 即全排列
```js
select A.*, B.* from A, B; // CROSS Join 没有 join condition, A 的 每一行都会和 B 的 每一行生成新的一行
```
## Conditional Join
通常来说，join 是需要有 condition 的。Conditional Join 有 inner join 和 outer join. inner join 则是 仅返回 left 和 right side 都满足 join condition 的 行，outer join 则分为 left join, right join 和 full join.  
## Lateral Join
有一个特殊的 join 是 lateral join, 它允许下一个 subquery 从上一个 subquery 的结果里生成。
```js
SELECT *
FROM range(3) t(i), LATERAL (SELECT i + 1) t2(j);
duckdb> SELECT *
   ...> FROM range(3) t(i), LATERAL (SELECT i + 1) t2(j);
┌───┬───┐
│ i ┆ j │
╞═══╪═══╡
│ 0 ┆ 1 │
│ 1 ┆ 2 │
│ 2 ┆ 3 │
└───┴───┘
```
## Positional Join
有的时候，左右两张表已经是按照顺序排列的，且 size 相同，没必要再通过 condition 来 join 两张表，可以直接通过 *positional join*
```js
SELECT *
FROM t1
POSITIONAL JOIN t2;
```