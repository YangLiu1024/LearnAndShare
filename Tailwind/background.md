# Background Attachment
控制 background image 在滚动时的行为.  
fixed 表示 background is fixed to view port, 即使元素本身有 scroll bar, background 也不会跟着 element scroll  
local 表示 background is fixed relative to the element's content. 当 element scroll 的时候，background 会和 element 一起 scroll。
* bg-fixed -> background-attachment: fixed
* bg-local
* bg-scroll

# Background Color
* bg-inherit
* bg-transparent
* bg-black
* other...

# Background Origin
设置元素 background is posotioned relative to borders, padding, and content
* bg-origin-border -> background-origin: border-box;
* bg-origin-padding
* bg-origin-content

# Background Position
控制 background image 的位置
* bg-bottom -> background-position: bottom
* bg-left-bottom
* bg-right-bottom
* bg-top
* bg-left-top
* bg-right-top
* bg-left
* bg-right
* bg-center

# Background Repeat
控制 background image 的 repeat 行为
* bg-repeat -> background-repeat: repeat
* bg-no-repeat
* bg-repeat-x
* bg-repeat-y
* bg-repeat-round
* bg-repeat-space

# Background Size
控制元素 background image 的大小
* bg-auto -> background-size: auto, 显示 image default size
* bg-cover -> background image 会在x, y 方向上拉伸或者压缩相同比例，直到 cover 整个元素
* bg-contain -> 通过拉伸或者压缩，只需要在任意方向 cover 就行

# Background Image
设置 background image, 或者通过 <em>style="background-image: url(...)"</em> 来设置
* bg-none
* bg-gradient-to-t
* bg-gradient-to-rt
* other...

# Gradient Color Stops
在设置 bg-gradient-to-{direction}, 使用了 gradient, 它需要指定 from color 以及 to color
* from-black
* other...
* to-black
* other...