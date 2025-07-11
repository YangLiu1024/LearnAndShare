# createRoot
how to use
```js
import { App } from "./app";
import { createRoot } from "react-dom/client";

const container = document.getElementById("root");

// This is the first step
const root = createRoot(container);

// Then, the second
root.render(<App />);
```
## 实现
```js
  // 首先创建 FiberRootNode
  const root = createContainer(
    container, // 原生 dom container element
    ConcurrentRoot,
    null,
    isStrictMode,
    concurrentUpdatesByDefaultOverride,
    identifierPrefix,
    onUncaughtError,
    onCaughtError,
    onRecoverableError,
    transitionCallbacks,
  );

  // createContainer 来自 react-reconciler
  // 首先，会创建一个 fiber root node
const fiberRoot = new FiberRootNode(
  container, // the host element
  tag, // ConcurrentRoot
);  
// 然后是 rootFiber node
 const unitializedFiber = new FiberNode(
  HostRoot, // tag
  null, // pendingProps
  null, // key
  mode, // deduced react mode (strict mode, strict effects, concurrent updates..)
);

fiberRoot.current = unitializedFiber;
unitializedFiber.stateNode = fiberRoot;

// finally 
return fiberRoot;
```
FiberRootNode 和 container element 相关联，FiberRootNode.current 会指向一颗 Fiber 树，即一个 FiberNode. 在渲染时，react 会维护两颗 fiber 树，一颗 fiber 树是上一次更新后的内容，称之为 current fiber 树，一颗是当前更新中的 fiber 树，称之为 workInProgress fiber 树。当更新完成，FiberRootNode.current 会指向 workInProgress fiber 树完成替换，workInProgress fiber 树成为 current fiber 树。  

## root.render()
FiberRootNode 创建完成时，它指向的 fiber 树还是空的。在调用 render 后，才会开始渲染。
```js
root.render() // 内部调用 updateContainer

function updateContainer(
    element, // 需要被渲染的 react node list
    container, // FiberRootNode
) {
    // 申请当前 fiber node
    const current = container.current; // 当前的 fiber 树
    // 会使用 current.mode 来判断需要什么等级的 lane
    // 如果不是 ConcurrentMode, 则使用 SyncLane
    // 如果是来自于状态改变引发的更新，则使用最高等级 lane
    // 如果状态改变来自于 transition, 则使用 TransitionLane
    const lane = requestUpdateLane(current); // 申请 lane 优先级

    // 创建一个 update, 该 update tag 为 UpdateState
    const update = createUpdate(lane);
    update.payload = {element}; // element 是传入的 <App>
    update.callback = callback;    

    // 把 update 添加到 current.shared.pending
    // HostRoot 被返回
    const root = enqueueUpdate(current, update, lane);

    // schedule update
    // 底层调用 scheduleImmediateTask(processRootScheduleInMicrotask)
    scheduleUpdateOnFiber(root, current, lane);

    // 如果 render 是被包裹在 setTransition 中，则会进入该方法
    entangleTransitions(root, current, lane);
}
// 在 updateContainer 结束时，react 其实还没有真正开始 render 组件，它做的所有事情就是 schedule the work via *queueMicrotask*

```
## How root render scheduled work
在上述 root.render() 中，会调用 *scheduleUpdateOnFiber*, 该函数会调用以下方法来做调度
```js
// React code was like this
scheduleImmediateTask(processRootScheduleInMicrotask);

// which will do something similar to this (in almost all cases)
queueMicrotask(processRootScheduleInMicrotask);
```
实际上，react 有一个 global firstScheduleRoot 变量，该变量的 pending 属性，存储了一些 pending 的 callback.  
the work loop 本质上就是 渲染组件，并将渲染后的组件更新到页面上。这个 loop 会被 root.render, 状态更新，suspense 等触发
```js
// ReactFiberRootScheduler.js

// A linked list of all the roots with pending work. In an idiomatic app,
// there's only a single root, but we do support multi root apps, hence this
// extra complexity. But this module is optimized for the single root case.
export let firstScheduledRoot: FiberRoot | null = null;
let lastScheduledRoot: FiberRoot | null = null;

function processRootScheduleInMicrotask() {
    let root = firstScheduledRoot;

while (root !== null) {
  const next = root.next;
  // 1
  entangleSyncLaneIfInsidePopStateEvent(root);
  // 2, // 返回 fiber root 中优先级最高的 lane, 并且 schedule render on current root
//   const newCallbackNode = scheduleCallback(
//   // NormalPriority for simple root.render
//   schedulerPriorityLevel,
//   performConcurrentWorkOnRoot.bind(null, root), // trigger concurrent work loop
// );

// root.callbackPriority = newCallbackPriority;
// root.callbackNode = newCallbackNode;
// return newCallbackPriority;
  const nextLanes = scheduleTaskForRootDuringMicrotask(root, currentTime);
  // 3
  if (nextLanes === NoLane) { // no pending work to do
    detachRootFromScheduledRoots(root);
  } else {
    // 4
    if (includesSyncLane(nextLanes)) {
      mightHavePendingSyncWork = true;
    }
  }
  root = next;
}

// 5
flushSyncWorkOnAllRoots();    
}

function flushSyncWorkOnAllRoots() {
    // trigger sync work loop
    performSyncWorkOnRoot(root, nextLanes)
}
```
## workloop
