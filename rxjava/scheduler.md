# Scheduler
如果想给 observable 操作符链添加多线程功能，可以使用指定操作符在特定的调度器上执行。  
## observeOn
observeOn 指示一个 observable 在一个特定的调度器上执行订阅者的 onNext, onError 和 onCompleted 方法
## subscribeOn
指示 observable 将全部的处理过程，包括发射数据和通知放在特定的调度器上执行

## 调度器的种类
1. Schedulers.computation(), // 用于计算任务，如事件循环和回调处理。默认线程数等于处理器的数量
2. Schedulers.from(executor) // 使用指定 Executor 作为调度器
3. Schedulers.immediate() // 在当前线程立即执行任务
4. Schedulers.io() // 用于 IO 密集型任务，如异步阻塞 io 操作
5. Schedulers.newThread() // 为每一个任务创建一个新线程
6. Schedulers.trampoline() // 当其它排队的任务完成后，在当前线程排队开始执行