# list functions
duckdb 支持数组数据类型，也有很多相关的函数操作
## list[index]
获取指定位置元素，1-based inex, 和list_extract(list, idx) 和 list[idx] 等价
## list[begin:end], list[begin:end:step]
获取指定区间元素，和 list_slice() 等价, start 和 end 都是 inclusive。如果 start 和 end 都 omit, 但是指定了 step, 那么 end 必须用 *-*
```js
duckdb> SELECT ([1, 2, 3, 4, 5])[:-:2];
┌──────────────────────────────────────┐
│ main.list_value(1, 2, 3, 4, 5)[:-:2] │
╞══════════════════════════════════════╡
│ [1, 3, 5]                            │
└──────────────────────────────────────┘

duckdb> SELECT ([1, 2, 3, 4, 5])[:-:-2] as i;
┌───────────┐
│ i         │
╞═══════════╡
│ [5, 3, 1] │
└───────────┘
```
## array_pop_back(list)
弹出最后一个元素，返回剩下的元素
## array_pop_front(list)
弹出第一个元素，返回剩下的元素
## flatten(lists...)
对一个list 元素展开一级，然后再拼接在一起
## len(list)
返回元素个数
## list_aggregate(list, name)
对list 执行指定的聚合函数
## list_any_value(list)
返回 list 第一个 non-null value
## list_apend(list, element)
添加 element 到末尾
## list_concat(lists....)
拼接 list
## list_contains(list, element)
check list 是否包含指定元素
## list_distinct(list)
list 元素去重
## list_filter(list, lambda)
过滤 list. 类似于 JS array.filter()
## list_has_all(list, sub-list)
判断 list 是否包含所有 sub-list 中的元素
## list_has_any(list1, list2)
判断 list1  和 list2 是否有交集
## list_intersect(list1, list2)
返回 list1 和 list2 的交集
## list_position(list, element)
返回元素所在位置，如果不存在，则返回 NULL
## list_reduce(list, lambda)
对数组元素 reduce, 返回 single value。类似于 js array.reduce()
## list_resize(list, size, value = NULL)
将数组置为指定长度，新增元素值为 value
## list_reverse(list)
list 逆序
## list_select(list, index-list)
根据指定的 索引数组，返回对应的 value 数组
## list_sort(list)
数组排序
## list_reverse_sort(list)
list_sort 可以指定排序方式，默认是 'ASC NULLS LAST'
```js
duckdb> select list_sort([1,3,6,2]) as i;
┌──────────────┐
│ i            │
╞══════════════╡
│ [1, 2, 3, 6] │
└──────────────┘

duckdb> select list_reverse_sort([1,3,6,2]) as i;
┌──────────────┐
│ i            │
╞══════════════╡
│ [6, 3, 2, 1] │
└──────────────┘
```
## list_transform(list, lambda)
类似于 JS array.map
```js
duckdb> select list_transform([1,3,6,2], x -> x * 2) as i;
┌───────────────┐
│ i             │
╞═══════════════╡
│ [2, 6, 12, 4] │
└───────────────┘

// 还可以访问元素索引，注意索引是 1-based
duckdb> select list_transform([1,3,6,2], (x,i) -> x +i) as i;
┌──────────────┐
│ i            │
╞══════════════╡
│ [2, 5, 9, 6] │
└──────────────┘
```
## list_unique(list)
count unique elements. 不考虑 NULL
```js
duckdb> select list_unique([1,3,6,2, 2,3, NULL]) as i;
┌───┐
│ i │
╞═══╡
│ 4 │
└───┘

// list_distinct 也不考虑 NULL
duckdb> select list_distinct([1,3,6,2, 2,3, NULL]) as i;
┌──────────────┐
│ i            │
╞══════════════╡
│ [6, 2, 3, 1] │
└──────────────┘
```
## list_value(values...)
根据传入的参数返回一个 list
```js
duckdb> select list_value(1,3,6,2, 2,3, NULL, 8) as i;
┌─────────────────────────┐
│ i                       │
╞═════════════════════════╡
│ [1, 3, 6, 2, 2, 3, , 8] │
└─────────────────────────┘
```
## repeat(list, count)
将 list 元素 重复 count 次
## unnest(list)
展开 list 一级，也可以通过 max_depth 和 recursive 指定展开级数

### 怎么 check list 里是否包含 NULL 值呢
大部分 list function 都会 ignore NULL 值，比如 list_distint, list_unique, list_has_any.那么该怎么才能知道  list 是否包含 NULL 值呢？  
duckdb 没有提供类似于 array.some() 的函数
```js
// 一个方法是使用 list_filter 过滤出 null value list, 然后 check count
duckdb> select list_filter([1,2, NULL, 3], i -> i is NULL).len() > 0 as i;
┌──────┐
│ i    │
╞══════╡
│ true │
└──────┘
// 一个方法是使用 list_transform 把  null 转换为 boolean, 然后 reduce
duckdb> select list_transform([1,2, NULL, 3], i -> i is NULL).list_reduce((i1, i2) -> i1 or i2, false) as i;
┌──────┐
│ i    │
╞══════╡
│ true │
└──────┘
```
# List operators
* &&, alias for list_has_any(list1, list2), check 是否有交集，NULL 不被考虑
* @>, alias for list_has_all, check 是否右侧的 list 是子集
* <@, check 是否左侧的 list 是子集
* ||， 拼接 list, 如果任意 list 本身是 null 会导致 null

# List Comprehension
python-style 的 列表推导也是支持的 *[f(x) for x IN y IF g(x)]*
```js
duckdb> SELECT [upper(x) FOR x IN strings IF len(x) > 0] AS strings
   ...> FROM (VALUES (['Hello', '', 'World'])) t(strings);
┌────────────────┐
│ strings        │
╞════════════════╡
│ [HELLO, WORLD] │
└────────────────┘
```
在底层，*[f(x) for x IN y IF g(x)]* 会被转换为 *list_filter(y, x -> g(x)).list_transform(x -> f(x))*
# Range functions
duckdb 提供了两个函数，range 和 generate_series, 以及它们的变种 来生成 list
## range
```js
// 对 range 来说，start 是 inclusive, end 是 exclusive
// range(end), start 默认是 0
duckdb> select range(5) as i;
┌─────────────────┐
│ i               │
╞═════════════════╡
│ [0, 1, 2, 3, 4] │
└─────────────────┘

// range(start, end), 步长默认为 1
duckdb> select range(2,5) as i;
┌───────────┐
│ i         │
╞═══════════╡
│ [2, 3, 4] │
└───────────┘

duckdb> select range(2,5, 2) as i;
┌────────┐
│ i      │
╞════════╡
│ [2, 4] │
└────────┘
```
从 range() 简单创建 table
```js
// range(10) 本身返回 一个 list
duckdb> select range(10) as i, k:10;
┌────────────────────────────────┬────┐
│ i                              ┆ k  │
╞════════════════════════════════╪════╡
│ [0, 1, 2, 3, 4, 5, 6, 7, 8, 9] ┆ 10 │
└────────────────────────────────┴────┘

// 搭配上 unnest, 就可以展开
duckdb> select unnest(range(5)) as i, j:10;
┌───┬────┐
│ i ┆ j  │
╞═══╪════╡
│ 0 ┆ 10 │
│ 1 ┆ 10 │
│ 2 ┆ 10 │
│ 3 ┆ 10 │
│ 4 ┆ 10 │
└───┴────┘

// 如果作用在 from 后， 这里 range(5) 会自动展开，其效果等同于 unnest(range(5))
// as t(i) 就是把展开后的 selection 重命名为 t, 且列名 为 i
duckdb> select * from range(5) as t(i);
┌───┐
│ i │
╞═══╡
│ 0 │
│ 1 │
│ 2 │
│ 3 │
│ 4 │
└───┘

duckdb> select * from unnest(range(5)) as t(i);
┌───┐
│ i │
╞═══╡
│ 0 │
│ 1 │
│ 2 │
│ 3 │
│ 4 │
└───┘

duckdb> select i, i+1 as j, j + 1 as k from unnest(range(5)) as t(i);
┌───┬───┬───┐
│ i ┆ j ┆ k │
╞═══╪═══╪═══╡
│ 0 ┆ 1 ┆ 2 │
│ 1 ┆ 2 ┆ 3 │
│ 2 ┆ 3 ┆ 4 │
│ 3 ┆ 4 ┆ 5 │
│ 4 ┆ 5 ┆ 6 │
└───┴───┴───┘

// 如果想在 from 后直接生成多列
duckdb>  select * from range(5) as t(i), i + 1 as j;
Parser Error: syntax error at or near "+"

LINE 1:  select * from range(5) as t(i), i + 1 as j;

// 需要使用 LATERAL join
duckdb>  select * from range(5) as t(i), LATERAL (select i + 1 as j), LATERAL (select j + 1 as k);
┌───┬───┬───┐
│ i ┆ j ┆ k │
╞═══╪═══╪═══╡
│ 0 ┆ 1 ┆ 2 │
│ 1 ┆ 2 ┆ 3 │
│ 2 ┆ 3 ┆ 4 │
│ 3 ┆ 4 ┆ 5 │
│ 4 ┆ 5 ┆ 6 │
└───┴───┴───┘
```
### Date range
range 也可以用来创建时间范围
```js
// 可以作用于 date 或者 timestamp
duckdb> SELECT *
   ...> FROM range(DATE '1992-01-01', DATE '1992-03-01', INTERVAL '5' DAY);
┌─────────────────────┐
│ range               │
╞═════════════════════╡
│ 1992-01-01T00:00:00 │
│ 1992-01-06T00:00:00 │
│ 1992-01-11T00:00:00 │
│ 1992-01-16T00:00:00 │
│ 1992-01-21T00:00:00 │
│ 1992-01-26T00:00:00 │
│ 1992-01-31T00:00:00 │
│ 1992-02-05T00:00:00 │
│ 1992-02-10T00:00:00 │
│ 1992-02-15T00:00:00 │
│ 1992-02-20T00:00:00 │
│ 1992-02-25T00:00:00 │
└─────────────────────┘
```
## generate_series
generate_series 和 range 很类似，区别在于 generate_series 的 end 是 inclusive 的
```js
// 和 range 很类似
duckdb> SELECT generate_series(5);
┌────────────────────┐
│ generate_series(5) │
╞════════════════════╡
│ [0, 1, 2, 3, 4, 5] │
└────────────────────┘
```
# List Aggregation
支持直接对 list 做聚合，正常的聚合函数都支持
```js
duckdb> SELECT list_aggregate([2, 4, 8, 42], 'sum') as i;
┌────┐
│ i  │
╞════╡
│ 56 │
└────┘

// 如果聚合函数本身需要参数，则可以继续传递
duckdb> SELECT list_aggregate([2, 4, 8, 42], 'string_agg', '|') as i;
┌──────────┐
│ i        │
╞══════════╡
│ 2|4|8|42 │
└──────────┘
```
# List Sorting
list_sort 可以提供额外的参数指定排序方式，比如 'ASC NULLS FIRST', "DESC NULLS LAST", 其行为和 ORDER BY 一致
# Flatten
flatten 是把 list 的 list 的元素 concat 一级。作用就是去掉最外层数组，让最外层数组的所有元素 concat 一次。  
如果参数是空数组，也会返回空数组。如果参数是 NULL, 则返回 NULL. 如果参数是 list, list 中的 NULL 元素会被丢弃，但如果元素本身也是 list 且 包含 NULL, 则元素内部的 NULL 不会被 touch.