# position
position 属性指定了元素的摆放方式。
* static, 默认值, 按照默认元素流的方式一个接一个的摆放元素，会无视 top,bottom,left,right 属性
* fixed, 总是摆放在 viewport 的同一地方，不受页面滚动的影响。
* relative, 会相对于它的 normal position 进行摆放。而其它元素也不会受该元素的挪动影响，会停留在它们本来的位置。
* absolute, 是相对于它最近的 positioned(non-static) 祖先来摆放的。absolute 元素会被移出 normal flow, 并且可以和其它元素重叠。如果找不到 positioned 祖先，则会使用 document body.
* sticky, 元素的摆放会依据它设置的offset 和当前 scroll 的位置判断，是否达到当前 viewport 边界. 如果没有，则按照它的正常位置来摆放，如果达到边界, 则以 fixed 形式摆放

# display
display 指定了元素的展示方式
* block, block level 的 元素总是 start a new line and take up the full width avaiable, such as div,h1-h6,p,section,header,footer,form
* inline, inline 元素不会另起一行，并且只占据必要的宽度，such as span,a,img
* none, 如果为none，则 hide 该元素
* inline-block, 相对于 block, inline-block 不会另起一行，相对于 inline, inline-block 可以设置宽度高度。

改变一个元素的默认 display, 只是改变了元素的展示方式，并没有在本质上改变该元素，比如一个 span 元素，即使 display 改为了 block, 也不会允许在 span 里内嵌一个 block 元素。  
隐藏一个元素，除了使用 *display:none*, 还可以使用 *visibility:hidden*, 区别在于 *display:none* 是删除元素，其空间会被释放，而 *visibility:hidden* 只是简单的 hide, 其空间仍会被占据。*visibility* 的值可以是 *visible*, *hidden*, *initial*

# overflow
overflow 控制当 content 比 container 更大时的行为。比如裁剪 content, 或者添加 scroll bar.
* visible, 默认值，overflow 的内容并不会被裁剪并且也是 visible 的，它会在 container 之外被渲染
* hidden, overflow的内容会被裁剪掉，只有 container 里面的内容可见
* scroll, overflow 的内容会被裁剪掉，然后添加 scroll bar 来查看被裁剪掉的内容
* auto, 和 scroll 类似，但是只在必要的时候添加 scroll bar

overflow 只在元素是 block 元素且有指定高度的时候有效。

# align
对于设置了宽度的 block 元素，可以直接使用 *margin:auto* 来让 block 元素居中显示。因为block 元素之外的空间会被左右 margin 平分。  
对于元素中的文字，如果想要居中显示，可以使用 *text-align:center*.  
如果想把元素左/右对齐，可以使用 float, 或者直接使用 absolute 定位。  
或者使用 flex 布局

# z-index
z-index 指定元素的 stack index, 具有 higher index 总是被绘制在 lower index 元素之上。  
注意， z-index 只对 positioned element 以及 flex items(具有flex 布局元素的直接子元素) 有效。  
如果多个 positioned elements 在没有指定 z-index 的情况下重叠在一起，则后定义的元素会在更上面绘制。

# flex
flex 是为了在不使用 float 或者 positioned elements 的条件下，更容易的设计出 flexible responsive layout structure.  
首先，flex layout 需要一个 flex container, 即指定了 *display:flex* 的元素。  
该 flex container 的所有直接子元素，被称为之 flex items. 
## flex container  
一个 flex container 具有以下属性：
### flex-direction
定义了 flex container 想以什么顺序展示flex items, 
* column, 表示从上往下
* column-reverse, 表示从下往上
* row, 表示从左往右
* row-reverse, 表示从右往左

### flex-wrap
用于当容器高度或者宽度不足以完全显示容器内元素的时候，是否需要换行或者换列，其值可以为 *wrap*, *nowrap*, *wrap-reverse*.

### flex-flow
*flex-flow* 是设置 *flex-direction* 和 *flex-wrap* 的 shorthand

### justify-content
用来水平对齐 flex items.
* center, 居中对齐
* flex-start, 默认值，左对齐
* flex-end, 右对齐
* space-around, 左右两个元素之外会有一定的间隔，其它所有 item 以等间距排列
* space-between, 左右两个元素之外没有间隔，所有元素等间距排列

### align-items
在 flex 布局里，比如在 row 布局中，每个 item  一般指定了自己的宽度，但是没有指定高度，这个时候，每个元素(包括 block， inline 元素)都会自动充满整个 container 的高度。  
或者，如果元素指定了高度，那么这些元素在竖直方向，是怎样对齐的呢？  
为了解决这些问题，可以使用 *align-items*
* center, 如果元素指定了高度，则使用它的高度，如果没指定，则使用它必要的高度。 然后这些元素在竖直方向居中对齐。
* flex-start, 上对齐
* flex-end, 下对齐
* stretch, 默认值，通过拉伸元素来填满容器

### align-content
如果设置了 wrap, 那么容器可能就会有多行，行与行之间怎么对齐呢，就通过 *align-content* 决定。
* center
* flex-start
* flex-end
* space-around
* space-between
* stretch

## flex items
flex container 的直接子元素，自动成为了 flex item.  flex item 具有以下 style 属性
* order, 可以更改 item 的 order, 即改变 item 在容器里的顺序, 必须为数字，默认值为 0, 比如
```html
<div style="display:flex">
    <div style="order: 2"></div>
    <div style="order: 1"></div>
</div>
```
* flex-grow, 指定元素相对于其它元素的增长系数，越大，则增长的越快，即占据的空间越大. 必须为数字，也可以是百分比，比如 %50， 即表示占据一半空间。默认值为 0. 如果为0, 则表示不增长，使用自己本身的高度/宽度
* flex-shrink, 指定元素相对于其它元素的压缩系数，越大，则压缩的越快，占据的空间越小。必须为数字，默认值为 1. 如果为0， 则表示不压缩，即在容器压缩的时候，该元素不减小
* flex-basis, 设置 item 初始宽度
* flex, shorthand for 'flex-grow flex-shrink flex-basis'
* align-self, 该 item apply 自己的 align style, 而不是使用 container 的 align-items

