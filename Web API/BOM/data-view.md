# DataView
DataView 是一个可以从底层二进制数据(ArrayBuffer)读写多种数值类型的底层接口，使用它时，DataView 提供接口设置以什么字节序读取数据，而不考虑不同平台的字节序。

```js
// buffer 是一个 ArrayBuffer 或者 SharedArrayBuffer
new DataView(buffer)
new DataView(buffer, byteOffset)
new DataView(buffer, byteOffset, byteLength)
```

## 实例属性
* buffer => 返回底层使用的 array buffer
* byteLength => view 所绑定的字节长度, 通常和 buffer.byteLength 相同，但也可能被构造参数 byteOffset,byteLength 改变
* byteOffset => view 在创建时，相对于底层 array buffer 的偏移量
## 实例方法
* getInt16(byteOffset, littleEndian?) => 在 byteOffset 处读取两个字节，以 Int16 的格式读取，littleEndian 表示是否以 低位到高位的顺序的字节序，如果不指定，默认值为 false
* setInt16(byteOffset, value, littleEndian?) => 在 byteOffset 处以 Int16 的格式写入一个 value 


## 固定宽度数值转换
TypedArray 和 DataView 在进行二进制数据类型转换时，首先会截取掉小数部分，然后取整数二进制补码的最低几位来表示
```js
const buffer = new ArrayBuffer(16);
const view = new DataView(buffer);
// 200 的 二进制表示为  0011001000，因为是正数，其补码就是原码
// 那么 view 就会写入 11001000
view.setInt8(1, 200); 
// 在读取的时候，因为是按照 int8 的格式来读取的，那么就会这个位置存的是补码
// 那么表示该值是一个负数，然后取反加 1 之后 变成 10111000 => 该数值为 -56
console.log(view.getInt8(1));// -56
```