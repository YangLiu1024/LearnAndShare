# Spacing 可分为 padding 和 margin
# Padding
设置元素 padding, 格式为 `p{t|b|r|l}-{size}`
* p-0
* p-px -> padding: 1px
* px-px -> padding-left: 1px;padding-right: 1px;
* py-px
* p-1 -> padding: 1rem
* pt-px
* pb-0.25
* pl-0.5
* pr-px

# Margin
和 padding 类似

# Space between
用来控制 children elements 之间的 space, 它本质上只是给元素添加 margin, space-x 就是添加 margin-left, space-y 就是添加 margin-top.  
对于复杂的 layout，比如 grid, flex, 最好使用 gap, 而不是 space
* space-x-0
* space-y-1
* other...