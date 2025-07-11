integer 类型会有 对应 的 uinteger 类型，即 对应的 unsigned 类型
| Names | Alias | Description 
|-------|-------|-------------
| INTEGER     | INT,INT4,SIGNED     | signed 4 bytes integer           
| SMALLINT    | INT2,SHORT          | siged two bytes integer           
| TINYINT     | INT1                | signed one-byte integer
| BIGINT      | INT8, LONG          | signed 8 bytes integer           
| HUGEINT    |         | siged 16 bytes integer              
| Blob | BINARY | variable-length binary data
| BOOLEAN | BOOL | logical boolean
| DOUBLE | FLOAT8 | 8 bytes double precision floating number
| REAL | FLOAT4, FLOAT | 4 bytes single precision floating number
| VARCHAR | CHAR,TEXT, STRING | variable-length character string


# python type -> duckdb type
Built-in types	| DuckDB type
|----------|---------------
bool	| BOOLEAN
bytearray |	BLOB
bytes	| BLOB
float	| DOUBLE
int	| BIGINT
str	| VARCHAR
None | NULL

# numpy Dtypes -> duckdb type
Type| 	DuckDB type
|----------|---------------
bool|	BOOLEAN
float32|	FLOAT
float64	| DOUBLE
int16	| SMALLINT
int32	| INTEGER
int64	| BIGINT
int8	| TINYINT
uint16	| USMALLINT
uint32	| UINTEGER
uint64	| UBIGINT
uint8	| UTINYINT
