# File System
允许 程序与用户本地设备，以及用户能够访问的网络文件系统上的文件进行交互。此 API 的核心功能包括读取文件，写入或保存文件以及访问目录结构。  
为了在 TS 中使用，需要安装对应的 types
```js
npm install --save @types/wicg-file-system-access
```

* window.showOpenFilePicker(), 显示文件选择器，成功后返回文件句柄
* window.showSaveFilePicker(), 显示文件选择器，成功后返回文件句柄
* window.showDirectoryPicker()，显示 folder 选择器，成功后返回 目录句柄

对常规文件的操作，总是会有很多适当的安全性检查来防止恶意内容被写入文件系统。所以这些写入都不是原地的，而是先写到一个临时文件，除非通过了所有的安全性检查，否则原文件不会被修改。  

# FileSystemHandle
## 属性
* name => 条目名字
* kind => 条目类型，file 或者 directory

## 方法
* isSameEntry() => 比对两个句柄是否指向同一个条目
* queryPermission() => 实验性 API, 查询当前条目的指定权限状态
* requestPermision() => 实验性 API, 请求指定权限
* remove() => 实验性 API, 请求删除条目


# FileSystemFileHandle
## 方法
* getFile() => 返回 *Promise<File>* 对象，该对象表示条目所代表的文件在磁盘上的状态
* createSyncAccessHandle() => 返回一个 *Promise<FileSystemSyncAccessHandle>* 对象，该对象支持同步读写文件，但只能在 web worker 里使用
* createWritable() => 返回一个 *Promise<FileSystemWritableFileStream>* 对象，可用于写入文件

### File
File 是一种特殊类型的 Blob
#### 属性
* lastModified => 最后修改时间的毫秒数
* lastModifiedDate => 最后修改时间的日期
* name => 文件名字
* type => 文件 MIME type
* size => 文件大小
* webkitRelativePath => File 相关的 path 或者 URL


# FileSystemDirectoryHandle
可以通过 *window.showDirectoryPicker()*, *window.storage.getDirectory()*，*FileSystemDirectoryHandle.getDirectoryHandle()* 这些方法获取。  
## 方法
* getDirectoryHandle(name) => 返回子目录 handle
* getFileHandle(name) => 返回 folder 下文件 handle
* removeEntry(name) => 删除指定 条目
* resolve() => 返回一个 包含从父目录前往指定子条目中间的目录的名称的数组，数组的最后一项是子条目的名称。如果不是当前父目录下的子条目，则返回 null

## 异步迭代器方法
* entries() => 返回一个迭代目录下面所有条目键值对(key: name, value: handle)的迭代器
* values() => 返回一个迭代目录下面所有条目的句柄的迭代器
* keys() => 返回一个迭代目录下面所有条目名字的迭代器

# FileSystemSyncAccessHandle
这个类只在处理 OPFS 上的文件专用的 web worker 可以使用。这使得这个类适用于重要的，大规模的文件更新，比如针对 SQLite 数据库进行更新。
## 方法
* flush() => 将通过 write() 写入的数据持久化在磁盘上
* close() => 关闭同步文件句柄，释放之前加在文件上的独占锁，禁止之后对其的任何操作
* getSize() => 返回与目标相关联的文件的字节大小
* read(buffer， {at: number}) => 将内容读取到指定缓冲区, 可指定偏移量，返回读取的字节大小
* truncate(size) => 将文件大小调整为指定大小
* write(buffer, {at: number}) => 将 数据写到指定位置，返回写入的字节大小

# FileSystemWritableFileStream
## 方法
* write() => 向调用此方法的文件写入数据，写入到当前文件指针偏移处
* seek() => 更新文件当前指针偏移量到指定位置，以字节为单位
* truncate() => 将文件调整为指定字节大小