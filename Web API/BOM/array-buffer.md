# ArrayBuffer
ArrayBuffer 是一个字节数组，存储纯粹的二进制数据，不能直接操作 ArrayBuffer 的内容，通常需要通过 TypeArray 或者 DataView 来操作，它们会将缓冲区中的数据表示为特定的格式，并通过这些格式来读写缓冲区的内容。  

```js
// 创建了一个可以存储 8 字节 的 array buffer
// options 包含了 maxByteLength 参数，表示缓冲区可调整到的最大大小
// 如果包含该参数，表示 ArrayBuffer 大小可调
new ArrayBuffer(byteLength, options)
```
ArrayBuffer 是一个 ***可转移对象***，可以直接在不同上下文之间进行转移，比如 web worker。在转移后，原始 array buffer 对象变成 detached 状态，意味着它不再可用。

## 静态方法
* ArrayBuffer.isView(buffer): boolean => 如果 buffer 是 TypeArray 之一，或者 DataView, 则返回 true, 否则 false
## 实例属性
* byteLength => 返回 buffer 字节数
* maxByteLength => 在构造时指定，不可改
* resizable => 如果指定了 maxByteLength, 则表示大小可调，返回 true
* detached => 表示是否已分离，不可用
## 实例方法
* resize(byteLength) => 调整大小
* slice(begin, end): ArrayBuffer => 返回指定范围的副本，begin 包含，end 不包含
* transfer() => 创建一个新的 ArrayBuffer 对象，其内容是当前内容的副本，然后分离当前缓冲区
* transferToFixedLength() => 和 transfer 类似，只是新建的 Arraybuffer 对象 大小不可改