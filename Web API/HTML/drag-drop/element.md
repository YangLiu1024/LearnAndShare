# Drag & Drop
拖拽事件
* drag => 当拖拽元素或选中文本时触发，通过 *ondrag* 响应
* dragstart => 当用户开始拖拽元素或选中文本时触发，*ondragstart*
* dragend => 在拖拽结束时触发，比如松开鼠标，或者 Esc, 通过 *ondragend* 响应
* dragenter => 当拖拽元素或选中的文本到一个可释放目标时触发，*ondragenter*
* dragleave => 当拖拽元素或选中文字离开可释放区域时触发，*ondragleave*
* dragover => 当元素或选中文本被拖到一个可释放目标上时触发，每 100ms 触发一次，*ondragover*
* drop => 当用户在可释放区域释放鼠标时触发，*ondrop*

需要注意的时，当从操作系统向浏览器中拖拽文件时，不会触发 dragsart 和 dragend 事件。  

给 HTML 添加拖拽功能，需要使用 DragEvent 和 DataTransfer
* DragEvent => 有一个 dataTransfer 属性，指向一个 DataTransfer 对象
* DataTransfer => 包含了拖拽事件的状态，比如拖拽类型(copy or move), 拖拽的数据(一个或多个项), 和每个拖拽项的 MIME 类型。DataTransfer 也可以添加/删除数据项，获取数据项的内容

## DataTransferItem
每个 DataTransfer 都包含一个 items 属性，该属性是 DataTransferItem 的 list. 一个  DataTransferItem 表示一个拖拽项，每个项目都有一个 kind(string or file) 和一个表示数据 MIME 类型的 type.  
DataTransferItemList 是 DataTransferItem 的列表，这个列表对象支持 添加拖拽项，移出拖拽项，清空拖拽项的功能。  

## 让元素可拖拽
在 HTML 中，除了图像，链接和选中的文本，其它元素在默认情况下是不能拖拽的。为了让一个元素可拖拽需要添加 *draggable* 属性，再加上注册 *ondragstart* 回调，且在回调里设置拖拽数据。  
如果没有拖拽数据，那么拖拽行为不会发生。
```js
  function dragstart_handler(ev) {
    // Add the target element's id to the data transfer object
    ev.dataTransfer.setData("text/plain", ev.target.id);
  }

  window.addEventListener("DOMContentLoaded", () => {
    // Get the element by id
    const element = document.getElementById("p1");
    // Add the ondragstart event listener
    element.addEventListener("dragstart", dragstart_handler);
  });

  <p id="p1" draggable="true">This element is draggable.</p>
```

## 定义拖拽数据
应用程序可以在拖拽操作中包含任意数量的数据项，每一个数据都是 string, 它的 类型是 MIME, 如 text/html.  
每一个 drag event 都有一个 dataTransfer 属性，DataTransfer 可以通过 setData(type, data) 来添加数据项。

## 定义拖拽图像
在拖拽过程中，浏览器会在鼠标旁显示一张默认图片。当然，应用程序也可以自定义
```js
function dragstart_handler(ev) {
  var img = new Image();
  img.src = "example.gif";
  ev.dataTransfer.setDragImage(img, 10, 10);
}
```
## 定义拖拽效果
*dropEffect* 属性用来控制拖放操作中用户给予的反馈，它会影响拖拽过程中浏览器显示的鼠标样式。比如，当鼠标悬停在目标元素上的时候，浏览器鼠标也许要反映拖放操作的类型。  
有三个效果可以定义
* copy => 表示被拖拽的目标将被拷贝到目的位置
* move => 表示被拖拽的目标将被移动
* link => 表示在拖拽源位置和目标位置之间将会创建一些连接
* none => 不允许操作

```js
function dragstart_handler(ev) {
  ev.dataTransfer.dropEffect = "copy";
}
```
## 定义一个放置区
当拖拽一个项目到 HTML 元素中时，浏览器默认不会有任何响应。想要让一个元素变成可释放区域，该元素必须设置 *ondragover* 和 *ondrop* 事件处理程序
```js
<script>
  function dragover_handler(ev) {
    ev.preventDefault();
    ev.dataTransfer.dropEffect = "move";
  }
  function drop_handler(ev) {
    ev.preventDefault();
    // Get the id of the target and add the moved element to the target's DOM
    var data = ev.dataTransfer.getData("text/plain");
    ev.target.appendChild(document.getElementById(data));
  }
</script>

<p
  id="target"
  ondrop="drop_handler(event)"
  ondragover="dragover_handler(event)">
  Drop Zone
</p>
```
## 拖拽结束
当拖拽操作结束时，在源元素(最开始拖拽的目标元素) 上触发 *dragend* 事件，不管拖拽是完成还是取消，都会触发这个事件。dragend 事件处理程序可以检查 dropEffect 属性的值来确认是否拖拽成功与否。  


# 推荐的拖动类型

## 拖拽文字
对于文字，最好使用 *text/plain* 类型，data 必须是字符串。  
拖拽文本框中的文字和页面选中部分的文字是自动完成的，不需要手动处理这些拖动。  

## 拖拽链接
链接应该包含两种类型 *text/uri-list* 和 *text/plain*.
```js
const dt = event.dataTransfer;
dt.setData("text/uri-list", "http://www.mozilla.org");
dt.setData("text/plain", "http://www.mozilla.org");
```
在释放拖拽时，因为数据里可能包含多个 link, 也可能包含一些 comments, 可以使用 特殊类型 *URL* 来获取第一个 valid *uri-list* link. 但 *URL* 不能作为 setData 的类型
```js
const url = event.dataTransfer.getData("URL");
```
有的情况下，还会有 *text/x-moz-url* 类型，这种类型会包含 link 和 link title
```js
http://www.mozilla.org
Mozilla
http://www.example.com
Example
```
## 拖拽HTML 和 XML
对于 HTML, 其类型是 *text/html*, 其数据多半是 *element.innerHTML*. 对于 XML, 其类型是 *text/xml*, 其数据需要是 well-formed XML.  
如果使用 *text/plain* 来表示 HTML/XML 数据，则字符串不允许包含 *source tag* 或者 *attributes*
```js
var dt = event.dataTransfer;
dt.setData("text/html", "Hello there, <strong>stranger</strong>");
dt.setData("text/plain", "Hello there, stranger");
```

## 拖拽文件
拖拽文件使用类型 *application/x-moz-file*, 当然，也可以在最后加上 *text/uri-list* 和 *text/plain* 来表示文件 URL 等信息.
```js
// 对于文件，其数据不再是字符串，不能使用 setData, getData
// mozSetDataAt
event.dataTransfer.mozSetDataAt("application/x-moz-file", file, 0);
// mozGetDataAt
var file = event.dataTransfer.mozGetDataAt("application/x-moz-file", 0);
```

## 拖拽图像
图像一般不支持直接拖拽，通常是拖拽图像的 URLs. 所以需要使用类型 *text/uri-list*. 其数据应该是 URL of image, 或者 data URL(图像不存储在 web site or disk).  
在 chrome 里，还可以使用类型 *image/jpeg*, *image/png*, *image/gif* 等类型，其数据应该是实现了 *nslInputStream* 接口的对象，当读取该对象时，该对象需要提供图像的 bit 数据。  
如果图像在 local disk, 还可以使用 *application/x-moz-file* 类型来实现拖拽。  

数据的顺序是很重要的，应该从 最具体到最不具体。对于图像，*image/jpeg* 这类类型应该是第一个，*application/x-moz-file* 应该是第二个，再然后才是 *text/uri-list* 和 *text/plain*.
```js
var dt = event.dataTransfer;
dt.mozSetDataAt("image/png", stream, 0);
dt.mozSetDataAt("application/x-moz-file", file, 0);
dt.setData("text/uri-list", imageurl);
dt.setData("text/plain", imageurl);
```
## 拖拽节点
对于节点，可以使用 *application/x-moz-node* 类型，其数据必须是 DOM node.
## 拖拽自定义数据
对于自定义数据，一般使用 *application/customized-type*, 这种类型通常用于应用程序本身。  
为了让拖拽可以应用到任何地方，通常需要添加 *text/plain* data.