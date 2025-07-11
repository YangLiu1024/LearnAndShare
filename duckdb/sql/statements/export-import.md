# Export
数据库可以被导出到一个目录，export 和 copy 有点像，copy 也支持 copy ... to ...
```sql
-- 从数据源 copy 数据到 table
copy data from 'data.csv'

-- 从 table copy 到文件
copy data to 'data.csv' (FORMAT CSV)

-- copy 还支持 partition, 将 table 以 hive 的形式导出到指定目录
copy data to 'datas' (FORMAT CSV, PARTITION_BY (YEAR, MONTH))

-- copy 还支持将一个 attach 的 数据库一整个 copy 到另一个 attach 的 数据库
copy from database db1 to db2
```
export 的作用在于把整个 database 导出到指定 目录下, 而不是某个 table 或者 selection
```sql
-- 把当前使用的 database 导出到 data 目录, 包括数据，schema 等等一切信息
-- 如果有多个 database, 比如通过 attach 导入的，可以通过 use name 来使用指定 db
export database 'data' (FORMAT PARQUET)

-- 导出的 数据库可以通过 import 再导入
-- 或者执行 data 目录下面的 schema.sql 以及 load.sql 来导入 schema 和 数据
import database 'data'
```