# Worker
为了在 JS 里引入多线程的能力，引入了 `Web Worker` 这一功能。它可以在前端另开一个线程，去执行和 window, DOM 无关的一些代码，主要用于一些比较花费时间的计算业务。这样就不会卡住 main thread, 从而避免了网页卡顿。  
```js
// 创建 worker 需要传入一个命名的 js 文件，该文件包含将在 worker 线程中运行的代码。
// worker 在另一个全局上下文，而不是 当前的 window 里运行。所以，在 worker 里，需要使用 self 而不是 window 来指向全局上下文

// app.ts
const worker = new Worker(script: string | URI)
worker.postMessage({

})

worker.onmessage = ({data}) => {

}

// 终止一个 worker
worker.terminate()
// worker.ts
// self 其实也可以省略，因为在 worker 里，其上下文就是 self, 也就是 WorkerGlobalScope
self.onmessage = ({data}) => {

}
```
在 worker 里除了不能访问 window 和 DOM, 其它 window 之下的东西都是可以使用的，包括 websocket, 或者 indexeddb 等数据存储。  
worker 和主线程之间的数据传输通过消息机制进行，双方都使用 `postMessage` 方法来发送各自的消息，使用 `onmessage` 事件函数来处理接收到的消息。  
这个过程中传递的数据，并不是共享，而是被 ***复制***
## DedicatedWokerGlobalScope
专用的 worker 是标准的 worker, 表示仅在单一脚本中被使用，且仅能被首次生成它的脚本使用。即该 worker 在创建的时候，就和它传入的脚本绑定在一起。
## SharedWorkerGlobalScope
共享 worker 可以被多个脚本使用，即使这些脚本正在被不同的 window, iframe, worker 访问。
```js
// app.ts
const worker = new SharedWorker(script: string | URI)
// 共享 worker 和 专用 worker 最大的区别在于，
// 专用 woker 可以直接使用 worker.onmessage, worker.postMessage, worker.onerror 等来进行通信
// 而共享 worker 必须显示的使用 port 对象，该 port 对象是一个确切的打开的端口供 脚本和 worker 通信。在专用 worker 里，这一步是隐式的
// 在传递消息之前，端口必须被显示的打开。打开方式是使用 onmessage 事件处理函数

// 当端口连接被创建时，即设置 onmessage 事件处理函数时，worker 会调用 onconnect 事件处理函数来执行代码
// 这里会打开父线程 向 worker 通信的端口连接
worker.port.onmessage = ({data}) => {

}


// worker.ts
onconnection = (e) => {
    const port = e.ports[0]

    // 这里会打开 worker 向父线程通信的端口连接
    port.onmessage = ({data}) => {

    }
}

```

## 可转移对象
允许在 数据转移给 worker 或者从 worker 返回而无需复制，在转移后，数据在上一上下文将不再可用。这样可以确保可转移对象一次仅在一个上下文可用。
```js
const buffer = new ArrayBuffer(1024*1024*32).map((_,i) => i);
// postMessage(message: any, transfers: Transferable[])
// transfers 是支持 可转移的一些对象，这些对象在传输过程中，会被转移，而不是复制
worker.post(message, [buffer])
```

## practice in product
```js
// webpack5 允许你不需要配置 worker-loader 之类的东西，只需要通过以下方式来创建 web worker 就行
new Worker(new URL(url, import.meta.url))

// D4A 的实现，是首先有一个 WorkerManager, 它可以提交任务，参数包括为任务名字，以及对应的参数，返回值是一个 promise, 以及对应的 cancel 函数
interface IWorkerManager<R> {
    submit(name: string, params: any[]): [Promise<R>, () => void]
}
// 然后 在 IWorkerManager 的实现里，会有 IWorkerTask 的概念。manager 在接收到 task 之后，并不会立即执行，而是放到一个队列里，等待 schedule
// 当然 worker manager 会持有一个 web worker, 也负责接收 worker 传递回来的消息，然后把消息转发给对应的 task

// task 的 start 表示是把 message 发送到 worker, end 是返回最后的 promise 结果
export interface IWorkerTask<R> {
  start(): void;
  end(): Promise<R>;
}

// 真正的 worker 持有了一个拥有多个函数的对象，当接收到一个消息，就根据消息的 name 查找对应的函数，然后在执行函数，最后把执行结果发送给 manager
```