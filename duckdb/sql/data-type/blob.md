# Blob
blob 就是 binary large object, 用于存放任意的二进制数据
```js
duckdb> SELECT 'AB'::BLOB;
┌────────────┐
│ 'AB'::BLOB │
╞════════════╡
│ 4142       │
└────────────┘

duckdb> SELECT '\xAA\xAB\xAC'::BLOB;
┌──────────────────────┐
│ '\xAA\xAB\xAC'::BLOB │
╞══════════════════════╡
│ aaabac               │
└──────────────────────┘
```