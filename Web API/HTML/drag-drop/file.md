# File Drag & Drop
支持从系统文件管理器拖拽一个或多个文件到网页。  

触发 *drop* 事件的目标元素需要一个 *ondrop* 事件处理函数, *ondragover* 也是需要的，
```html
<div
  id="drop_zone"
  ondrop="dropHandler(event);"
  ondragover="dragOverHandler(event);">
  <p>Drag one or more files to this Drop Zone ...</p>
</div>
```
当用户释放文件时 *drop* 事件将会被触发。如果浏览器支持 *DataTransferItemList* 接口，则可以使用 *getAsFile()* 来获取每个文件。否则使用 *DataTransfer.files* 属性。  

```js
function dropHandler(ev) {
  console.log("File(s) dropped");

  // 阻止浏览器默认行为(阻止文件被打开)
  ev.preventDefault();

  if (ev.dataTransfer.items) {
    // Use DataTransferItemList interface to access the file(s)
    for (var i = 0; i < ev.dataTransfer.items.length; i++) {
      // If dropped items aren't files, reject them
      if (ev.dataTransfer.items[i].kind === "file") {
        var file = ev.dataTransfer.items[i].getAsFile();
        console.log("... file[" + i + "].name = " + file.name);
      }
    }
  } else {
    // Use DataTransfer interface to access the file(s)
    for (var i = 0; i < ev.dataTransfer.files.length; i++) {
      console.log(
        "... file[" + i + "].name = " + ev.dataTransfer.files[i].name,
      );
    }
  }
}

```