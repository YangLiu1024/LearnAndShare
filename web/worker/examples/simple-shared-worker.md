SharedWorker 可以在多个窗口，和多个脚本一起工作，但是需要指定 port
```js
// main1.js
const worker = new SharedWorker('task1.js')

// 发送消息
worker.port.postMessage([v1, v2])

// 接收消息
worker.port.onmessage = ({data}) => {

}

// task1.js
onconnect = (e) => {
    const port = e.ports[0]
    
    port.onmessage = ({data}) => {
        // 处理数据

        // 返回结果
        port.postMessage()
    }
}

// main2.js
// main2 和 main1 类似，task2.js 也和 task1.js 类似
```