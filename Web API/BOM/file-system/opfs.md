## OPFS(Origin Private File System)
当前 origin 所专有的 存储端点，不像常规文件一样对用户可见。它提供对一种经过高度性能优化的特殊文件的访问能力，例如对文件内容的原地写入访问。 
OPFS 提供了页面所属源私有的，对用户不可见的，底层的逐字节文件访问能力。因此它不需要经过与调用文件系统访问 API 所需的一系列相同的安全性检查和授权，而且比文件系统访问 API 更快。  
它还有一套同步 调用方法可用(其它文件系统 API 调用都是异步的)，但 只能在 web worker 里运行，这样就不会阻塞主线程 
```js
// 以下示例显示了在 web worker 里访问 OPFS 
onmessage = async (e) => {
  // 获取从主线程发往 worker 的消息
  const message = e.data;

  // 获取 OPFS 中草稿文件的句柄
  const root = await navigator.storage.getDirectory();
  const draftHandle = await root.getFileHandle("draft.txt", { create: true });
  // 获取同步访问句柄
  const accessHandle = await draftHandle.createSyncAccessHandle();

  // 获取文件大小
  const fileSize = accessHandle.getSize();
  // 将文件内容读取到缓冲区
  const buffer = new DataView(new ArrayBuffer(fileSize));
  const readBuffer = accessHandle.read(buffer, { at: 0 });

  // 将消息写入到文件末尾
  const encoder = new TextEncoder();
  const encodedMessage = encoder.encode(message);
  const writeBuffer = accessHandle.write(encodedMessage, { at: readBuffer });

  // 将更改持久化至磁盘
  accessHandle.flush();

  // 用完 FileSystemSyncAccessHandle 记得把它关闭
  accessHandle.close();
};
```
OPFS 和用户可见文件系统的不同
1. OPFS 和其它源分区存储机制(例如 indexed DB) 一样，受到浏览器存储配额限制。可用通过 navigator.storage.estimate() 来检查使用情况
2. 清除站点的存储数据会删除 OPFS
3. 访问 OPFS 中的文件不需要权限提示和安全性检查
4. 浏览器会把 OPFS 的内容持久化保存在磁盘的某个位置，但你不能指望找到这些文件，OPFS 对用户不可见

## 访问 OPFS
首先，需要调用 *navigator.storage.getDirectory()* 获得 OPFS root directory 的句柄  
### 在主线程访问 OPFS
#### 获取文件、目录句柄
需要调用异步 API, 比如 FileSystemDirectoryHandle.getFileHandle() 和 FileSystemDirectoryHandle.getDirectoryHandle() 方法来分别访问文件（FileSystemFileHandle）和目录（FileSystemDirectoryHandle） 
在上述方法中传入 *{create: true}* 参数，会在文件或者 folder 不存在时创建相应的文件或者 目录
#### 读取文件
调用 FileSystemFileHandle.getFile() 方法返回一个 File 对象。这是一种特化的 Blob 对象，所以可用像其它 Blob 一样去操作它。
#### 写入文件
调用 *FileSystemFileHandle.createWritable()* 返回一个 FileSystemWritableFileStream 对象， 这是一种特化的 WritableStream 对象  
调用 FileSystemWritableFilestream.write() 来向其写入内容。  
使用 WritableStream.close() 关闭流
#### 删除文件或者目录
可用在父目录调用 FileSystemDirectoryHandle.removeEntry() 来删除指定项，或者也可以在想删除的 文件或者目录的句柄上直接调用 remove().  
值得注意的是，想要删除文件夹和它所有的子文件夹，比如传入 *{recursive: true}*  
删除所有 OPFS => ***await (await navigator.storage.getDirectory()).remove({ recursive: true });***
#### 列出文件夹内容
FileSystemDirectoryHandle 是一个 异步迭代器，可以用 *for await ..of*, 或者 entries(), keys(), values() 来遍历
```js
for await (let [name, handle] of directoryHandle) {
}
for await (let [name, handle] of directoryHandle.entries()) {
}
for await (let handle of directoryHandle.values()) {
}
for await (let name of directoryHandle.keys()) {
}
``` 
#### 区分文件和目录
FileSystemHandle.kind, 如果是 'file', 那么就是 文件，如果是 'directory', 就是 folder

### 在 web worker 里操作 OPFS
通过 FileSystemFileHandle.createSyncAccessHandle() 来同步地处理文件。虽然创建的是同步访问，但是该方法本身是异步的。  
返回的 FileSystemSyncAccessHandle 上有几个同步的方法可用：
* getSize() => 返回文件字节大小
* write(buffer) => 将一个缓冲区的内容写到文件里，可选择在给定的偏移处开始写入。它会返回写入的字节数
* read(buffer) => 读取文件的内容到一个缓冲区，可选择在给定的偏移处开始读取
* truncate(size) => 将文件调整至给定的大小
* flush() => 确保文件的内容包含所有通过 write() 完成的修改
* close() => 关闭访问句柄

```js
const opfsRoot = await navigator.storage.getDirectory();
const fileHandle = await opfsRoot.getFileHandle("fast", { create: true });
const accessHandle = await fileHandle.createSyncAccessHandle();

const textEncoder = new TextEncoder();
const textDecoder = new TextDecoder();

// 将这个变量初始化为文件的大小。
let size;
// 文件当前的大小，最开始是 `0`。
size = accessHandle.getSize();
// 编码要写入文件的内容。
const content = textEncoder.encode("Some text");
// 在文件的开头写入内容。
accessHandle.write(content, { at: size });
// 强制刷入更改。
accessHandle.flush();
// 文件当前的大小，现在是 `9`（"Some text" 的长度）。
size = accessHandle.getSize();

// 编码更多要写入文件的内容。
const moreContent = textEncoder.encode("More content");
// 在文件的末尾写入内容。
accessHandle.write(moreContent, { at: size });
// 强制刷入更改。
accessHandle.flush();
// 文件当前的大小，现在是 `21`（"Some textMore content" 的长度）。
size = accessHandle.getSize();

// 准备一个长度与文件相同的数据视图。
const dataView = new DataView(new ArrayBuffer(size));

// 将整个文件读取到数据视图。
accessHandle.read(dataView);
// 打印 `"Some textMore content"`。
console.log(textDecoder.decode(dataView));

// 在数据视图中的偏移位置 9 处开始读取。
accessHandle.read(dataView, { at: 9 });
// 打印 `"More content"`。
console.log(textDecoder.decode(dataView));

// 裁去文件头 4 个字节之后的内容。
accessHandle.truncate(4);
```