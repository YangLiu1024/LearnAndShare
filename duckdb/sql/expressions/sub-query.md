# sub query
sub query 一般和 select... from 绑在一起， *pivot* 结果也可以被视作 sub query. 当 sub query 只返回一个值，还可以被当作 value 使用
```sql
-- (SELECT min(grade) FROM grades) 只返回一个值，所以可以在这里被当作 value 直接使用
SELECT course FROM grades WHERE grade = (SELECT min(grade) FROM grades);
```
那如果一个 sub query 返回多个值，也想被当作一个 value 使用呢？就可以使用 *ALL*, *SOME*, *ANY*. any 和 some 是等价的
```sql
SELECT 6 <= ALL (SELECT grade FROM grades) AS adequate;

SELECT 8 >= ALL (SELECT grade FROM grades) AS excellent;

SELECT 5 >= ANY (SELECT grade FROM grades) AS fail;
```
## exists
exists 用来 check sub query 是否有结果, 当有至少一行时，返回 true, 否则返回 false
```sql
SELECT EXISTS (FROM grades WHERE course = 'Math') AS math_grades_present;
```