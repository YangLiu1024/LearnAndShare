# Group By & Having
group by 是用于将数据分组做聚合，在聚合之后，可能需要继续对数据进行过滤，这个时候就需要用到 having.  
having 和 where 的区别在于，where 作用于 group 之前，having 作用于 group 之后。
```js
CREATE TABLE cities (
    country VARCHAR, name VARCHAR, year INTEGER, population INTEGER
);
INSERT INTO cities VALUES
    ('NL', 'Amsterdam', 2000, 1005),
    ('NL', 'Amsterdam', 2010, 1065),
    ('NL', 'Amsterdam', 2020, 1158),
    ('US', 'Seattle', 2000, 564),
    ('US', 'Seattle', 2010, 608),
    ('US', 'Seattle', 2020, 738),
    ('US', 'New York City', 2000, 8015),
    ('US', 'New York City', 2010, 8175),
    ('US', 'New York City', 2020, 8772);


duckdb> select country, name, avg(population) as population from cities group by country, name;
┌─────────┬───────────────┬───────────────────┐
│ country ┆ name          ┆ population        │
╞═════════╪═══════════════╪═══════════════════╡
│ NL      ┆ Amsterdam     ┆            1076.0 │
│ US      ┆ Seattle       ┆ 636.6666666666666 │
│ US      ┆ New York City ┆ 8320.666666666666 │
└─────────┴───────────────┴───────────────────┘

// 使用 having 对 group 结果进行过滤
duckdb> select country, name, avg(population) as population from cities group by country, name having population < 1000;
┌─────────┬─────────┬───────────────────┐
│ country ┆ name    ┆ population        │
╞═════════╪═════════╪═══════════════════╡
│ US      ┆ Seattle ┆ 636.6666666666666 │
└─────────┴─────────┴───────────────────┘
```