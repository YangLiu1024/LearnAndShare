# FileReaderSync
FileReaderSync 允许以同步的形式来读取 File 或者 Blob 之中的内容。  
该接口只在 worker 里可用，因为在主线程里进行 IO 操作会阻塞 UI

## 实例方法
* readAsArrayBuffer(blob): ArrayBuffer => 读取文件，返回数据的 array buffer 对象
* readAsBinaryString(blob): string => 返回字符串
* readAsText(blob, encoding?): string
* readAsDataURL(blob): string