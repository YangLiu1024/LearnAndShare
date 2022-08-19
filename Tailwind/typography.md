# Font Family
设置 font family
* font-sans
* font-serif
* font-mono

# Font Size
设置 font size, line height 是指文本行的高度
* text-xs -> font-size: 0.75rem; line-height: 1rem
* text-sm -> font-size: 0.875rem; line-height: 1.25rem
* text-base
* text-lg
* text-xl
* other...

# Font Style
* italic -> font-style: italic
* not-italic -> font-style: normal

# Font Weight
控制文本的粗细
* font-thin -> font-weight: 100
* font-extralight -> font-weight: 200
* font-light -> font-weight: 300
* font-normal
* font-medium
* font-semibold
* font-bold
* font-extrabold
* font-black

# Letter Spacing
控制字符之间的距离
* tracking-tighter -> letter-spacing: -0.05em
* tracking-tight -> letter-spacing: -0.025em
* tracking-normal
* tracking-wide
* tracking-wider
* tracking-widest

# Line Height
设置 文本行高, 除了绝对值，还可以设置相对值。该相对值是相对于 font-size
* leading-3 -> line-height: .75rem
* leading-4 -> line-height: 1rem
* other...
* leading-none -> line-height: 1
* leading-normal -> line-height: 1.5
* leading-loose -> line-height: 2

# List Style Type
控制 list item 的 style type
* list-none -> list-style-type: none
* list-disc -> list-style-type: disc
* list-decimal -> list-style-type: decimal

# List Style Position
控制 list marker 的位置
* list-inside -> list-style-position: inside
* list-outside

# Text Alignment
控制文本的对齐方式，这个和 word 里面文本的对齐方式概念一样
* text-left -> text-align: left
* text-right
* text-center
* text-justify

# Text Color
控制文本的颜色
* text-inherit -> color: inherit
* text-current -> color: currentColor
* text-tranparent -> color: transparent
* text-black -> color: rgb(0, 0, 0)
* text-slate-50
* other...

当想控制 opacity 时，可以添加 opacity modifier, such as *text-blue-500/50*

# Text Decoration
控制文本的 装饰 style
* underline -> text-decoration-line:underline
* overline
* line-through
* no-underline 

# Text Decoration Color
控制 decoration 的 color
* decoration-inherit -> text-decoration-color: inherit
* decoration-current
* decoration-transparent
* decoration-gray-100
* other...

# Text Decoration Style
设置 decoration style
* decoration-solid -> text-decoration-style: solid
* decoration-double
* decoration-dotted
* decoration-dashed
* decoration-wavy

# Text Decoration Thickness
控制 text decoration 的厚度
* decoration-auto -> text-decoration-thickness: auto
* decoration-from-font
* decoration-0
* decoration-1 -> text-decoration-thickness: 1px

# Text Underline Offset
设置 text underline decoration offset
* underline-offset-auto
* underline-offset-0 -> text-underline-offset: 0px
* underline-offset-1
* other...

# Text Transform
通过CSS 来 format 文本
* uppercase -> text-transform: uppercase
* lowercase
* capitalize
* normal-case -> text-transform: none

# Text Overflow
设置在元素内的文本的 overflow 行为.  
首先，当文本超过了元素大小，需要设置 overflow 来控制范围外的内容。如果不是 overflow-hidden, 那么 text-overflow 就不会起作用，因为实际上没有 overflow 发生。  
那么在 overflow-hidden 之后，范围外的文本被隐藏，这时候就可以指定 text-overflow: ellipsis 来让超出范围的单词变成 '...', clip 则是和 overflow-hidden 效果一样。  
至于 truncate, 多了一个 white-space: nowrap 则表示对于空白符，不需要 wrap, 所以文本在第一行就会被截断。
* truncate -> overflow: hidden; text-overflow: ellipsis; white-space:nowrap
* text-ellipsis -> text-overflow: ellipsis
* text-clip -> text-overflow: clip

# Text Indent
set the amount of empty space (indentation) that’s shown before text in a block.
* indent-0
* indent-px
* indent-1 -> text-indent: 0.25rem
* other...

# Vertical Alignment
vertical-align 可用于两种环境：  
1. 使行内元素 box 与行内容器垂直对齐，比如垂直对齐一行文本内的图片
2. 垂直对齐表格单元内容

需要注意的是，vertical-align 只对 inline, inline-block 和 table-cell 元素生效，不能使用它垂直对齐块级元素。
* align-baseline -> vertical-align: baseline
* align-top
* align-middle
* align-bottom
* align-text-top
* align-text-bottom
* align-sub
* align-super

# White Space
控制元素内文本空白符的行为.   
normal 表示当需要时，文本会自动在空白符处 wrap, 并且文本内的 空白符， 换行符会被 collapsed.   
nowrap 表示不会 wrap  
pre 表示会保留文本本身的空白符和换行符，且不会自动 wrap  
pre-line 表示会保留换行符，但是不保留空白符， 文本会自动 wrap。  
pre-line-wrap 表示保留换行符和空白符，并且本文会自动 wrap
* whitespace-normal -> white-space: normal
* whitespace-nowrap
* whitespace-pre
* whitespace-pre-line
* whitespace-pre-wrap

# Word Break
控制 word break 行为, 一般情况下，当空间不够时，文本会在空白符处 wrap。  
break-words 表示当 word 本身宽度就超过容器宽度时，直接 break word 显示，即 word 超过宽度的部分会 wrap 到下一行而不是超过范围。  
break-all 表示，不需要保留任何 word, 在任何需要的时候，都可以把任何 word break, 让超过范围的部分 wrap 到下一行
* break-normal -> overflow-wrap: normal; word-break: normal
* break-words -> overflow-wrap: break-word;
* break-all -> word-break: break-all