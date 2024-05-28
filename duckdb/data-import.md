# Data Import
duckdb 支持 load 很多类型的数据

## csv
duckdb 可以直接从 csv 文件里load 数据, 
```sql
# test.csv 是文件路径
select * from 'test.csv'

# 也支持 read_csv 函数，该函数支持配置一些 parsing 参数
select * from read_csv('test.csv', delim=',', header=True, columns={'C1': 'Date', 'C2': 'VARCHAR'})

# 也可以直接从 csv 文件创建 table
create table test as select * from 'test.csv'

# select  也是可以省略的
create table test as from 'test.csv'

```
从 query/table 到 csv 文件也是很方便的
```sql
# 从 query 到 csv
COPY (SELECT * from test) TO 'dest.csv' WITH (HEADER true, DELIMITER '|')

# 如果 序列化整个 table, 可以直接 copy
COPY test TO 'dest.csv' WITH (HEADER true, DELIMITER '|')
```
csv 的 loading 是自动且智能的，如果有一些自定义行为，可以通过 *read_csv* 函数进行配置

## parquet
parquet 和 csv 的处理很类似，比如 read_parquet 和 read_csv.  
比较特殊的是 read_parquet 支持加载多个 parquet 文件
```sql
# 加载多个文件到同一个 table
SELECT * FROM read_parquet(['file1.parquet', 'file2.parquet', 'file3.parquet']);

# 加载 test folder 下面所有的 parquet 文件
SELECT * FROM 'test/*.parquet';

# filename 会在最后生成的 table 里面加上 filename column, 该列的值是 file anme
SELECT * FROM read_parquet('test/*.parquet', filename = true);
```
从 table/query 到 parquet 文件和 csv 类似
```sql
# 从 query 到 parquet
COPY
    (SELECT * FROM tbl)
    TO 'result-snappy.parquet'
    (FORMAT 'parquet');


# 从 table 到 parquet
COPY
    tbl
    TO 'result-snappy.parquet'
    (FORMAT 'parquet');

# 直接从 csv 到 parquet 也是可以的
COPY 'test.csv' TO 'dest.parquet' (FORMAT PARQUET);
```