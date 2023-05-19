# JS 单线程
浏览器是多进程的，一般有 core 进程，渲染进程等。渲染进程又分为多条子线程，比如渲染线程，JS 线程，DOM事件响应线程等。且渲染线程与 JS 线程是互斥的，当执行 JS 线程时，页面渲染时阻塞的。    
JS 在设计之初，就采用单线程设计，但是为了避免某些任务执行时间太长，导致渲染线程得不到执行，JS 采取异步执行的策略。  
异步即是指在主线程碰到异步任务时，会将异步任务的回调函数 push 到任务队列，等待之后再执行。当主线程执行完所有当前的同步代码，就会去遍历任务队列，从任务队列中提取任务执行。  
任务有两种类型，宏任务和微任务。宏任务是指 setTimeout, setInterval, I/O, *script*, 网络请求等触发的任务。微任务是指 Promise, process.nextTick 等触发的任务。  
事件循环就是指，首先取出一个宏任务，即 script tag 包含的js 代码，将该 script 压入执行栈。期间产生的任务压入任务队列。  
在执行栈结束后，去检查微任务队列，开始执行微任务。在执行微任务期间产生的其它任务也会压入任务队列。一直执行直到微任务队列为空。  
然后开始渲染 ui.  
渲染结束后，从宏任务队列里取出一个任务，压入执行栈开始执行。循环往复。  
可以参考 https://jsfiddle.net/sn9xrkeg/
```js
//定义了 4个 async 函数
async function af1(p) {
  console.log(p, 'async function1')
  return 'af1'
}

async function af2(p) {
  console.log(p, 'async function2')
  return 'af2'
}
async function af3(p) {
  console.log(p, 'async function3')
  return 'af3'
}
async function af4(p) {
  console.log(p, 'async function4')
  return 'af4'
}
// 在 aaf 函数中依次调用 af 函数，有的使用 await, 有的不使用
async function aaf(p = '') {
  await af1(p);
  af2(p).then(v => console.log(v));
  await af3(p);
  af4(p).then(v => console.log(v));
}
// 在以下执行顺序中，首先打印 1，
// 然后 执行 aff 函数，该函数是异步函数. 进入函数体，await af1
// 所以会执行 af1, 打印 'async function 1', 因为使用 await, 所以 aaf 里, af1 之后的所有代码被 push 到微任务队列里, 退出 aaf 的执行
// 然后 setTimeout 会将其中的代码，push 到宏任务队列里
// 然后打印 2，主线程当前同步代码执行完毕，进入任务轮询

// 首先从 微任务队列中，拉出 af1 之后的所有代码，继续执行，微任务队列现在为空，之后插入的微任务只能等待下一次轮询。
// af2 被调用，打印 'async function 2'，它的回调被 push 到 微任务队列里
// 然后继续执行 af3, 打印 'async function 3'， 因为是 await 调用，af3 之后的所有代码，被 push 到微任务队列里。现在微任务队列里有两个回调函数了。并且当前微任务执行结束。
// 因为微任务队列为空，尝试从宏任务中提取一个任务执行。但宏任务里是 setTimeout, 因为 ES6 约定 小于 某个界限(记不清具体多少值)的延迟都当作该值处理，所以这个时候宏任务的回调大概率还不会被触发。那么继续轮询微任务
// 第一个是 af2 的 then 回调，打印 'af2'
// 第二个是 af3 之后的代码，那么执行 af4, 打印 'async function 4', 其回调 push 到微任务队列。此时微任务队列里有一个回调函数。
// 尝试执行宏任务，发现还没好，继续执行微任务
// 打印 af4
// 等到 setTimeout 返回，开始执行宏任务
// 这个时候，打印 'time out 1', 然后继续执行 aff('tt') 函数
// aff('tt') 里，开始执行 af1, 打印 'tt async function 1', af1 之后的代码被 push 到 微任务队列里。现在 微任务队列里有三个回调函数了
// aff('tt') 执行结束，打印 'time out 2'
// 1 
// '' async function1
// 2
// '' async function2
// af2
// '' async function3
// '' async function4
// af4
// timeout1
// 'tt' async function1
// time out 2
// tt async function2
// af2

console.log(1)
aaf()
setTimeout(() => {
  console.log('time out 1');
  aaf('tt');
  console.log('time out 2');
}, 0)
console.log(2)
// 1
// async function 1
// 2
// async function 2
// async function 3
// af2
// async function 4
// af4
// time out 1
// tt async function 1
// time out 2
// tt async function 2
// tt async function 3
// af2
// tt async function 4
// af4
```
async function 本身其实也只是 function，函数体仍然会在主线程被执行。当在 async 函数中碰到 await, 如果await 后的表达式是一个函数，则继续执行该函数，如果是一个值，则把该表达式及其之后的代码作为回调函数，push 到微任务队列，在这个 调用链之上的其它的 await 函数则被暂停执行。
```js
// 比如把 setTimeout 的回调函数作为 async 函数调用，且 await aaf
// 首先 打印 time out 1
// 进入 await aaf 函数. 只有当 aaf 执行结束，才会把之后的代码加入到任务队列
// 
setTimeout(async () => {
  console.log('time out 1');
  await aaf('tt');
  console.log('time out 2');
}, [0])
```
比如当有多层 await 函数时
```js
// 1 '' 'async function1' 'f' 8 'ff' 10 [await ff]
// 2
// 'f' 88
// 8
// 'af1' 
async function ff() {
	console.log('ff', 10)
  return 10
}
async function f() {
  console.log('f', 8)
  await ff();
  console.log('f', '88')
  return 8
}
async function af1(p='') {
  console.log(p, 'async function1')
   console.log(await f())
  return 'af1'
}
console.log(1)
af1().then(v => console.log(v))
console.log(2)

```