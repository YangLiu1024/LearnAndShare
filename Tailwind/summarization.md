# 重点
## Display
元素的 display 方式有多种，常用的有
* inline
* block
* inline-block
* flex
* grid

## Position
元素的 position 有几种
* static -> 默认的元素流，依次摆放
* relative -> 没有脱离元素流，只是相对正常的位置，有一定的偏移
* absolute -> 脱离了元素流，相对于它最近的 non-static 父节点摆放
* fixed -> 脱离了元素流，相对于视窗摆放
* sticky -> 当元素原始位置在视窗内，则正常显示，当 scroll 后，元素会脱离视窗，则固定在视窗上显示

## Overflow
当元素的内容超过了父元素的大小，则超出的部分通过 overflow 来控制
* overflow-visible -> 超出部分始终可见
* overflow-hidden -> 超出部分被裁剪
* overflow-scroll -> 始终显示 x,y scroll bar, 超出部分通过 scroll 来显示
* overflow-x-scroll
* overflow-y-scroll
* overflow-auto -> 只有当内容超过大小时，才显示 scroll bar

## Flex
flex 容器本质上是一维的，只是可以通过 wrap 来换行。
### Flex 容器
* flex-row -> 设置 flex direction, 可以为 row 或者 col
* flex-wrap, flex-nowrap -> 设置是否可以 wrap
* justity-center -> 设置 justify-content, flex items 水平方向的对齐方式
* items-center -> 设置 align-items, flex items 垂直方向的对齐方式

### Flex 子元素
* grow-1 -> flex-grow, 子元素自己的拉伸系数，越大，则该子元素在拉伸时，占据空间越大
* shrink-1 -> flex-shrink, 子元素自己的压缩系数，越大，则该子元素在压缩时，压缩的越大
* basis-1, basis-1/2 -> flex-basis, 元素的初始大小

通常使用这三种参数的 shorthand
* flex-1 -> flex: 1 1 0%
* flex-none -> flex: none
* flex-initial -> flex: 0 1 auto
* flex-auto -> flex: 1 1 auto

## Grid
Grid 布局是二维的
* grid-cols-2 -> 设置初始的列数
* grid-rows-3 -> 设置初始的行数
* col-span-2 -> 元素所占列数, col-span-full 表示占据所有列，row-span-full 类似
* col-start-1 -> 元素所占列的起始索引，索引以 1 开始, row-start-2 类似
* col-end-4 -> 元素所占列的结束索引，该索引并不被 include， row-end-5 类似
* grid-flow-row -> 在 grid 里添加元素时，元素的摆放方式，row 表示按行摆放，在必要时添加新行，col 表示按列摆放，必要时添加新列。row-dense 表示首先尝试在之前的空白区域进行摆放，如果摆放不成功，才按行摆放，col-dense 类似
* justify-center -> grid 里 justify-content 是针对 columns 来说的
* justify-items-start -> 元素在 column 里的对齐方式
* justify-self-end -> 在元素上设置，以覆盖 grid 的 justify-items 设置
* content-between -> align-content, 设置行与行的对齐方式
* items-baseline -> align-items, 设置每行内的元素在垂直方向上的对齐方式

## Flex & Grid
justify-content 就是说，在水平方向，容器内的元素要怎么对齐。  
只是对于 flex 容器来说，容器内的元素就是填充的内容本身，而 grid 的元素是 列。  
justify-item 是针对于一行里面每个元素内部需要怎么水平对齐，对于 flex 来说，没有意义，对于 grid, 就是 column 里的元素需要怎么水平对齐。  
align-content 就是控制，在垂直方向，容器内的元素要怎么对齐。  
对于 flex 容器，当有 wrap 时，则表示行与行之间的对齐，当只有一行时，则没有意义。  
对于 grid, 则是表示 行与行之间的对齐。  
align-items 则是表示对于每一行，元素在行内要怎么垂直对齐。

## Text
* font-serif -> 设置文本 font family
* text-sm -> 设置 text font size 以及 line height
* text-black -> 设置 文本颜色
* font-bold -> 设置文本 weight, 越大文本越粗
* text-left -> text-align, 设置文本的对齐方式，这个和 word 里的文本对齐方式一样
* indent-1 -> text-indent, 设置文本在首行的缩进

## Text Decoration
* underline -> text-decoration-line, 设置 decoration style, 还可以是 overline, line-through
* decoration-solid -> 设置 decoration 的 样式, dotted, dashed, wave
* decoration-black -> 设置 decoration 的 颜色
* decoration-1 -> 设置 decoration 的 thickness
* underline-offset-1 -> 设置 underline 的 offset

## Text Overflow
当元素设置了 overflow-hidden 时，范围外的内容将被clip, 但是对于文本，这种行为一般会引起歧义，所以在此基础上，加入了 text-overflow 的设置。
* truncate -> text-overflow: ellipsis; white-space:nowrap
* text-ellipsos -> text-overflow: ellipsis;

truncate 设置了 white-space:nowrap 表示文本不会 wrap 空白符，那么所有的文本将会显示为一行，又设置了 text-overflow: ellipsis; 那么超出范围的文本将被显示为'...'.  

## White Space
对于元素内的文本，一般空白符和换行符会被 collapsed, 即多个连续的空白符或者换行符会被当作一个空白符来处理。  
* whitespace-normal -> 即默认行为
* whitespace-nowrap -> 空白符和换行符会被 collapsed, 并且空白符不允许 wrap, 即文本会显示为一行
* whitespace-pre -> 空白符和换行符不会被 collapsed, pre 是 preserve的意思，不允许自动 wrap
* whitespace-pre-line -> 表示保留换行符，且自动 wrap
* whitespace-pre-wrap -> 保留换行符和空白符，且自动 wrap
