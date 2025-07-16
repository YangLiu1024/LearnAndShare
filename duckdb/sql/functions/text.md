# Text Functions
字符串处理是最常见的功能
## string[index], array_extract(string, index)
提取索引为 index 的字符，*索引是 1-based*
```js
duckdb> select 'abc'[2] as i;
┌───┐
│ i │
╞═══╡
│ b │
└───┘
```
## string[begin:end], array_slice(string, start, end)
类似于 slice, begin 和 end 缺失的话，默认为 beginning 和 end, end 可以为负数
```js
duckdb> select 'abc'[:2] as i;
┌────┐
│ i  │
╞════╡
│ ab │
└────┘
```
## string LIKE pattern, string SIMILAR TO regex
字符串模式匹配，返回 true or false
## starts_with(string, search)
判断 string 是否由 search 开头，search 是 plain text, 不是正则表达式，alias *^@*
```js
duckdb> select starts_with('.bc', '.b') as i;
┌──────┐
│ i    │
╞══════╡
│ true │
└──────┘

duckdb> select 'abc' ^@ '.b' as i;
┌───────┐
│ i     │
╞═══════╡
│ false │
└───────┘
```
## contains(string, search), ends_with(string, search)
和 starts_with 类似
## arg1 || arg2
拼接两个参数，如果有任意参数为 NULL, 则结果为 NULL. 类似于 concat(value, ...)
```js
duckdb> select 'a' || 'b' as i;
┌────┐
│ i  │
╞════╡
│ ab │
└────┘
```
## base64(blob)
把 blob 对象转为 base64 字符串
## greatest(arg1, ...)
返回最大的字符串, least 返回最小的
## hash(value, ...)
返回 一个 UBIGINT value
## instr(string, search), position, strpos
返回 search 字符串的位置
```js
duckdb> select instr('abcdef', 'cde') as i;
┌───┐
│ i │
╞═══╡
│ 3 │
└───┘

// 使用 position
duckdb> select position('b' IN 'abc') as i;
┌───┐
│ i │
╞═══╡
│ 2 │
└───┘
```
## left(string, count)
提取 left-most count 字符，和 right 类似
## len(string)， length
返回字符串字符数
## lower(string), lcase
返回 lower case 的字符串
## ltrim(string，characters = space)
字符串左边去空， 和 rtrim 类似
## parse_dirname(path, separator = system)
文件路径操作
```js
// parse_dirname 返回最顶层目录名
duckdb> select parse_dirname('C:\Localdata\test_cases') as d;
┌────┐
│ d  │
╞════╡
│ C: │
└────┘

// 返回上一级路径
duckdb> select parse_dirpath('C:/Localdata/test_cases') as d;
┌──────────────┐
│ d            │
╞══════════════╡
│ C:/Localdata │
└──────────────┘
// 返回最后一级 component 名
duckdb> select parse_filename('C:/Localdata/test_cases/case1.csv') as d;
┌───────────┐
│ d         │
╞═══════════╡
│ case1.csv │
└───────────┘

// 返回路径的每一级
duckdb> select parse_path('C:/Localdata/test_cases/case1.csv') as d;
┌────────────────────────────────────────┐
│ d                                      │
╞════════════════════════════════════════╡
│ [C:, Localdata, test_cases, case1.csv] │
└────────────────────────────────────────┘
```
## repeat(string, count)
把字符串重复多次
## replace(string, source, target)
把所有 source 替换为target
## reverse(string)
把字符串反向