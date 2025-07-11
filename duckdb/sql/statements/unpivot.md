# unpivot
unpivot 用于将多个指定列，转换为堆叠的更少的列，通常转换为 2列。  
unpivot 的 sytax 为 
```js
UNPIVOT dataset
ON column(s)
INTO
    NAME name_column_name
    VALUE value_column_name(s)
ORDER BY column(s)_with_order_direction(s)
LIMIT number_of_rows;
```

Example
```js
duckdb> from monthly_sales;
┌───────┬─────────────┬─────┬─────┬─────┬─────┬─────┬─────┐
│ empid ┆ dept        ┆ Jan ┆ Feb ┆ Mar ┆ Apr ┆ May ┆ Jun │
╞═══════╪═════════════╪═════╪═════╪═════╪═════╪═════╪═════╡
│     1 ┆ electronics ┆   1 ┆   2 ┆   3 ┆   4 ┆   5 ┆   6 │
│     2 ┆ clothes     ┆  10 ┆  20 ┆  30 ┆  40 ┆  50 ┆  60 │
│     3 ┆ cars        ┆ 100 ┆ 200 ┆ 300 ┆ 400 ┆ 500 ┆ 600 │
└───────┴─────────────┴─────┴─────┴─────┴─────┴─────┴─────┘

// 把指定列的值，堆叠到指定的两列上。新的两列，一列的 value 来自于原始指定列的 name，一列的 value 来自于原始指定列的 value
duckdb> UNPIVOT monthly_sales
   ...> ON jan, feb, mar, apr, may, jun
   ...> INTO
   ...>     NAME month
   ...>     VALUE sales;
┌───────┬─────────────┬───────┬───────┐
│ empid ┆ dept        ┆ month ┆ sales │
╞═══════╪═════════════╪═══════╪═══════╡
│     1 ┆ electronics ┆ Jan   ┆     1 │
│     1 ┆ electronics ┆ Feb   ┆     2 │
│     1 ┆ electronics ┆ Mar   ┆     3 │
│     1 ┆ electronics ┆ Apr   ┆     4 │
│     1 ┆ electronics ┆ May   ┆     5 │
│     1 ┆ electronics ┆ Jun   ┆     6 │
│     2 ┆ clothes     ┆ Jan   ┆    10 │
│     2 ┆ clothes     ┆ Feb   ┆    20 │
│     2 ┆ clothes     ┆ Mar   ┆    30 │
│     2 ┆ clothes     ┆ Apr   ┆    40 │
│     2 ┆ clothes     ┆ May   ┆    50 │
│     2 ┆ clothes     ┆ Jun   ┆    60 │
│     3 ┆ cars        ┆ Jan   ┆   100 │
│     3 ┆ cars        ┆ Feb   ┆   200 │
│     3 ┆ cars        ┆ Mar   ┆   300 │
│     3 ┆ cars        ┆ Apr   ┆   400 │
│     3 ┆ cars        ┆ May   ┆   500 │
│     3 ┆ cars        ┆ Jun   ┆   600 │
└───────┴─────────────┴───────┴───────┘
// 通常需要处理的列是动态的，不好提前确定，可以使用 COLUMNS(* EXCLUDE) 来过滤
duckdb> UNPIVOT monthly_sales
   ...> ON COLUMNS(* EXCLUDE (empid, dept))
   ...> INTO
   ...>     NAME month
   ...>     VALUE sales;
┌───────┬─────────────┬───────┬───────┐
│ empid ┆ dept        ┆ month ┆ sales │
╞═══════╪═════════════╪═══════╪═══════╡
│     1 ┆ electronics ┆ Jan   ┆     1 │
│     1 ┆ electronics ┆ Feb   ┆     2 │
│     1 ┆ electronics ┆ Mar   ┆     3 │
│     1 ┆ electronics ┆ Apr   ┆     4 │
│     1 ┆ electronics ┆ May   ┆     5 │
│     1 ┆ electronics ┆ Jun   ┆     6 │
│     2 ┆ clothes     ┆ Jan   ┆    10 │
│     2 ┆ clothes     ┆ Feb   ┆    20 │
│     2 ┆ clothes     ┆ Mar   ┆    30 │
│     2 ┆ clothes     ┆ Apr   ┆    40 │
│     2 ┆ clothes     ┆ May   ┆    50 │
│     2 ┆ clothes     ┆ Jun   ┆    60 │
│     3 ┆ cars        ┆ Jan   ┆   100 │
│     3 ┆ cars        ┆ Feb   ┆   200 │
│     3 ┆ cars        ┆ Mar   ┆   300 │
│     3 ┆ cars        ┆ Apr   ┆   400 │
│     3 ┆ cars        ┆ May   ┆   500 │
│     3 ┆ cars        ┆ Jun   ┆   600 │
└───────┴─────────────┴───────┴───────┘
// 有的时候，可能需要把 columns 映射到不同的 列
duckdb> UNPIVOT monthly_sales
            //意思是把 jan,feb,mar 三列当作一组一起处理，最后生成的 value 列也必须有三列, 且其它组也必须有三列
            //生成的 name 列里的 value 就来自于表达式 (jan, feb, mar), 如果不使用 as q1, 则其值会是 Jan_Feb_Mar
   ...>     ON (jan, feb, mar) AS q1, (apr, may, jun) AS q2 
   ...>     INTO
   ...>         NAME quarter
   ...>         VALUE month_1_sales, month_2_sales, month_3_sales;
┌───────┬─────────────┬─────────┬───────────────┬───────────────┬───────────────┐
│ empid ┆ dept        ┆ quarter ┆ month_1_sales ┆ month_2_sales ┆ month_3_sales │
╞═══════╪═════════════╪═════════╪═══════════════╪═══════════════╪═══════════════╡
│     1 ┆ electronics ┆ q1      ┆             1 ┆             2 ┆             3 │
│     1 ┆ electronics ┆ q2      ┆             4 ┆             5 ┆             6 │
│     2 ┆ clothes     ┆ q1      ┆            10 ┆            20 ┆            30 │
│     2 ┆ clothes     ┆ q2      ┆            40 ┆            50 ┆            60 │
│     3 ┆ cars        ┆ q1      ┆           100 ┆           200 ┆           300 │
│     3 ┆ cars        ┆ q2      ┆           400 ┆           500 ┆           600 │
└───────┴─────────────┴─────────┴───────────────┴───────────────┴───────────────┘
```