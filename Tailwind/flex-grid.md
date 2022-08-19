# Flex
container : flex, flex-direction, justify-content, align-items  
children: order, flex-basis, flex-grow, flex-shrink  
对于 flex item, 它有自己的初始大小(这个大小可以是绝对值，或者相对值), 以及在容器 resize 时的拉伸和压缩效果。 拉伸靠 flex-grow 控制，压缩靠 flex-shrink. 如果使用 flex-none, 则表示该 item 不受 flex 影响，不可拉伸，不可压缩，一直使用自己的 size.
# Flex basis
控制 flex items 的初始大小
* basis-0 -> flex-basis: 0px
* basis-1 -> flex-basis:0.25rem
* basis-2 -> flex-basis: 0.5rem
* other...
* basis-auto
* basis-1/2 -> flex-basis: 50%
* basis-1/3
* other...
* basis-full

# Flex Direction
* flex-row
* flex-row-reverse
* flex-col
* flex-col-reverse

# Flex Wrap
当 flex item 的宽度或者高度超过 container 的 宽度或者高度，是否要 overflow
* flex-wrap
* flex-nowrap
* flex-wrap-reverse

# Flex
Flex 是 flex-grow, flex-shrink, flex-basis 的缩写
* flex-1 -> flex: 1 1 0%, 可拉伸，可压缩，丢弃初始大小
* flex-auto -> flex: 1 1 auto, 可拉伸，可压缩，考虑初始大小
* flex-initial -> flex: 0 1 auto, 可压缩，不可拉伸，考虑初始大小
* flex-none -> flex: none, 不可拉伸，不可压缩, 一直使用初始大小

# Flex Grow
* grow -> flex-grow: 1
* grow-0 -> flex-grow: 0

# Flex Shrink
* shrink -> flex-shrink:1
* shrink-0

# Order
设置 flex item 的 order, 让 item 的顺序可以和 DOM 不一样
* order-1
* order-2
* other...
* order-last
* order-first


# Grid
display: grid 让容器以 grid 形式来渲染

# Grid Template Columns
控制 grid 里的 columns 数量, 每一列 size 相同
* grid-cols-1
* grid-cols-2
* other...
* grid-cols-12
* grid-cols-none

# Grid Columns Span
grid col 的索引是从1 而不是 0 开始，所以 grid-cols-3 的 grid 如果 col-span-full， 那么换成 start/end 形式的话，就应该是 col-start-1 col-end-4, start/end 是左闭右开
* col-auto
* col-span-1
* col-span-2
* other...
* col-span-full
* col-start-1
* col-start-2
* other...
* col-end-1
* col-end-2
* other...

# Grid Template Rows
控制 grid 里的 rows 数量, 每一行的 size 相同
* grid-rows-1
* grid-rows-2
* other...
* grid-rows-6

# Grid Rows Span
Similar with grid columns span
* row-span-1
* row-span-2
* other...
* row-span-full
* row-start-1
* other...
* row-end-1
* other...

# Grid Auto Flow
控制 item 在 grid 里的 自动摆放。dense 表示，在摆放元素的时候，会尝试去填充网格中前面留下的空白。这样会填上稍大元素留下的空白，但也会打乱顺序。
* grid-flow-row -> 逐行填充来排列元素，在必要时增加新行, ***默认值***
* grid-flow-col -> 逐列填充来排列元素，在必要时增加新列
* grid-flow-row-dense
* grid-flow-col-dense

# Grid Auto Columns
对于 grid-flow-col, col 是逐渐创建的，对于新创建出的列，怎么控制它的 size
* auto-cols-auto -> grid-auto-columns: auto
* auto-cols-min -> grid-auto-columns: min-content
* auto-cols-max -> grid-auto-columns:max-content
* auto-cols-fr

# Grid Auto Rows
similar with Grid Auto Columns

# Gap
控制 grid 和 flexbox 元素之间的 gutter
* gap-0 -> gap: 0px
* gap-x-0 -> column-gap: 0px
* gap-y-0 -> row-gap: 0px
* gap-1 -> gap: 0.25rem
* gap-2 -> gap: 0.5rem
* other...

# Justify Content
控制 *grid* 和 *flexbox* 元素在 main axis 上面的对齐方式.  
对于 flexbox, 就是针对每一个元素的 content  
对于 grid, 就是针对每一列
* justify-start -> justify-content: flex-start
* justify-end
* justify-center
* justify-between
* justify-around
* justify-evenly

# Justify Items
控制 *grid* 元素在各自的 column 上面的对齐方式
* justify-items-start -> justify-items: start
* justify-items-end
* justify-items-center
* justify-items-stretch

# Justify Self
Override grid justify-items 的设置，设置 element 自己的 justify-item
* justify-self-auto
* jusitfy-self-start
* jusitfy-self-end
* jusitfy-self-center
* jusitfy-self-stretch

# Align Content
控制在 grid 或者 multi-row flexbox 里 row 的对齐方式
* content-center -> align-content: center
* content-start
* content-end
* content-between
* content-around
* content-evenly

# Align Items
控制在 grid 或者 flexbox 里元素在 cross axis 上元素的对齐方式
* items-start -> align-items: flex-start
* items-end
* items-center
* items-baseline
* items-stretch

# Align Self
元素覆盖容器 align-items 的设置
* self-auto
* self-start
* self-end
* self-center
* self-baseline
* self-stretch

# Place Content
同时设置 justify 和 align
* place-content-center -> place-content: center
* place-content-start
* place-content-end
* place-content-between
* place-content-around
* place-content-evenly
* place-content-stretch

# Place Items
* place-items-start -> place-items: start
* place-items-end
* place-items-center
* place-items-stretch

# Place Self
覆盖 container 的 place-items 设置


# Conclusion
对于 Flex 布局，可以设置 flex-direction, flex-wrap  
justity-content 表示在 main axis 上面元素的对齐方式， 当有多行，行与行之间的对齐方式通过 align-content 设置  
justify-items， justify-self 只作用于 grid 布局.  
align-items 设置元素在 cross axis 上的对齐方式。  
align-self 则会覆盖 container 的 align-items 设置。  
flexbox 里的元素可以随着容器 size 的变化而变化自己的 size, flex-grow 表示元素拉伸的值，值越大，拉伸所占的比例越大。flex-shrink 表示元素压缩的值，值越大，压缩的比例越大。flex-basis 表示元素的初始大小。 

flex 是 flex-grow flex-shrink flex-basis 的 shorthand
* flex-1(flex: 1 1 0%) 表示可拉伸，可压缩，丢弃初始大小
* flex-auto(flex: 1 1 auto) 表示可拉伸，可压缩，使用初始大小
* flex-initial(flex: 0 1 auto) 表示不可拉伸，可压缩，使用初始大小
* flex-none(flex: none) 表示不可拉伸，不可压缩，使用初始大小

对于 grid 布局，grid-cols-{n}, grid-rows-{n} 设置初始 列数和 行数，这里生成的行与列会等宽等高。  
col-span-{n}, row-span-{n} 设置 元素的 row/column span, col-start-{n}, row-start-{n}, col-end-{n}, row-end-{n} 设置元素 span 的起始位置和终止位置，索引从 1 开始，范围区间左闭右开。  
grid-flow-row, grid-flow-col, grid-flow-row-dense, grid-flow-col-dense 设置元素的自动摆放方式， grid-flow-row 就是按行摆放，必要时添加新行，grid-flow-col 就是按列摆放，必要时添加新列。dense 就是在摆放元素的时候，会先尝试放在前面的空白位置。  
对于隐式创建的行与列，它们的 size 受 auto-rows-min, auto-rows-max 等来控制。  

对于 grid, justify-content 是控制列之间的对齐方式，justify-items 是控制元素在自己列内部的对齐方式，justify-self 则是元素覆盖容器 justify-items 的设置。  
align-content 是用于行与行之间的对齐，align-items 则是用于元素在行内部 cross axis 上的对齐方式
