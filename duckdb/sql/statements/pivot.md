# Pivot
pivot 就是把行按照某些列的值进行聚合，并转为列。  
duckdb 支持的 pivot syntax
```js
PIVOT dataset
ON columns // 每一列可以继续使用 IN 来做 filter, 比如 name IN ('anne', 'david')
USING values // USING 只支持 aggregation 操作, 列名可以 as, 比如 using sum(population) as total, 最后生成的 column 就会是 ${column_value_combination}_total
GROUP BY rows // group by 是 optional, 如果不指定，则是 group by 除了 ON, USING 指定的 其它的所有 columns
ORDER BY columns_with_order_directions
LIMIT number_of_rows;
```
首先，`ON COLUMNS` 就是先对指定的列就行 group by, 每一种 combination 对应着多行，且每一种 combination 对应着新的一列。列名就是每一列的值通过 '_' join 在一起。  
`USING values` 就是指定对每一种 combination 对应的多行结果怎么聚合，不同的聚合方式会生成不同的列。比如 sum(population) as total, max(population) as max.  
`GROUP BY` 指定怎么对每一种 combination对应的多行结果进一步 group by. 这个会影响最后 pivot 的结果的行，`ON COLUMNS` 影响的是列。  
`ORDER BY` 对最后的结果进行排序，这里可以指定 pivot 生成的 columns

Example
```js
duckdb> select * from cities;
┌────┬─────────┬───────────────┬──────┬────────────┐
│ id ┆ country ┆ name          ┆ year ┆ population │
╞════╪═════════╪═══════════════╪══════╪════════════╡
│  1 ┆ NL      ┆ Amsterdam     ┆ 2000 ┆       1005 │
│  2 ┆ NL      ┆ Amsterdam     ┆ 2010 ┆       1065 │
│  3 ┆ NL      ┆ Amsterdam     ┆ 2020 ┆       1158 │
│  4 ┆ US      ┆ Seattle       ┆ 2000 ┆        564 │
│  5 ┆ US      ┆ Seattle       ┆ 2010 ┆        608 │
│  6 ┆ US      ┆ Seattle       ┆ 2020 ┆        738 │
│  7 ┆ US      ┆ New York City ┆ 2000 ┆       8015 │
│  8 ┆ US      ┆ New York City ┆ 2010 ┆       8175 │
│  9 ┆ US      ┆ New York City ┆ 2020 ┆       8772 │
└────┴─────────┴───────────────┴──────┴────────────┘

// 如果不指定 group by, 则默认会对其它所有列进行 group by. 因为这里有 id column, 所以会导致每一行都是一个 combination
// 这大概率不是我们想要的，所以需要指定 group by
duckdb> pivot cities on year using sum(population);
┌────┬─────────┬───────────────┬──────┬──────┬──────┐
│ id ┆ country ┆ name          ┆ 2000 ┆ 2010 ┆ 2020 │
╞════╪═════════╪═══════════════╪══════╪══════╪══════╡
│  1 ┆ NL      ┆ Amsterdam     ┆ 1005 ┆      ┆      │
│  2 ┆ NL      ┆ Amsterdam     ┆      ┆ 1065 ┆      │
│  3 ┆ NL      ┆ Amsterdam     ┆      ┆      ┆ 1158 │
│  4 ┆ US      ┆ Seattle       ┆  564 ┆      ┆      │
│  5 ┆ US      ┆ Seattle       ┆      ┆  608 ┆      │
│  6 ┆ US      ┆ Seattle       ┆      ┆      ┆  738 │
│  7 ┆ US      ┆ New York City ┆ 8015 ┆      ┆      │
│  8 ┆ US      ┆ New York City ┆      ┆ 8175 ┆      │
│  9 ┆ US      ┆ New York City ┆      ┆      ┆ 8772 │
└────┴─────────┴───────────────┴──────┴──────┴──────┘

// 如果指定了 group by, 那么其它未使用的 column 则不会被考虑
duckdb> pivot cities on year using sum(population) group by country, name;
┌─────────┬───────────────┬──────┬──────┬──────┐
│ country ┆ name          ┆ 2000 ┆ 2010 ┆ 2020 │
╞═════════╪═══════════════╪══════╪══════╪══════╡
│ NL      ┆ Amsterdam     ┆ 1005 ┆ 1065 ┆ 1158 │
│ US      ┆ Seattle       ┆  564 ┆  608 ┆  738 │
│ US      ┆ New York City ┆ 8015 ┆ 8175 ┆ 8772 │
└─────────┴───────────────┴──────┴──────┴──────┘

// 如果对 year = 2020 不感兴趣，则可以使用 IN
duckdb> pivot cities on year IN (2000, 2010) using sum(population) group by country, name;
┌─────────┬───────────────┬──────┬──────┐
│ country ┆ name          ┆ 2000 ┆ 2010 │
╞═════════╪═══════════════╪══════╪══════╡
│ NL      ┆ Amsterdam     ┆ 1005 ┆ 1065 │
│ US      ┆ Seattle       ┆  564 ┆  608 │
│ US      ┆ New York City ┆ 8015 ┆ 8175 │
└─────────┴───────────────┴──────┴──────┘

// 同样的，如果只想统计 country level
duckdb> pivot cities on year IN (2000, 2010) using sum(population) group by country;
┌─────────┬──────┬──────┐
│ country ┆ 2000 ┆ 2010 │
╞═════════╪══════╪══════╡
│ NL      ┆ 1005 ┆ 1065 │
│ US      ┆ 8579 ┆ 8783 │
└─────────┴──────┴──────┘

// 如果想要通过 country 和 name 来 pivot
// 这里会按照 country 和 name 的 distinct value 来做全排列，每一种组合都会生成一列，但有些列可能会没有数据
duckdb> pivot cities on country, name using sum(population) group by year;
┌──────┬──────────────┬──────────────────┬────────────┬──────────────┬──────────────────┬────────────┐
│ year ┆ NL_Amsterdam ┆ NL_New York City ┆ NL_Seattle ┆ US_Amsterdam ┆ US_New York City ┆ US_Seattle │
╞══════╪══════════════╪══════════════════╪════════════╪══════════════╪══════════════════╪════════════╡
│ 2000 ┆         1005 ┆                  ┆            ┆              ┆             8015 ┆        564 │
│ 2010 ┆         1065 ┆                  ┆            ┆              ┆             8175 ┆        608 │
│ 2020 ┆         1158 ┆                  ┆            ┆              ┆             8772 ┆        738 │
└──────┴──────────────┴──────────────────┴────────────┴──────────────┴──────────────────┴────────────┘

// 为了只添加有数据的列，可以通过表达式来指定 ON COLUMNS
// country || '_' || name 则只会使用行本身的 value 来做 distinct, 而不是不同列的 全排列。 '_' 是为了仿照全排列的命名规则，可以是任意其它的表达式
duckdb> pivot cities on country || '_' || name using sum(population) group by year;
┌──────┬──────────────┬──────────────────┬────────────┐
│ year ┆ NL_Amsterdam ┆ US_New York City ┆ US_Seattle │
╞══════╪══════════════╪══════════════════╪════════════╡
│ 2000 ┆         1005 ┆             8015 ┆        564 │
│ 2010 ┆         1065 ┆             8175 ┆        608 │
│ 2020 ┆         1158 ┆             8772 ┆        738 │
└──────┴──────────────┴──────────────────┴────────────┘

// USING values 也可以指定多个聚合方式
duckdb> PIVOT cities
   ...> ON year
   ...> USING sum(population) AS total, max(population) AS max
   ...> GROUP BY country;
┌─────────┬────────────┬──────────┬────────────┬──────────┬────────────┬──────────┐
│ country ┆ 2000_total ┆ 2000_max ┆ 2010_total ┆ 2010_max ┆ 2020_total ┆ 2020_max │
╞═════════╪════════════╪══════════╪════════════╪══════════╪════════════╪══════════╡
│ NL      ┆       1005 ┆     1005 ┆       1065 ┆     1065 ┆       1158 ┆     1158 │
│ US      ┆       8579 ┆     8015 ┆       8783 ┆     8175 ┆       9510 ┆     8772 │
└─────────┴────────────┴──────────┴────────────┴──────────┴────────────┴──────────┘
```
