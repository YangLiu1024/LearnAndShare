# regexp functions
duckdb 支持多种正则函数来处理字符串
## regexp_extract(string, pattern, group = 0, options)
group = 0 表示返回 整个 pattern 所 match 的字符串。如果 pattern 本身分了 group, 则可以传入 group = 1, group = 2 之类的返回特定 group 的内容
```js
duckdb> select regexp_extract('ab1dasdsfab2d', '(ab\dd).*(ab\dd)', 0) as i;
┌───────────────┐
│ i             │
╞═══════════════╡
│ ab1dasdsfab2d │
└───────────────

duckdb> select regexp_extract('ab1dasdsfab2d', '(ab\dd).*(ab\dd)', 2) as i;
┌──────┐
│ i    │
╞══════╡
│ ab2d │
└──────┘
```

## regexp_extract(string, pattern, name_list, options)
支持将内容按照 groups 和 name_list 一一对应的方式返回
```js
duckdb> select regexp_extract('2023-04-15', '(\d+)-(\d+)-(\d+)', ['y', 'm', 'd']) as d;
┌─────────────────────────┐
│ d                       │
╞═════════════════════════╡
│ {y: 2023, m: 04, d: 15} │
└─────────────────────────┘

duckdb> SELECT regexp_extract('2023-04-15 07:59:56', '^(\d+)-(\d+)-(\d+) (\d+):(\d+):(\d+)', ['y', 'm', 'd', 'h']) as t;
┌────────────────────────────────┐
│ t                              │
╞════════════════════════════════╡
│ {y: 2023, m: 04, d: 15, h: 07} │
└────────────────────────────────┘
```

## regexp_extract_all(string, pattern, group = 0, options)
字符串可能包含多个不重叠的 part 满足指定的 pattern, 需要将不同 part 都提取出来
```js
duckdb> select regexp_extract_all('ab1dasdsfab2d', 'ab\dd', 0) as i;
┌──────────────┐
│ i            │
╞══════════════╡
│ [ab1d, ab2d] │
└──────────────┘

duckdb> select regexp_extract_all('2023-04-15', '(\d+)') as d;
┌────────────────┐
│ d              │
╞════════════════╡
│ [2023, 04, 15] │
└────────────────┘

duckdb> select regexp_extract_all('Peter: 33, Paul:14', '(\w+):\s*(\d+)', 2) as i;
┌──────────┐
│ i        │
╞══════════╡
│ [33, 14] │
└──────────┘
```
## regexp_full_match(string, pattern, options)
check 是否整个字符串匹配指定 pattern，等价于给 pattern 加了 *^* 和 *$*.  
其效果和 SIMILAR TO 相同
```js
// 需要整个字符串满足指定 pattern
duckdb> select regexp_full_match('anabanana', 'ana') as i;
┌───────┐
│ i     │
╞═══════╡
│ false │
└───────┘
// 即使部分满足，也不行
duckdb> select regexp_extract_all('anabanana', 'ana') as i;
┌────────────┐
│ i          │
╞════════════╡
│ [ana, ana] │
└────────────┘

duckdb> select regexp_full_match('anabanana', 'ana.*') as i;
┌──────┐
│ i    │
╞══════╡
│ true │
└──────┘
```
## regexp_matches(string, pattern, options)
和 regexp_full_match 相比，regexp_matches 则只需要字符串包含匹配模式的字符串就行
```js
duckdb> select regexp_matches('anabanana', 'ana') as i;
┌──────┐
│ i    │
╞══════╡
│ true │
└──────┘
```
## regexp_replace(string, pattern, replacement, options)
把匹配 pattern 的字符串替换为指定字符串，默认只替换第一个匹配部分，可以使用参数 *g* 来替换所有
```js
duckdb> select regexp_replace('anabanana', 'ana', '-') as i;
┌─────────┐
│ i       │
╞═════════╡
│ -banana │
└─────────┘

duckdb> select regexp_replace('anabanana', 'ana', '-', 'g') as i;
┌───────┐
│ i     │
╞═══════╡
│ -b-na │
└───────┘

// a|b a 优先
duckdb> SELECT regexp_replace('abc', '(b|c)', 'X') as i;
┌─────┐
│ i   │
╞═════╡
│ aXc │
└─────┘

// replacement 可以使用 group \0,\1,\2 等
duckdb> SELECT regexp_replace('abc', '(b|c)', '\1\1\1\1') as i;
┌────────┐
│ i      │
╞════════╡
│ abbbbc │
└────────┘
```
## regexp_split_to_array(string, pattern, options)
把字符串根据指定 pattern split
```js
duckdb> select regexp_split_to_array('anabanana', 'ana') as i;
┌───────────┐
│ i         │
╞═══════════╡
│ [, b, na] │
└───────────┘
```

### options
* c, 表示为大小写敏感
* i, 表示为大小写不敏感
* l, literal, 表示匹配字符本身，而不是正则表达式
* g, 仅在 regexp_replace 中有效，表示全局替换
* s, 表示对换行符不敏感
* m, n, p, 对换行符敏感, 即 *.* 不可以匹配换行符
```js
// 大小写不敏感
duckdb> SELECT regexp_matches('abcd', 'ABC', 'i') as i;
┌──────┐
│ i    │
╞══════╡
│ true │
└──────┘

// 把 pattern 当作字符，而不是正则表达式来匹配
duckdb> SELECT regexp_matches('ab^/$cd', '^/$', 'l') as i;
┌──────┐
│ i    │
╞══════╡
│ true │
└──────┘

// 换行符的匹配
duckdb> SELECT regexp_matches(E'hello\nworld', 'hello.world', 'p') as i;
┌───────┐
│ i     │
╞═══════╡
│ false │
└───────┘
```