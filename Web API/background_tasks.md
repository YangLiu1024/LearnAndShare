# 浏览器事件循环
浏览器是多进程，多线程架构，每个 tab 都是独立的渲染进程。我们关注的，是浏览器的渲染进程。该进程也有多个线程，其主线程用来执行 JS 代码，计算页面 CSS, 布局等。其它线程用来进行网络请求，定时任务等。虽然主线程即执行 JS 代码，又进行页面渲染，但是二者是交替进行的。也就是说，只有在停止执行 JS 代码时，主线程才会去渲染页面。这是为了保证页面渲染的有效性。因为 JS 代码是可以修改渲染树的，如果在渲染过程中，JS 代码更改了 DOM, 那么最后的渲染结果就会不可预见。为了避免出现页面渲染效果的不一致性，所以 JS 在浏览器中的实现，被设计为了 JS 执行阻塞渲染。  
需要注意的是，JS 引擎和 渲染引擎是否在同一个线程里，浏览器规范并没有指定。我们需要关心的，不是到底是不是同一个线程，而是需要知道，JS 引擎和 渲染引擎是互斥的就可以了。  
## JS 事件循环
主线程负责的事情很多，其核心机制就是事件循环，event loop. JS 是单线程的，这意味着一次只执行一个任务。事件循环的主要作用就是协调同步任务和异步任务的执行顺序。  
* 任务队列。任务队列(宏任务队列) 用来存放通过 setTimeout, setInterval, 网络请求，事件处理等，在 ready 后需要调用的回调。比如当 setTimeout 等待事件到了后，其回调就会被添加到任务队列。网络请求，事件处理也是一样的。
* 微任务队列。 微任务队列存放一些通过 Promise 回调，MutationObserver 等。  
事件循环的基本流程就是
* 执行调用栈里同步代码
* 检测微任务队列，依次执行微任务队列
* 如果调用栈为空，则表示当前任务执行结束。从任务队列取出下一个任务并执行
* 重复上述步骤  

除了执行JS 代码的主线程，渲染线程用来渲染页面，还会有一些其它线程。比如处理 setTimeout, setInterval 的线程，该线程在计时 ready 后，会把对应的回调添加到任务队列中，等待主线程执行。http 请求也会新开一个线程进行处理，等请求返回，也会把对应的回调添加到任务队列。

## 帧渲染机制
浏览器每一帧渲染过程通常包括以下几个阶段
* 样式计算，计算每个元素的 style
* 布局，layout，计算每个元素的位置和大小
* 绘制，paint, 将元素绘制在屏幕上
* 合成，composite, 将不同的图层合成在一起，生成最终的图像
浏览器在每一帧都会执行上述步骤，以确保页面的更新和渲染

## 事件循环和帧渲染
事件循环和帧渲染的关系可以通过以下几点来理解
* 任务执行和帧渲染是交替执行的。在每一帧的时间间隔内，事件循环执行完当前宏任务和所有微任务，浏览器会进行一次帧渲染。但如果两个宏任务间隔很近，有可能会被合并到同一个帧来处理。
* 事件循环的执行时间会影响帧渲染的频率，也就是说如果事件循环的执行时间太长，帧率就会降低，降到一定程度，就会导致卡顿或掉帧。

有两个回调可以介入事件循环和帧渲染过程，*requestAnimationFrame* 是在浏览器下次重绘前执行的回调，它和浏览器的帧率保持同步，*requestIdleCallback* 是在不需要重绘的帧内，且任务队列都为空的情况下，才会执行的回调。 
```js
console.log('开始执行');
// 等待时间为 0，通常该任务只需要等待 0-4ms 即可添加到任务队列
setTimeout(() => {
  console.log('setTimeout');
}, 0);

// 添加 animation frame, 在页面重绘前执行
requestAnimationFrame(() => {
  console.log('requestAnimationFrame');
});
// Promise 函数体同步执行，其回调添加到微任务队列
new Promise((resolve, reject) => {
  console.log('Promise');
  resolve('promise resolved');
}).then((v) => console.log(v));

// 添加 idle 回调
requestIdleCallback(() => {
  console.log('requestIdleCallback');
});
// 异步函数执行，其 await 之后的回调添加到微任务队列
(async function asyncFunction() {
  console.log(await 'asyncFunction');
})();

console.log('执行结束');

// 开始执行
// Promise
// 执行结束
// promise reoslved
// async function
// setTimeout 和 requestAnimationFrame/requestIdelCallback 的先后需要看 setTimeout 加入到任务队列的时机
// 总之，在重绘前，通常当前任务队列和微任务队列已经清空。这个时候执行 requestAnimationFrame。重绘结束后，如果当前任务队列没有任务，则可以直接 idle callback
```
```js
    const box = document.getElementById('box');

    // 模拟一个长时间运行的任务
    function longTask() {
      const start = Date.now();
      while (Date.now() - start < 50) {
        // 模拟耗时操作
      }
    }

    // 使用 requestAnimationFrame 进行动画
    function animate() {
      box.style.transform = `translateX(${Math.random() * 100}px)`;
      requestAnimationFrame(animate);
    }

    // 执行长时间运行的任务, 这里会执行 50ms, 导致帧率下降
    longTask();

    // longTask 执行结束后，开始动画
    requestAnimationFrame(animate);
```