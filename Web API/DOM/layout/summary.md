# 总结

元素有如下坐标属性
* clientWidth/ClientHeight => 元素的 client 区域大小，包含 padding, 但不包含 border, 也不包含 滚动条(如果显示的话)
* offsetWidth/offsetHeight => 元素整体的大小，包含所有
* scrollTop/scrollLeft => 元素本身已经滚动的距离，该值可改。更改时，元素会随之滚动
* scrollHeight/scrollWidth => 元素本身在完全展开后的宽度和高度

不常用的坐标属性
* clientLeft/ClientTop => 不常用，在 ltr 方向下，表示元素的 border 宽度、高度
* offsetParent => 获取离当前元素最近的定位元素，或者 td,tr,table
* offsetLeft/offsetTop => 当前元素相对于定位祖先元素的偏移


## 计算滚动条宽度
```js
// 当元素只有垂直滚动条，没有水平滚动条时，怎么计算滚动条的宽度
// 对于普通元素，clientWdith = scrollWidth, 如果没有 border, offsetWidth = clientWidth + scrollBarWidth
// 但是对于 document.documentElement, 它返回的 clientWdith， scrollWidth， offsetWidth 很可能是相同的， offsetWidth 并没有考虑滚动条
// 这个时候就可以使用 window.innerWidth， 这个属性包含了滚动条的宽度
```

## 文档的坐标属性
document.documentElement 也是一个元素，也会有 元素的坐标属性。但该元素返回的坐标属性并不一定和普通元素有一样的效果，比如 offsetWidth.  
如果想获取文档的宽度和高度，可以使用 window.innerWidth, window.innerHeight  
如果想获取文档的滚动量，可以使用 window.scrollX, window.scrollY

## 滚动 API
* window.scrollBy(dx,dy)
* window.scrollTo(x, y)
* elem.scroll(param: [number, number] | {top: number, left: number, behavior?: 'smooth' | 'auto'})
* elem.scrollIntoView(top) => 让指定元素滚动到 window 视图里

## 坐标 API
* getBoundingClientRect => 返回元素的 最小矩形 bounding box 的坐标信息。通常使用的就是 x,y,width,height 等 client 坐标
* document.elementFromPoint(x, y), 找到在当前可视区域内， 坐标为 x,y 的点对应的嵌套最深的元素 