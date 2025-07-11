# update
通常 update table 本身比较简单
```js
UPDATE tbl
SET i = 0
WHERE i IS NULL;
```
有的时候需要 update from other table
```js
UPDATE city
SET revenue = revenue + 100
FROM country
WHERE city.country_code = country.code
  AND country.name = 'France';
```