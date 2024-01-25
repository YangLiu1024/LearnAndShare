# FileReader
允许异步读取文件或者数据缓冲区的内容，使用 File 或者 Blob 对象指定要读取的文件或者数据。  
这里的 File 通常来自于 *<input type='file'>* 元素选择的文件

## 实例属性
* error => 读取数据时发生的错误
* readyState => 0(EMPTY), 表示还没有加载任何数据， 1(LOADING), 表示正在加载数据， 2(DONE), 表示加载结束，可能是正确完成，也可能是被中断
* result => 加载结果，在读取操作完成后才有效

## 事件处理
* onload => 数据加载完成时触发
* onerror => 读取发生错误时触发
* onabort => 读取操作被中断时触发

## 实例方法
* readAsArrayBuffer(blob) => 开始读取数据，完成时，result 属性是 文件对应的 array buffer
* readAsBinaryString(blob) => 完成时，result 是数据的二进制
* readAsDataURL(blob) => 完成时，result 属性是一个 data:URL 格式的 base64 字符串
* readAsText(blob) => 完成时，result 属性是一个字符串
* abort() => 中断当前读取 

```js
// original: 11.csv
// A,B,C,D,E
// 1,1,1,0,
// 1,2,3,90,
// 5,10,5,both,
// ,1,,,
// 0.5,0.5,7,0,
// 2.5,2.5,1,90,
// 2.5,5,,both,
// 12.5,25,,,
// 1.25,1.25,7,,
// 6.25,6.25,9,0,
// 6.25,12.5,11,90,

// readAsDataURL
//data:text/csv;base64,QSxCLEMsRCxFDQoxLDEsMSwwLA0KMSwyLDMsOTAsDQo1LDEwLDUsYm90aCwNCiwxLCwsDQowLjUsMC41LDcsMCwNCjIuNSwyLjUsMSw5MCwNCjIuNSw1LCxib3RoLA0KMTIuNSwyNSwsLA0KMS4yNSwxLjI1LDcsLA0KNi4yNSw2LjI1LDksMCwNCjYuMjUsMTIuNSwxMSw5MCw=

// readAsText => same as original text

// readAsBinaryString => same as original text

// readAsArrayBuffer => 返回一个 array buffer, 155 byte length

// URL.createObjectURL(file)
// 该链接只在当前 document 上下文里有效
// blob:https://053be9dc-9043-41f6-a0df-21979b8968d3.mdnplay.dev/c2aeac1c-c3ff-404a-aee9-c7798b46f17b
```
