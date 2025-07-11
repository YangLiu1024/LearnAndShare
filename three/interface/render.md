# 渲染器
Three.js 支持多种渲染器
```js
export interface Renderer {
    // 最后生成的 canvas element
    domElement: HTMLCanvasElement;

    // render 场景
    render(scene: Object3D, camera: Camera): void;
    // 设置渲染区域
    setSize(width: number, height: number, updateStyle?: boolean): void;
}
```
## WebGLRender
WebGLRender 使用 WebGL 来渲染场景
```js
// 当不提供参数时，它将采用合理的默认值
WebGLRender(parameters?: Object)
// parameters 可以包含以下属性
```
### Parameters 属性
* canvas, 一个供渲染器绘制的 canvas, 如果不提供，则会默认创建一个 canvas. 与 domElement 对应
* antialias, 是否执行抗锯齿，默认为 false

一般只需要调用渲染器的 render 方法

## WebGLRenderTarget
render target 是一个 buffer, 一般不使用

## 