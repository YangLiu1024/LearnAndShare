# window layout
除了找到元素本身的 layout, 那么对于 window 的 layout 呢？  
* document.documentElement.clientWidth, 和 element.clientWidth 意义相同，除去 border, 且不包含 滚动条的宽度
* document.documentElement.clientHeight

window 也具有一些属性
* window.innerWidth, 等于 document.documentElement.clientWidth + 滚动条宽度
* window.innerHeight, 等于 document.documentElement.clientHeight + 滚动条高度

通常我们会希望使用不包含滚动条的宽度，高度

# 文档的 width/height
按道理来说，document.documentElement.scrollHeight 即可以表示 document 完整的高度，但是有的浏览器在有的情况下，可能会返回一些错误的值，这是因为一些 legacy issue 引入的问题。  
为了可靠的获取文档的完整大小，可以使用
```js
let scrollHeight = Math.max(
  document.body.scrollHeight, document.documentElement.scrollHeight,
  document.body.offsetHeight, document.documentElement.offsetHeight,
  document.body.clientHeight, document.documentElement.clientHeight
);
```
# 文档的当前滚动
可以通过 document.documentElement.scrollTop/scrollLeft 来获取，但在一些较旧的浏览器上会出错。  
但我们可以使用 window.pageXOffset/pageYOffset 来获取滚动信息。window.scrollX/scrollY 也是可以的，pageXOffset 只是 scrollX 的别名

# 滚动
可以通过 scrollLeft/scrollTop 直接设置滚动进度，也可以通过方法
* window.scrollBy(dx, dy), 相对当前位置，进行滚动 dx, dy
* window.scrollTo(x, y), 滚动到具体 x, y 位置
* elem.scrollIntoView(top), 该调用会使页面滚动，使 elem 出现在 top ? 窗口顶部 : 窗口底部