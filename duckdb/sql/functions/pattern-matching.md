# Pattern Matching
duckdb 支持多种模式匹配
## LIKE
LIKE 是最古老的 SQL sytax, 它总是 match 整个字符串. *%* 用来匹配任意字符任意次，*_* 用来匹配字符一次。如果 pattern 没有带 百分号和下划线，则该 pattern 表示的就是字符串本身，LIKE pattern 则等价于 equals
```sql
SELECT 'abc' LIKE 'abc';    -- true
SELECT 'abc' LIKE 'a%' ;    -- true
SELECT 'abc' LIKE '_b_';    -- true
SELECT 'abc' LIKE 'c';      -- false
SELECT 'abc' LIKE 'c%' ;    -- false
SELECT 'abc' LIKE '%c';     -- true
SELECT 'abc' NOT LIKE '%c'; -- false

-- 为了不区分 大小写，可以使用 ILIKE
SELECT 'abc' ILIKE '%C'; -- true
SELECT 'abc' NOT ILIKE '%C'; -- false
```
## SIMILAR TO
*SIMILAR TO* 和 LIKE 很像，区别在于 similar to 是使用 正则表达式来进行模式匹配，相同在于，similar to 也总是 match 整个字符串。  
```sql
SELECT 'abc' SIMILAR TO 'abc';       -- true
SELECT 'abc' SIMILAR TO 'a';         -- false
SELECT 'abc' SIMILAR TO '.*(b|d).*'; -- true
SELECT 'abc' SIMILAR TO '(b|c).*';   -- false
SELECT 'abc' NOT SIMILAR TO 'abc';   -- false
```
## Globbing
Globbing 最早是用作使用 模式匹配 file name，后面则演变为一种 glob-style 的字符串模式匹配规则。  
GLOB 使用 *？* 表示匹配任意单字符，*\** 表示匹配任意字符任意次，*[]* 表示匹配括号内任意单字符，*[!]* 表示不匹配括号内任意字符。  
```sql
SELECT 'best.txt' GLOB '*.txt';            -- true
SELECT 'best.txt' GLOB '????.txt';         -- true
SELECT 'best.txt' GLOB '?.txt';            -- false
SELECT 'best.txt' GLOB '[abc]est.txt';     -- true
SELECT 'best.txt' GLOB '[a-z]est.txt';     -- true

-- [] 是大小写敏感
SELECT 'Best.txt' GLOB '[a-z]est.txt';     -- false
SELECT 'Best.txt' GLOB '[a-zA-Z]est.txt';  -- true

-- 使用 ！表示不匹配括号内任意字符
SELECT 'Best.txt' GLOB '[!a-zA-Z]est.txt'; -- false

-- 对 GLOB 取反
SELECT NOT 'best.txt' GLOB '*.txt';        -- false
```