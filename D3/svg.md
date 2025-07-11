SVG(Scalable Vertor Graphics) provide options to draw different shapes such as Lines, Rectangles, Circles, Ellipses etc

# Shapes
```xml
<svg height="300" height="300"></svg>
```

create a line start at (100, 100) and ending at (200, 150) and set color for line
```xml
<svg height="300" height="300">
    <line x1="100" y1="100" x2="200" y2="150" style="stroke:rgb(255,0,0);stroke-width:2"></line>
</svg>
```

note that the origin point of svg is the left top corner (0, 0), the horizontal axis is x, the vertical axis is y

to create a line using d3, 
```js
d3.select('svg')
  .append('line')
  .attr('x1', 100)
  .attr('y1', 100)
  .attr('x2', 200)
  .attr('y2', 150)
  .style('stroke', "rgb(255,0,0)")
  .style('stroke-width', 2)
```

create a rectagle element with d3
```js
d3.select('svg')
.append('rect')
.attr("x", 20)//x,y is the top-left point
.attr("y", 20)
.attr("width", 200)
.attr("height", 100)
.attr("fill", "green");
```

create a circle element with d3
```js
d3.select('svg')
.append("circle")
.attr("cx", 200)//center x
.attr("cy", 50)//center y
.attr("r", 20)//radius
.attr("fill", "green");
```

create a ellipse element with d3
```js
d3.select('svg')
.append("ellipse")
.attr("cx", 200)
.attr("cy", 50)
.attr("rx", 100)//radius for x direction
.attr("ry", 50)//radius for y direction
.attr("fill", "green")
```

create a text element with d3
```js
d3.select('svg')
.append('text')
.attr('x', 100)
.attr('y', 100)
.attr('dx', 10)
.attr('dy', '.5em')
.text('hello world')
```

create a path element.

*<path>* could be used to create both basic/advanced shapes, its defined by its attribute *d*

属性 d 的值是一个 "命令 + 参数" 的序列，每一个命令都用一个关键字母来表示，跟在字母后面的就是该命令需要的参数
```js
M = move to, 需要指定 x, y, 但只是移动到指定位置，并不会画线，一般用来指明从何处开始画
L = line to, 需要指定 x, y, 从当前位置画线到指定 x, y
H = horizontal line to
V = vertical line to
C = curve to
S = smooth curve to
Q = quadratic Bezier curve
T = smooth quadratic bezier curve
A = elliptical arc
Z = close path

注意上面所有命令都允许小写字母。大写表示绝对定位，小写表示相对定位
```
take rectangle as example
```js
let svg = d3.select('#container')
.append('svg')
.attr('height', 500)
.attr('width', 500)

svg.append('path')
.attr('d', 'M 10 10 H 90 V 90 H 10 L 10 10')
.style('fill', 'blue')

//还可以通过 z 来简化, 因为 Z 表示回到起点
svg.append('path')
.attr('d', 'M 100 10 H 180 V 90 H 100 Z')
.style('fill', 'yellow')

//还可以通过相对定位来定义, 小写字母的命令参数不是指定一个明确的坐标，而是相对于它前面的点需要移动多少距离
svg.append('path')
.attr('d', 'M 10 100 h 80 v 80 h -80 Z')
.style('fill', 'red')
```

![](./images/path-rectabgle.png)

d3 对 svg path　也有适配　d3-path

```js
let path = d3.path()//create a path

path.moveTo(0, 0)

path.lineTo(10, 10)

path.arcTo(x1, y1, x2, y2, radius)

path.arc(x, y, radius, startAngle, endAngle)//x, y is the center

path.rect(x, y, w, h)

path.closePath()

path.toString(),//return the string represention of this path
```


# viewbox & viewport
先看一个最简单的例子, svg 设置了宽度，高度，但不设置 viewBox
```html
<!-- 这里 svg 和 rect 都是使用的 px 单位-->
<!-- svg 大小是 500 x 200，rect 是从 20,10 开始，大小为 10，5 矩形，如下图所示-->
<!-- 可见矩形是很小的-->
<svg width="500" height="200">
    <rect x="20" y="10" width="10" height="5"
          style="stroke: #000000; fill:none;"/>
</svg>
```
![](./images/svg-no-viewbox.png)

```html
<!-- 在设置 viewBox = "0 0 50 20" 后，即表示 svg 原本 0，0，500，200 的坐标系，在底层其实是使用 0 0 50 20-->
<!-- 换句话说，svg 内部的元素，它每一个 unit，在 x 方向， 其实是对应着 500 / 50，即 10 个 pixel, 在高度上，每一个单位对应着 200 / 20， 即 10 个 pixel -->
<!-- 对于 rect 来说，其起点则变成 200， 100， 宽度也变成 100， 高度变成 50 pixel-->
<svg width="500" height="200" viewBox="0 0 50 20">
    <rect x="20" y="10" width="10" height="5"
          style="stroke: #000000; fill:none;"/>
</svg>
```
![](./images/svg-with-viewbox.png)
```html
<!-- 对于 平移，它是相对于 用户坐标系而言的 -->
<!-- 可以理解为先进行平移，再进行缩放-->
<svg width="500" height="200" viewBox="10 10 50 20" >
    <rect x="20" y="10" width="10" height="5"
          style="stroke: #000000; fill:none;"/>
</svg>
```
![](./images/svg-with-pan.png)
如果设置了 viewBox, 但是不设置 viewport, 会发生什么呢？  
首先 svg 在保持 viewBox 中 width/height ratio 的同时，会尽量占满可用空间, 在确定 svg viewport 的 width,height 后则和之前一样
```html
<svg viewBox="10 10 50 20" >
    <rect x="20" y="10" width="10" height="5"
          style="stroke: #000000; fill:none;"/>
</svg>
```

## preserveAspectRatio
如果 viewport 和 viewbox 的 aspect ration 不一样，那么就需要使用该属性来控制 svg 的渲染行为。  
refer to ![Doc](https://jenkov.com/tutorials/svg/svg-viewport-view-box.html)

## 总结
如果不设置 viewBox, 那么 svg 内部元素就在 width 和 height 对应的坐标系下进行绘制，没有平移缩放。  
设置 viewBox 后，首先根据 x, y 在坐标系下进行平移，然后根据 svg 的 viewport width,height 以及 viewbox 里的 width, height 的 ratio 进行缩放。  
举例来说，如果 view port 是 200， 100， viewBox 是 50，20， 那么 svg 里元素的每个单位在 x 方向上相当于 200 / 50 = 4 个像素，在 y 方向上，每个单位相当于 100 / 20 = 5 个像素。图形进行了放大。  
如果 viewBox 是 400，200， 那么每个单位则在 x 方向上相当于 200 / 400 = 0.5 个像素，在 y 方向上相当于 100 / 200 = 0.5 个像素，图形进行了缩小。 
