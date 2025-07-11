# Values
values 用来生成行，有几个值，就对应着几列。生成的行可以用在多个地方，比如 insert
```sql
INSERT INTO cities
VALUES ('Amsterdam', 1), ('London', 2);

-- 还可以直接把 values 当作 table 来使用
SELECT *
FROM (VALUES ('Amsterdam', 1), ('London', 2)) cities(name, id);
```