OffscreenCanvas 可以在 web worker 里进行绘制， 而不会阻塞主线程
```html
<canvas id="main"></canvas>
<canvas id="worker"></canvas>
```
main canvas 在主线程渲染，worker canvas 在 offscreen canvas 里渲染
```js
// main.js
const canvasA = document.getElementById("main");
const canvasB = document.getElementById("worker");

function drawCanvasA() {
    // 通过 canvasA 获取 context, 继而画画
}


const worker = new Worker('worker.js')

// 会返回一个 OffscreenCanvas object
const canvasWorker  = canvasB.transferControllToOffscreen()
// canvasWorker 是 transfer 对象
worker.postMessage({canvas: canvasWorker}, [canvasWorker])
```
```js
// worker.js
let canvas = null

self.onmessage = ({data}) => {
    // 获取 canvas 引用
    canvas = data.canvas

    // 在 worker 里 draw canvas

}
```
需要注意的是，
* 当 worker 因为一些 heavy 的 task 没有重新绘制 canvas 时，canvas 会被 blocked. 但是并不会 block 主线程的绘制，而且 canvas 仍能响应 hover 等交互
* 当主线程被 heavy task 阻塞时，主线程的 canvas 也会停止更新，且所有元素的交互都会被 blocked. 但 worker 里的 canvas 仍然可以更新