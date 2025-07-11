一个 worker 使用的简单示例
```js
// main.js
// 负责启动 worker, 发送消息，接收消息

const worker = new Worker('./worker.js')

// 主线程发送消息
worker.postMessage([v1, v2])

// 接收 worker 返回的消息
worker.onmessage = ({data}) => {
    // 处理 worker 返回数据 data
}


// worker.js

onmessage = ({data}) => {
    // 处理数据
    // ***

    // 返回结果
    postMessage(res)
}
```