# Overview
## DuckDBPyConnection
为了使用 duckdb, 需要先创建一个 *DuckDBPyConnection* 对象来表示 database
```python
import duckdb as duck

# load from file, if file not exist, will create one
conn = duck.connect('test.db') # 返回一个 DuckDBPyConnection 对象

# 使用 内存 database
conn = duck.connect(':memory:')

# 除了显示使用 connect 返回的 connection, duck module 本身也可以直接调用 DuckDBPyConnection 的方法，它使用的就是 default database
duck.execute('create table temp as select * from "test.csv"') # 这里使用的就是 default connection
conn = duck.connect(':default:') # 这里显式获取 default connection
```
## DuckDBPyRelation
另外一个重要的类型就是 *DuckDBPyRelation*, 它用来表示数据，但它并不真正 hold 任何数据，也没有执行任何语句，直到有方法 trigger 了计算。  
rel 一般是通过 SQL query 来生成的，也可以通过 *read_csv*, *read_parquet*, "from_df" 等方法来生成
```python
rel = duck.sql('select * from "test.csv"') # rel 是一个 DuckDBPyRelation 对象

rel = duck.read_csv("test.csv")

rel.show() # 当执行 show 的时候，才会真正触发 sql 的执行，但是 show 其实也只是查询前 1w 行数据
```
python 里面比较神奇的是，relation object 可以直接被使用在 sql query 里
```python
rel = duck.read_csv("test.csv")

# 直接使用 rel
duck.sql('select * from rel').show()
```
除了 relation, pandas.DataFrame, polars DataFrame, py arrow tables 也可以
```python
import duckdb

# directly query a Pandas DataFrame
import pandas as pd
pandas_df = pd.DataFrame({"a": [42]})
duckdb.sql("SELECT * FROM pandas_df")

# directly query a Polars DataFrame
import polars as pl
polars_df = pl.DataFrame({"a": [42]})
duckdb.sql("SELECT * FROM polars_df")

# directly query a pyarrow table
import pyarrow as pa
arrow_table = pa.Table.from_pydict({"a": [42]})
duckdb.sql("SELECT * FROM arrow_table")
```
### operations
relation 支持多种操作, 这些操作其实是 sql 的 short hand, 也会返回 relation object
#### aggregate(expr, groups={})
```python
rel = duck.sql('select * from test')

rel.aggregate("id % 2 as g, sum(id), min(id), max(id)")
┌───────┬──────────────┬─────────┬─────────┐
│   g   │   sum(id)    │ min(id) │ max(id) │
│ int64 │    int128    │  int64  │  int64  │
├───────┼──────────────┼─────────┼─────────┤
│     0 │ 249999500000 │       0 │  999998 │
│     1 │ 250000000000 │       1 │  999999 │
└───────┴──────────────┴─────────┴─────────┘
```
#### filter(condition)
```python
import duckdb
rel = duckdb.sql("SELECT * FROM range(1_000_000) tbl(id)")
rel.filter("id > 5").limit(3).show()

┌───────┐
│  id   │
│ int64 │
├───────┤
│     6 │
│     7 │
│     8 │
└───────┘
```
#### join(rel, condition, type='inner')
```python
import duckdb
r1 = duckdb.sql("SELECT * FROM range(5) tbl(id)").set_alias("r1")
r2 = duckdb.sql("SELECT * FROM range(10, 15) tbl(id)").set_alias("r2")
r1.join(r2, "r1.id + 10 = r2.id").show()

┌───────┬───────┐
│  id   │  id   │
│ int64 │ int64 │
├───────┼───────┤
│     0 │    10 │
│     1 │    11 │
│     2 │    12 │
│     3 │    13 │
│     4 │    14 │
└───────┴───────┘
```
#### limit(n, offset=0)
```python
import duckdb
rel = duckdb.sql("SELECT * FROM range(1_000_000) tbl(id)")
rel.limit(3).show()

┌───────┐
│  id   │
│ int64 │
├───────┤
│     0 │
│     1 │
│     2 │
└───────┘
```
#### order(expr)
```python
import duckdb
rel = duckdb.sql("SELECT * FROM range(1_000_000) tbl(id)")
rel.order("id DESC").limit(3).show()

┌────────┐
│   id   │
│ int64  │
├────────┤
│ 999999 │
│ 999998 │
│ 999997 │
└────────┘
```
#### project(expr)
对每一行执行映射
```python
import duckdb
rel = duckdb.sql("SELECT * FROM range(1_000_000) tbl(id)")
rel.project("id + 10 AS id_plus_ten").limit(3).show()

┌─────────────┐
│ id_plus_ten │
│    int64    │
├─────────────┤
│          10 │
│          11 │
│          12 │
└─────────────┘
```
#### union(rel)
上下 union 两个 relation, 且 两个 relation 必须具备相同数量的 columns
```python
import duckdb
r1 = duckdb.sql("SELECT * FROM range(5) tbl(id)")
r2 = duckdb.sql("SELECT * FROM range(10, 15) tbl(id)")
r1.union(r2).show()

┌───────┐
│  id   │
│ int64 │
├───────┤
│     0 │
│     1 │
│     2 │
│     3 │
│     4 │
│    10 │
│    11 │
│    12 │
│    13 │
│    14 │
└───────┘
```
### conversion
relation object 也可以转换为其它格式的数据
```python
import duckdb
duckdb.sql("SELECT 42").fetchall()   # Python objects
duckdb.sql("SELECT 42").df()         # Pandas DataFrame
duckdb.sql("SELECT 42").pl()         # Polars DataFrame
duckdb.sql("SELECT 42").arrow()      # Arrow Table
duckdb.sql("SELECT 42").fetchnumpy() # NumPy Arrays
```
### 序列化
```python
import duckdb
duckdb.sql("SELECT 42").write_parquet("out.parquet") # Write to a Parquet file
duckdb.sql("SELECT 42").write_csv("out.csv")         # Write to a CSV file
duckdb.sql("COPY (SELECT 42) TO 'out.parquet'")      # Copy to a Parquet file
```

# DuckDBPyConnection API
* begin() => 开始一个 transaction
* commit() => commit changed performed within a transaction
* close() => close 当前 connection
* dtype(type_str)-> DuckDBPyType => 通过传入的字符串，返回对应的 duckdb type
* execute(query, parameters:Object = None) -> DuckDBPyConnection => 执行 given sql query, 有可能需要在使用 prepared statement 的情况下传入 参数
* df() => 当前 connection 在 execute 了 query 后，通过已下方法来获取查询结果，df() 是将结果作为 DataFrame 来获取
* fetch_df()
* fetchall() => 结果作为 python object 来获取，且获取所有结果
* fetchone() => 结果作为 python object 来获取，且获取一个，获取后，cursor 会往后移动
* fetchmany(size:int = 1) => 结果作为 python object 来获取，且获取 当前 cursor 之后的 size 行数据
* from_csv_auto(path) -> DuckDBPyRelation => 读取 csv, 返回一个 relation object
* read_csv()
* from_parquet() -> DuckDBPyRelation
* read_parquet()
* from_query(query) -> DuckDBPyRelation => 执行 sql query, 如果是 select 语句，则返回  DuckDBPyRelation
* query(query) => 和 from_query 一样
* sql(query) => 和 query 一样
* get_table_names(query)-> set[str] => 根据指定的 query 提取出需要的 table names
* table(table_name) -> DuckDBPyRelation => 对指定的 table 创建一个 DuckDBPyRelation
* view(view_name) -> DuckDBPyRelation => 对指定的 view 创建一个 DuckDBPyRelation
* values(values:Object) -> DuckDBPyRelation => 根据传入的 values 创建 DuckDBPyRelation

# DuckDBPyRelation API
* columns => columns 属性
* close() => close 当前 relation
* count(column: str, groups: str = '')
* create(table_name: str) -> None => 根据当前 relation 的内容，创建一个 table
* create_view(view_name: str) -> DuckDBPyRelation
* describle() -> DuckDBPyRelation => 做基础的统计，以及每一列是否包含 null value
* description => description 属性
* dtypes => dtypes 属性，表示所有列的 data type
* execute() -> DuckDBPyRelation => 执行所有 transforma
* df() => execute and fetch all rows as pandas DataFrame
* featchall() -> list
* fetchone() -> Optional[tuple]
* fetchmany(size=1)
* filter(filter_expr)
* insert(values:Object) -> None => insert values to current relation
* insert_into(table_name) -> None => insert relation to table
* join(other_rel, condition: object, how: str='inner')
* limit(n: int, offset=0) -> DuckDBPyRelation
* list(column: str) -> DuckDBPyRelation => return a list contain all values in a given column
* project()
* query()
* set_alias(alias: str) -> DuckDBPyRelation => 给当前 relation 设置 alias
* show()
* order(expr)
* sort(expr)
* to_csv(file_name)
* to_df() => same as df()
* write_csv()
* wrrite_parquet()
* to_table(table_name) => 和 create(table_name) 一样
* types => 属性 types, 显式所有列的 type