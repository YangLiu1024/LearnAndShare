# Border radius
设置元素 4个角的 radius
* rounded-none -> border-radius: none
* rounded-sm -> border-radius: 0.125rem
* rounded
* rounded-md
* rounded-lg
* other...
* rounded-full

# Border Width
* border-0 -> border-width: 0px
* border -> border-width: 1px
* border-2
* border-4
* border-x-0
* border-y-2
* other...
* border-t-2
* other...

还可以在 child elements 之间加 border, 通过 *divide-{x|y}-{width}* 和 *divide-{color}* 来实现

# Border Color
* border-inherit
* border-transparent
* border-black
* other...
* border-r-black
* other...
* border-x-black
* border-y-black


# Border Style
* border-solid -> border-style: solid
* border-dashed
* border-dotted
* border-double
* border-hidden
* border-none

# Divide Width
控制 children elements 之间的 border
* divide-x
* divide-y
* divide-x-2
* divide-y-2
* other...

# Divide Color
控制 children elments 之间 border 的颜色
* divide-transparent
* divide-slate-200
* other...

# Divide Style
* divide-solid
* divide-dashed
* other...

# Outline Width
控制元素的 outline width. outline 和 border 很类似，区别在于 outline 不占据空间，绘制与元素内容周围
* outline-0 -> outline-width: 0px
* outline-1 -> outline-width：1px
* other...

# Outline Color
* outline-slate-300 -> outline-color: #e2e8f0
* other...

# Outline Style
* outline-none -> outline: 2px solid transparent; outline-offset: 2px
* outline -> outline-style: solid
* outline-dashed
* outline-dotted
* outline-double
* outline-hidden

# Outline Offset
控制 outline offset
* outline-offset-0
* outline-offset-1 -> outline-offset: 1px
* other...

# Ring Width
ring 其实是 box-shadow 的运用
* ring-0
* ring-1
* ring-2
* ring
* ring-4
* other...

# Ring Color
* ring-balck
* ring-indigo-200
* other...

# Ring Offset Width
在 ring offset 的空白区域，默认会由 solid white box-shadow 填充
* ring-offset-0
* ring-offset-1
* other...

# Ring Offset Color
有的时候，ring offset 带来的空白区域，想使用其它颜色来填充
* ring-offset-black
* ring-offset-slate-100
* other...

# Conclusion
Outline is a line that is drawn just outside the border edge of the elements. 一般用来表示 element 的聚焦 或者 active 状态  
the different between border and outline
* outline 不占据空间
* outline 不允许单独设置任意 edge, 但 border 可以设置任意边的 width,style,color
* outline 不会影响 element 的 size　和　position

ring 是通过 box shadow 实现的效果



