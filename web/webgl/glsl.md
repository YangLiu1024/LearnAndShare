# GLSL
GLSL 为 shader 提供了三种不同作用的数据存储，每种都有一个特定的用例。每种数据作用不同，可以被一种或者全部 shader 访问

## attributes
attributes 属性可以被 js 代码操作，也可以在 vertex shader 中作为变量访问。  
attributes 通常用于存储颜色，纹理坐标，以及其它需要在 JS 和 vertex shader 之间互相传递的数据
```js
// 创建 attribute
const vColor = gl.getAttribLocation(program, "vColor");
// 创建 buffer, 存储数据
const buffer = gl.createBuffer()
gl.bindBuffer(buffer)
gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([...]), gl.STATIC_DRAW) // gl.STATIC_DRAW 表示该 数据只 set 一次，且不会更改，并会被多次使用

gl.vertexAttribPointer(vColor, 4, gl.FLOAT, false, 0, 0);
gl.enableVertexAttribArray(vColor);

const vertxt = `
attribute  vec4 vColor;

void main()
{
  fColor = vColor;
}
`
```
属性从缓冲区接收值，
## varyings
varyings 在 vertex shader 中定义，用于从 vertex shader 向 fragment shader 传递数据
## uniforms
uniform 是在 JS 代码中设置，且在 vertex 和 fragment shader 都可以访问的数据。使用 uniform 设置在一帧的所有绘制中相同的数据，例如光源，全局变换，透视数据等


# 着色器
## 顶点着色器
顶点着色器的任务，是把数据点映射到裁剪空间。可以访问 attribute, uniform, 以及定义 varying。varying 可以在 fragment 着色器中使用。  
最后返回的顶点坐标由 *gl_Position* 表示，是一个 *vec4*. 
## 片段着色器
片段着色器在顶点着色器处理完图形的顶点后，会被要绘制的每个图像的每个像素点调用一次。它的职责是确定像素的颜色，会考虑纹理，光照等因素。  
之后将颜色存储在特殊变量 *gl_FragColor* 中， 该颜色是一个 *vec4*
