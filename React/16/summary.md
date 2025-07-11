# React 架构
React15 架构分为
* reconciler(协调器)，负责找出变化的组件
* renderer（渲染器），负责将变化的组件渲染到页面上
当 mount, 或者 update 时，React 会同步递归 vdom, 完成 reconciler 和 renderer(交替执行, 但不会中断，所以用户不会看到未完成的更新), 由于是递归更新，一旦开始就无法停止，当更新时间过长，则会出现卡顿。  

为了解决这个问题，React16 架构可以分为
* scheduler(调度器)，调度任务优先级，高优先级任务先执行
* reconciler, 负责找出变化的组件
* renderer, 负责渲染变化的组件到页面

## scheduler
scheduler 是独立于 react 的 package, 支持将所有任务放入 *taskQueue* 和 *timerQueue*, 根据优先级执行 task, 且可以在一个任务结束后，执行下一个任务前，check 是否需要挂起，让浏览器可以处理其它任务
## reconciler
为了将同步更新变为异步可中断更新，react16 reconciler 将更新工作从递归变成了可中断的循环过程。每次循环都会 check shouldYield
```js
function workLoopConcurrent() {
  // Perform work until Scheduler asks us to yield
  while (workInProgress !== null && !shouldYield()) {
    workInProgress = performUnitOfWork(workInProgress);
  }
}
```
而且，React16 中，reconciler 和 renderer 不再是交替工作，当 scheduler 开始执行 reconciler 任务时，每个任务会为变化的 vdom 打上代表 增/删/更新的标记，所有的任务都在内存中进行。只有当所有任务都完成，才会统一交给 renderer 进行更新

### Fiber
曾经用于递归的 vdom 数据结构已经不能够满足异步可中断更新的需求，Fiber 应运而生。  