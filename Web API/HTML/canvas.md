# Canvas
Canvas 是一个画布，接受 width, height 属性，只允许在 canvas 上绘制 rectangle 和 path。  
左上角是坐标原点。

## Rectangle
* fillRect(x, y, w, h) -> 在 (x, y) 处以当前的 style 绘制一个 fill w, h 的矩形
* clearRect(x, y, w, h) -> 清空指定区域的绘制，使其透明
* stokeRect(x, y, w, h) -> 只绘制矩形的 outline

## Path
* beginPath() -> 创建一条路径，上下文会指向新建的 path, 然后可以通过图形绘制命令绘制图形
* closePath() -> 通过绘制一条从当前点到开始点的直线来关闭一条路径，optional
* stroke() -> 通过 outline 来绘制图形, 不会自动闭合图形
* fill() -> 通过填充路径内容区域生成实心图形,如果之前没有调用 closePath，fill 会自动闭合图形

### 图形绘制命令
* moveTo(x, y)
* lineTo(x, y)
* arc(x, y, radius, startAngle, endAngle, anticlockwise) -> x, y 是圆心坐标，radius 是半径，anticlockwise 标志是否逆时针绘制弧形，角度是弧度，且以x 轴为基准