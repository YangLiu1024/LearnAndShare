# File
File 对象是一个特殊类型的 blob, 通常 File 对象来自于一个 *<input>* 元素上选择文件后返回的 FileList 对象。  
File 对象没有任何方法

## 实例属性
* lastModified => 最后修改时间，毫秒数
* lastModifiedDate => 返回 Date 对象
* name => 文件名字
* size => 文件大小
* type => 文件 MIME 类型，不能确定时，为 空字符串
* webkitRelativePath => 文件相关的 path 或 URL

```js
// bits 和 Blob 一样，是 ArrayBuffer, TypedArray, Blob, DataView, string 的组合
// name 是文件名字或者路径
// options 包含 type, lastModified 属性
new File(bits, name, options)
```

## FileList
FileList 对象通常来自于 *<input>* 元素的 *files* 属性
```js
<input id="fileItem" type="file">

var file = document.getElementById("fileItem").files[0];
```
## 实例属性
* length => 返回列表中文件数量
## 实例方法
* item(index) => files.item(1) 等价于 files[1]

## 使用拖放来选择文件
1. 定义一个 接收 drop 事件的区域
2. 响应 drop 事件
```js
function onDrop(e) {
    const dt = e.dataTransfer
    const files = dt.files
}
```

## 显示用户选择的图片的缩略图
```js
declare const files: FileList
// 通过 URL.createObjectURL
for (let i = 0; i< files.length; i++>) {
    const img = document.createElement('img')
    img.src = URL.createObjectURL(files[i])
    img.height = 60;
    img.onload = () => {
        URL.invokeObjectURL(img.src)
    }
}

// 使用 FileReader
for (let i = 0; i< files.length; i++>) {
    const img = document.createElement('img')
    img.file = file;
    img.height = 60;
    const fr = new FileReader()
    fr.onload = (e) => {
        img.src = e.target.result
    }
    fr.readAsDataURL(file)
}
```

## 上传用户选择文件
将文件上传到服务器
```js
function uploadFile(file:File) {
    const reader = new FileReader();
    const xhr = new XMLHttpRequest();

    xhr.upload.addEventListener('progress', (e) => {
        // 监听 progress 事件，可以计算 已传输大小和 total 的比例
    })

    xhr.upload.addEventListener('load', () => {
        // 当上传已经完成
    })

    xhr.open('POST', 'http://*:8080/*')// 发起请求

    xhr.overrideMimeType('text/plain; chartset=x-user-defined-binary')

    // 在 file reader 结束读取时，发送文件
    reader.onload = (e) => {
        xhr.send(e.target.result)
    }

    reader.readAsBinaryString(file)
}
```