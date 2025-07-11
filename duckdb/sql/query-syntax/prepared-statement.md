# Prepared Statement
duckdb 支持 prepared statements, 有三种方式，*?*, *$1*, *$parameter*
```sql
-- 声明一个 prepare 使用 Prepare
PREPARE query_person as select * from person where starts_with(name, ?) and age >= ?;

-- 查询 名字以 B 开头的，年纪 大于 40 的人
execute query_person('B', 40)

-- 除了使用 ?， 还可以使用 positional parameter
PREPARE query_person as select * from person where starts_with(name, $2) and age >= $1;
execute query_person(40, 'B')

-- 还可以使用 named parameter
PREPARE query_person as select * from person where starts_with(name, $name_parameter) and age >= $age_parameter;
execute query_person(name_parameter := 'B', age_parameter:= 40)


-- prepared statement 是需要 drop 的 
DEALLOCATE query_person
```