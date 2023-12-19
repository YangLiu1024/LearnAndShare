# navigator

navigator 提供了一些实用的属性和方法
* clipboard -> 剪切板
* onLine -> 浏览器是否链接到网络，为了监测到这一改变，需要 *window.addEventListener('online', () => {})* 或者 *window.addEventListener('offline', () => {})*
* storage -> 返回一个 StorageManager, 用来访问 local storage


## Clipboard
实例方法, 需要注意的是，这些方法都是异步调用。如果剪切板访问被拒绝(比如没有申请访问权限)，则会返回 rejected promise
* read(), 读取任意数据，返回一个 promise, 返回的数据是 ClipboardItem[]
* readText()，读取文本数据，返回一个 promise, 数据是 DOMString
* write(), 写入任意数据，完成后 promise resolved
* writeText(), 写入文本数据，完成后 promise resolved

### ClipboardItem
用于表示各种不同种类的 数据，其构造函数，key 是 MIME type, value 是 Blob 数据，或者 string, 或者 promise which resolve to either string or blob

#### 构造函数
```ts
new ClipboardItem({
    [key: keyof MIME type]: Blob
})

async function writeClipImg() {
  try {
    const imgURL = "/myimage.png";
    const data = await fetch(imgURL);
    const blob = await data.blob();

    await navigator.clipboard.write([
      new ClipboardItem({
        [blob.type]: blob,
      }),
    ]);
    console.log("Fetched image copied.");
  } catch (err) {
    console.error(err.name, err.message);
  }
}
```
#### 属性和 API
* ***item.types*** 返回該 item 持有的 data 的 types 数组
* item.getType(type) 返回该 type 对应的 blob data

```js
async function getClipboardContents() {
  try {
    const clipboardItems = await navigator.clipboard.read();

    for (const clipboardItem of clipboardItems) {
      for (const type of clipboardItem.types) {
        const blob = await clipboardItem.getType(type);
        // we can now use blob here
      }
    }
  } catch (err) {
    console.error(err.name, err.message);
  }
}
```