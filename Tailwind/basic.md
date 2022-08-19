# Install
```bash
npx create-react-app my-project
cd my-project
npm install -D tailwind postcss autoprefier
npx tailwind init -p
```
# Layout
## Aspect ratio
元素的宽度/高度比 
* aspect-square -> aspect-ratio: 1 / 1
* aspect-video -> aspect-ratio: 16 / 9
* aspect-auto -> aspect-ratio: auto
* aspect-[w/h] -> aspect-ratio: w / h

## Container
<code>container</code> 会根据当前视窗的 breakpoint 设置元素的 max-width, 比如在宽度小于 sm 的情况下，container 的宽度是 100%, 在 sm 和 md 之间时，是 sm(640px), 其它 breakpoint 类似。  
container 并不自带其它任何 style, 如果想添加 center, margin, padding 等需要自行添加。

## Columns
将 element 分为几列，children 按照列依次摆放，有的children 还可以跨列，不是很好理解，建议不要使用

## Box Sizing
控制 browser 怎样计算 element 的 box size
* box-border -> border 和 padding 会算在 width/height 里， 比如指定 w-20 h-20 p-4, 那么元素的 content 大小，其实会是 16*16. ***这是默认行为***。
* box-content -> border 和 padding 不会计算在 width/height 里

# Display
控制元素的 display type
* block
* inline
* inline-block
* flex -> box level flex container
* inline-flex -> inline flex container
* grid -> create grid container
* inline-grid
* contents -> 该元素的子元素会渲染为该元素的父元素的子元素
* table -> 通过 table, table-header-group, table-row, table-cell, table-row-group 等 class 来渲染 table
* hidden -> display: none 来将元素移除出元素流

# Floats
不推荐使用

# Object Fit
对于置换元素(replaced element), CSS 并不能直接控制置换元素 content 的渲染，比如 img. Object Fit 就是用来控制置换元素 resize 的行为。

# Overflow
Overflow 用来控制当容器元素的内容超出了容器的大小时，该怎么处理。
* overflow-visible -> 范围外的内容仍然会被渲染，且可见
* overflow-hidden -> clip the content which overflow the container
* overflow-scroll -> 不管内容是否超出容器，总是 show scroll bar
* overflow-x-scroll
* overflow-y-scroll
* overflow-auto -> 当内容超出的时候，才 show scroll bar
* overflow-x-auto -> 在 x 方向 show scroll bar if necessary
* overflow-y-auto -> 在 y 方向 show scroll bar if necessary

# Overscroll Behavior
当 scroll 到边界时，该怎么操作
* overscroll-contain -> 当滚动到边界时，阻止向父元素发出 滚动事件，但不阻止 bounce 效果
* overscroll-none -> 当滚动到边界时，阻止向父元素发出 滚动事件，也阻止 bounce 效果
* overscroll-auto -> 不阻止继续发出 scroll 事件

# Position
* static -> normal document flow
* relative -> position according to the normal document flow
* absolute -> outside of normal flow, position according to nearest non-static element
* fixed -> fixed to browser window
* sticky -> 如果在视窗内，则在原始位置，如果即将滚动出视窗，则一直留在

# Visibility
* visible
* invisible

# Z index
越大的 z index 放在越上面
* z-0 -> z-index: 0
* z-10
* z-20
* z-30
* z-40
* z-50
* z-auto