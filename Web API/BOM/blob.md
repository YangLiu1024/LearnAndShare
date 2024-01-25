# Blob
Blob(二进制大型对象) 是一种不可变的轻量级对象，可以有效存储数据块，使其适用于处理大型文件，例如 图像，音频文件。  
* Blob 可用于读取，写入，操作文件，从而在 web 中支持文件上传，下载，读取等任务
* 在处理二进制数据时非常有用，允许直接在 JS 中操作数据
* Blob 还可以在 web worker 中使用

```js
// datas 是一个可迭代对象，一般是数组，元素类型可以是
// ArrayBuffer, TypeArray, DataView, Blob, 字符串 或者这些元素的混合

// options 是可选参数，可以指定数据 MIME 类型
new Blob(datas, options)

const b = new Blob(['abc', [1, 2, 3], new Uint8Array([8,8,8])])
b.size // 11
b.text // 'abc1,2,3\b\b\b'
// 返回 Promise<ArrauBuffer>, ArrayBuffer 有 11 个字节，
//                     a  b  c  1  ,  2  ,  3  8 8 8
// 其 [[Unit8Array]]: [97,98,99,49,44,50,44,51,8,8,8]
// 字符串会转换为对应的字节码 utf-8?, 
// 数组里的数字会当作 十进制数转换为字节码, 但是逗号也会被存储
// Unit8Array 会直接当作字节码存储
b.arrayBuffer()
// 返回
// abc1,2,3\b\b\b, 这是因为 8 在字符集里，代表的是 backspace, \b
b.text()
```

## 属性
* size => 返回 blob 对象所包含数据的字节数
* type => 文件包含数据的 MIME 类型，默认值是空字符串。浏览器一般不会读取文件字节流来确定类型，而是通过文件后缀来进行推断

## 实例方法
* arrayBuffer(): Promise<ArrayBuffer> => 返回 blob 包含数据的 ArrayBuffer
* text(): Promise<string> => 返回 blob 包含数据对应的 utf-8 格式的字符串
* slice(start?, end?, contentType?): Blob
  - start 是开始字节的索引，该字节会被拷贝，默认为 0，如果是负数，则从末尾开始倒数
  - end 是第一个不会被拷贝的字节索引，默认值是原始长度 size
  - 赋给新的 blob 数据类型，默认值是空字符串
* stream(): ReadableStream => 通过返回流读取 blob 数据 

## 创建 object URL
blob 对象被用来创建 URL 链接，该链接只在当前 document 下有效。
```html
<html>
  <head>
    <title>Title of the Document</title>
  </head>
  <body>
    <script>
      let link = document.createElement('a');
      link.download = 'welcome.txt';
      let blob = new Blob(['Welcome to W3Docs'], {type: 'text/plain'});
      link.href = URL.createObjectURL(blob);
      link.click();
      URL.revokeObjectURL(link.href);      
    </script>
  </body>
</html>
```