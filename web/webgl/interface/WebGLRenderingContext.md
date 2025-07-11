# WebGLRenderingContext
该接口提供基于 openGL ES2.0 的绘图上下文，用于在 HTML *canvas* 元素内绘图
```js
// 获取上下文
const canvas = document.getElementById("myCanvas");
const gl = canvas.getContext("webgl");

// 也能从离屏 canvas 里获取
var offscreen = new OffscreenCanvas(256, 256);
var gl = offscreen.getContext("webgl");
gl.canvas; // OffscreenCanvas
```

# 上下文属性
* gl.canvas, 返回该上下文绑定的 canvas， 可能为 HTMLCanvasElement 或者 OffscreenCanvas, 或者 null
* gl.drawingBufferWidth, 等于绑定的 canvas 元素的宽度
* gl.drawingBufferHeight

# 上下文方法
## WebGlBuffer
buffer 用来存储顶点，颜色等数据
```js
// 创建并初始化一个用于储存数据的 WebGLBuffer 对象，该对象没有定义任何自己的方法或者属性，且内容不能被直接访问
const buffer = gl.createBuffer()

// bindBuffer
// 将缓冲区绑定到目标
// webgl 里，target 可以是 gl.ARRAY_BUFFER(存储数据的数组), 或者 gl.ELEMENT_ARRAY_BUFFER(存储元素索引的数组)
// webgl2 支持更多种类的 target
void gl.bindBuffer(target, buffer);

// 获取当前绑定的 buffer
gl.getParameter(gl.ARRAY_BUFFER_BINDING)
gl.getParameter(gl.ELEMENT_ARRAY_BUFFER_BINDING)

// 同样的，buffer 也可以被删除
gl.deleteBuffer(buffer)

// 在绑定好 buffer 到上下文之后，就可以操作该 buffer
// 填充数据
// target 可以是 gl.ARRAY_BUFFER 或者 gl.ELEMENT_ARRAY_BUFFER
// size 是 buffer 数据存储区大小
// data 可选参数，一个 array buffer, array buffer view 或者 shared array buffer 对象， 将被复制到 buffer 的数据存储区
// usage 指定存储区的用法
//     gl.STATIC_DRAW, 表示缓冲区的数据会被经常使用，但不会被修改。
//     gl.DYNAMIC_DRAW, 表示缓冲区的数据可能会经常被使用，并且经常更改。
//     gl.STREAM_DRAW, 缓冲区的内容可能不会经常使用
gl.bufferData(target, size, usage)
gl.bufferData(target, data？, usage)
gl.bufferData(target, data?, usage, srcOffset, length) // webgl2
```

## Shader
着色器分为顶点着色器和片段着色器, 用于指定顶点位置和颜色
```js
// 创建 shader
// type 可以是 gl.VERTEX_SHADER 或者 gl.FRAGMENT_SHADER
const shader = gl.createShader(type)

// 创建之后，需要给 shader 绑定 source code, source code 是 GLSL 代码
gl.shaderSource(shader, source)

// 指定代码之后，就需要进行 compile, 将 GLSL 代码转换为 二进制数据， 然后就可以被 WebGLProgram 使用
gl.compileShader(shader)
```

## Program
一个 WebGLProgram 对象由两个编译过后的 shader 组成，顶点着色器和片段着色器就可以组合成为一个可用的 WebGL 着色器程序
```js
const program = gl.createProgram()

// 绑定着色器
gl.attachShader(program, vertexShader)
gl.attachShader(program, fragmentShader)


// 绑定好之后，还需要 link program 到上下文里, 从而完成为程序的着色器准备 GPU 代码的过程
gl.linkProgram(program)

// 检查 link 状态
if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
    throw ''
}

// 在完成 GPU 代码准备后，需要将 program 添加到当前上下文里
gl.useProgram(program)

// 着色器也可以被解绑
gl.detachShader(shader)
```
## Location
着色器里需要通过 attribute 或者 uniform 访问 通过 js 传入的数据，那么这些数据是怎么互相绑定的呢，这就需要用到 location
```js
// 返回给定 program 对象中某属性的下标指向位置
GLint gl.getAttribLocation(program, name);

// 返回的属性并没有被激活，必须激活后才能使用，一旦激活，vertexAttributePointer(), vertexArrtib*(), getVertexAttrib() 才能获取到属性的值
// index 是返回的 location, 如果不知道索引值，只知道属性名字，可以通过 getAttribLocation() 来获取索引
// 简单来说，就是使用 enableVertexArrtibArray() 函数来激活属性，以遍在后面在着色器中对属性进行数据绑定
gl.enableVertexArrtibArray(index)

// 上面还只是初始化了属性，并没有把属性和数据绑定在一起， 这就需要 vertexAttribPointer 来完成
// index 是属性的索引值，表示告诉 GPU, 当访问 index 指向的属性数据时，从当前上下文绑定的 buffer 中获取
// size 表示一个顶点所对应的 buffer 数组元素个数，必须是 1，2，3，或 4
// type 是 buffer 的数据类型，可能是 gl.BYTE(8 位整数，-128 ~ 127), gl.SHORT, gl.UNSIGNED_SHORT, gl.FLOAT
// normalized flag 表示当转换为浮点数时，是否应该将数值归一化到特定的范围。对于无符号数，归一化到 [0, 1], 有符号数，归一化到 [-1, 1]
// stride 以字节为单位指定连续顶点属性开始之间的偏移量，通常为 0， 表示属性是紧密打包的
// offset 表示顶点属性数组中第一部分的字节偏移量 
vertexAttribPointer(index, size, type, normalized, stride, offset)

// 除了 属性之外，还可以指定 一些统一的数据
// uniform 属性可以被 both 顶点着色器和片段着色器访问
const loc = getUniformLocation(program, name)
// 在拿到 loc 后，就可以对它进行设置
// [1234] 表示要设置的值的维度，[fi] 表示值是 浮点数还是整数，[v] 表示是否为 vector 
gl.uniform[1234][fi][v](loc, v)
// examples
gl.uniform1f(loc, 1.0)
gl.uniform2i(loc, 2, 3)
gl.uniform3fv(loc, Float32List)

// 还可以为 uniform 指定矩阵值
// 2,3,4 表示二阶，三阶，四阶矩阵，分别应该是有 4，9，16个浮点数的数组
// transpose 指定是否为转置矩阵， 必须为 false
gl.uniform[234]fv(loc, transpose, m)
```
## Draw
在准备好数据和 program 后，需要调用 drawArrays 或者 drawElements 来真正的开始绘制
```js
// drawArrays
// mode 可以是 
//   gl.POINTS(点)
//   gl.LINE_STRIP(一系列线段，上一点连接下一点)
//   gl.LINE_LOOP(线圈，一系列线段，上一点连接下一点，并且最后一点连接第一点)
//   gl.LINES(一系列单独的线段，每两个点作为端点，段与段之间不连接)
//   gl.TRIANGLE_STRIP(三角带)
//   gl.TRIANGLE_FAN(三角扇)
//   gl.TRIANGLES(一系列三角形，每三个点作为顶点)

// first 指定从哪个点开始绘制
// count 指定绘制需要使用到多少个点
void gl.drawArrays(mode, first, count);


// drawElements
// mode 和 drawArrays 一样
// count 表示要渲染的顶点的个数
// type 指定元素数组缓存区中的值的类型，可以是 gl.UNSIGNED_BYTE 或者 gl.UNSINGED_SHORT
// offset 指定元素数组缓冲区中的偏移量，必须是给定类型大小的有效倍数
void gl.drawElements(mode, count, type, offset);
```
drawArrays 和 drawElements 里的 count 都是需要绘制的顶点的数量
## 辅助功能
上下文里还有一些常用的辅助功能
```js
// 清除缓冲区
gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT | gl.STENCIL_BUFFER_BIT)

// 设置清除颜色缓冲区的颜色值
gl.clearColor(red, green, blue, alpha)

// 启动一些 功能
gl.enable(gl.DEPTH_TEST)

// block until all previously called commands are finished
gl.finish()

// 返回当前发生的 error
gl.getError()

// 设置 view port
// 当第一次创建上下文时，视口的大小和 canvas 的大小是匹配的。然而，如果重新改变了 canvas 的大小，需要重新设置 webgl 的上下文
gl.viewport(x,y,width,height)
```