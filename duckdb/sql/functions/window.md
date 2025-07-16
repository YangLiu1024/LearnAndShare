# Window Functions
window functions 本质上就是把数据分成多个 partition, 每个 partition 可以被 order by, 最后对每一行，可以基于当前行以及附件的行，计算一个新的值。  
如果没有指定 partition, 则整个数据被当作当个 partition, 如果没有 order by, 则使用 nature order.  
需要注意的是，window function 不能访问 自己 partition 之外的行。  

## Framing
frame 是定义当对每一行计算新值时，需要用到那些行，当没指定 order by 时，默认是整个 partition。当指定了 order by 时，默认是 *RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW*, 即从 partition 开始到当前行。
### ROWS Framing
可以通过 ROWS 来指定 frame
```sql
-- sum 作用于上一行，当前行，和下一行
SELECT points,
    sum(points) OVER (
        ROWS BETWEEN 1 PRECEDING
                 AND 1 FOLLOWING) AS we
FROM results;
```
当处于 partition 边界时，frame 会被截断，边界外的数据是不能被访问的。
### RANGE Framing
有的时候不能通过 行数来确定 frame, 而需要通过值的范围，这就需要使用 range between
```sql
-- 当前值的三天前，以及当前值的三天后
SELECT "Plant", "Date",
    avg("MWh") OVER (
        PARTITION BY "Plant"
        ORDER BY "Date" ASC
        RANGE BETWEEN INTERVAL 3 DAYS PRECEDING
                  AND INTERVAL 3 DAYS FOLLOWING)
        AS "MWh 7-day Moving Average"
FROM "test"
ORDER BY 1, 2;
```
## Exclude
exclude 可以排除掉当前行或者当前行附近的行
```sql
-- 统计10天前，10天后的数据，但是排除掉当前行
SELECT
    event,
    date,
    athlete,
    avg(time) OVER w AS recent,
FROM results
WINDOW w AS (
    PARTITION BY event
    ORDER BY date
    RANGE BETWEEN INTERVAL 10 DAYS PRECEDING AND INTERVAL 10 DAYS FOLLOWING
        EXCLUDE CURRENT ROW
)
ORDER BY event, date, athlete;
```
EXCLUDE 支持四种参数
* EXCLUDE CURRENT ROW, 排除当前行
* EXCLUDE GROUP
* EXCLUDE TIES, 排除掉所有 peer rows, 但是不排除当前行
* EXCLUDE NO OTHERS, 默认设置，不排除任何行
## Window
有的时候，我们希望基于相同的 window 进行不同的操作，那么 定义的window就希望能被重用
```sql
SELECT "Plant", "Date",
    min("MWh") OVER seven AS "MWh 7-day Moving Minimum",
    avg("MWh") OVER seven AS "MWh 7-day Moving Average",
    max("MWh") OVER seven AS "MWh 7-day Moving Maximum"
FROM "Generation History"
WINDOW seven AS (
    PARTITION BY "Plant"
    ORDER BY "Date" ASC
    RANGE BETWEEN INTERVAL 3 DAYS PRECEDING
              AND INTERVAL 3 DAYS FOLLOWING)
ORDER BY 1, 2;
``` 