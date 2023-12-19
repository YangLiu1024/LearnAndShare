# DOMParser
用于将字符串解析为 DOM

# HTMLCollection
一种 array-like 结构，用来遍历 HTML node

# NodeList
NodeList 对象是节点的集合，通常是由属性，如Node.childNodes 和 方法，如document.querySelectorAll 返回的

# MutationObserver
该接口提供了监视对 DOM 树所做修改的能力。  
***const observer = new MutationObserver(callback)***, callback 是对响应事件的回调  

***observer.observe(targetNode, config)*** 对指定节点监听指定事件  
***observer.disconnect()*** 取消监听
```js
// 选择需要观察变动的节点
const targetNode = document.getElementById("some-id");

// 观察器的配置（需要观察什么变动）
const config = { attributes: true, childList: true, subtree: true };

// 当观察到变动时执行的回调函数
const callback = function (mutationsList, observer) {
  // Use traditional 'for loops' for IE 11
  for (let mutation of mutationsList) {
    if (mutation.type === "childList") {
      console.log("A child node has been added or removed.");
    } else if (mutation.type === "attributes") {
      console.log("The " + mutation.attributeName + " attribute was modified.");
    }
  }
};

// 创建一个观察器实例并传入回调函数
const observer = new MutationObserver(callback);

// 以上述配置开始观察目标节点
observer.observe(targetNode, config);

// 之后，可停止观察
observer.disconnect();
```

config 用来指定监听的具体配置
* subtree, 为 true 时监听 target 所有子节点
* childList, 为true 时，监听 target 节点中发生的节点 新增和删除事件. 如果 subtree 也为 true, 那么会监听整个子树
* attributes, 为 true 时监听所有观察节点的属性值变化
* attributeFilter, 一个用于指定那些属性名会被监听的数组。如果不指定，所有属性都会被监听

# TextDecoder
TextDecoder 表示一个文本解码器，一个解码器只支持一种特定文本编码，例如 utf-8,gbk 等。解码器将字节流作为输入，输出解码之后的字符流  
* TextDecoder(encoding), 指定一个编码方式
* decode(buffer, options), buffer 是 ArrayBuffer, TypedArray, options 包含 stream 属性，如果为 true, 表示 buffer 是分块的，后续的 decode 调用将跟随附加数据

# TextEncoder
TextEncoder 是一个编码器，将码位流编码为 utf8 字节流
* encode(string) => 将字符串编码为 utf8 字节流
* encodeInto(string, unit8Array) => 将编码结果存入指定 unit8Array

需要注意的是，在将字符转换为字节时，每一个字符至少需要一个字节，比如英文字符，中国字符需要 3个字节，有的表情包还需要 4个字节

# TreeWalker
用于遍历指定 dom 节点