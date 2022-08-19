# Cursor
设置当 hover 在元素上的 鼠标形状
* cursor-auto
* cursor-default
* cursor-pointer
* cursor-wait
* cursor-text
* cursor-move
* cursor-help
* cursor-not-allowed
* cursor-progress
* cursor-none
* cursor-context-menu
* cursor-cell
* cursor-crosshair
* cursor-vertical-text
* cursor-alias
* cursor-copy
* cursor-no-drop
* cursor-grab
* cursor-grabbing
* cursor-allow-scroll
* cursor-col-resize
* cursor-row-resize
* cursor-n-resize
* cursor-e-resize
* cursor-w-resize
* cursor-s-resize
* cursor-zoom-in
* cursor-zoom-out

# Caret Color
设置 text input 里 caret 的颜色
* caret-black
* caret-gray-200
* other...

# Pointer Events
* pointer-events-auto -> 响应鼠标事件
* pointer-events-none -> 不响应鼠标事件，但鼠标事件仍然会传给 child element

# User Select
控制 user 是否可以 select text in an element
* select-none -> user-select: none, 不允许在当前元素以及子元素选择 text
* select-text
* select-all -> 当用户点击时，选中所有文本
* select-auto -> 使用 browser 默认 select 行为