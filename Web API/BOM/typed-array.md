# TypedArray
TypedArray 其实就是对底层二进制数据块的一种以特定类型来解读的视图
* Int8Array => -128~127, 1 字节大小，8 位有符号数，用补码
* Uint8Array => 0~255, 8 位无符号数
* Int16Array => -2^15 ~ 2^15 - 1, 2 个字节，16 位有符号数，补码
* Uint16Array
* Int32Array
* Uint32Array
* Float32Array
* Float64Array
* BigInt64Array
* BigUint64Array

所有的类型化数组都是基于 Arraybuffer 来操作的，当使用 多字节类型数组时，总是会根据平台的字节顺序来确定使用大端序(从最高位到最低位)还是小端序(从最低位到最高位)。  
如果想在缓冲区 读取、写入时指定字节顺序，需要使用 *DataView*.  
当对这些类型化数组写入数据时，如果数据超出范围，将会被截断

```js
new TypedArray()
new TypedArray(length) // 创建指定元素个数 的类型数组，其 byteLength 会是 length * BYTES_PER_ELEMENT
new TypedArray(typedArray) // 拷贝原数组的数据到新的类型数组，每个值在copy 时都转换为新的类型。新数组的长度和原数组相同
new TypedArray(object) // 等同于 TypedArray.from(obj)

new TypedArray(buffer) // 创建新的类型数组，该数组依赖于传入的 array buffer
new TypedArray(buffer, byteOffset) // byteOffset 和 length 指定类型化数组视图将暴露的内存范围，默认值为整个 buffer
new TypedArray(buffer, byteOffset, length) // 如果仅忽略 length, 则是从 byteOffset 开始的剩余部分的视图

// 这里，ArrayBuffer 的字节大小必须是 类型数组元素字节大小的整数倍，否则会抛出错误
const a = new Int16Array(new ArrayBuffer(16))
a.length // 8, Int16 使用 2个字节表示一个元素，所以 16 个字节有 8 个元素
a.byteLength // 16
```

## 静态方法
* TypedArray.from(obj: ArrayLike, mapFn) => 类似于 Array.from()，从类数组对象里创建类型数组. ArrayLike 包括：一个拥有 length 属性，和若干索引属性的任意对象，或者一个可迭代对象
* TypedArray.of(...elements) => 类似于 Array.of()
## 实例属性
* buffer => 类型数组底层引用的 array buffer 引用
* byteLength => 字节大小
* length => 数组中保存的元素个数
* byteOffset => 类型化数组距离 ArrayBuffer 起始位置的偏移量
## 实例方法
* at(index) => 通过索引 index 访问对应 元素，接受负数
* entries() => 返回数组元素的键值对迭代器

# 注意事项
* 在类型数组上设置或获取索引属性不会在原型链中搜索此属性，即使索引已经越界。索引属性将查询底层的 array buffer, 并且永远不会查看对象属性。
* 在使用 buffer 构造类型数组时，buffer 的字节大小必须是类型数组元素字节大小的整数倍，且 byteOffset 也必须是元素字节大小的整数倍