# React Fiber 架构
在 React16 之前，React 是采用同步性渲染的模式，当状态更新导致组件重新渲染时，React 需要重新构建整个虚拟树，并与之前的 虚拟树进行比较找出 diff, 然后将这些 diff 应用到实际的 DOM 上。如果这个过程非常耗时，那么渲染期间用户将无法与应用交互，这无疑会降低用户体验。  

## Fiber 架构
### 大任务问题
VDOM 的更新是一个大任务，那么就需要将它分解为多个小任务, 每个小任务就是一个 fiber node。之前是通过递归的遍历 vdom, 导致不可中断，那么就需要舍弃树结构，转为另一种数据结构，react 采用了链表把每个 fiber node 关联起来，这就组成了 fiber 树。
### 调度的问题
解决了 任务 breakdown 的问题，剩下的就是怎么调度这些任务。原则上，我们希望在浏览器空闲的时候来执行这些任务，*requestIdleCallback* 比较符合需求，但是这个 api 存在兼容性问题，而且它优先级过低，不符合 React 的预期。因此，React 封装了一个调度器 *Scheduler*

### Fiber
```js
export type Fiber = {
    tag: WorkTag;
    key: null | string;
    return: Fiber | null; // return to current parent fiber, 用 return 而不是 parent, 是因为 fiber node 作为一个工作节点，在完成工作后，会返回下一个节点，所以使用 return
    child: Fiber | null;
    sibling: Fiber | null;
    lanes: Lanes;
    childLanes: Lanes;
}

function FiberNode(
    tag: WorkTag,
    pendingProps: mixed,
    key: null | string,
    mode: TypeOfMode,
) {
  // 作为静态数据结构的属性
  this.tag = tag; // 组件类型，Function, Class, Host...
  this.key = key;
  // 大部分情况下和 type 相同，如果 使用 react.memo, 则不同
  this.elementType = null;
  // 函数组件，则是函数本身，class 组件则是 class, 原生组件，则是 tag name
  this.type = null;
  // fiber node 对应的真实 dom 节点
  this.stateNode = null;

  // 用于连接其他Fiber节点形成Fiber树
  this.return = null;
  this.child = null;
  this.sibling = null;
  this.index = 0;

  this.ref = null;

  // 作为动态的工作单元的属性
  // 保存本次更新造成的状态改变相关信息
  this.pendingProps = pendingProps;
  this.memoizedProps = null;
  this.updateQueue = null;
  this.memoizedState = null;
  this.dependencies = null;

  this.mode = mode;

  // 保存本次更新会造成的 dom 操作
  this.effectTag = NoEffect;
  this.nextEffect = null;

  this.firstEffect = null;
  this.lastEffect = null;

  // 调度优先级相关
  this.lanes = NoLanes;
  this.childLanes = NoLanes;

  // 指向该fiber在另一次更新时对应的fiber
  this.alternate = null;
}
```
每个 Fiber 节点对应了一个 React Element, 保存了该组件的类型(函数组件，类组件，原生组件...), 以及对应的 DOM 节点等静态信息。除此之外，Fiber 节点还保存了本次更新中该组件改变的状态，要执行的工作(需要被删除，被插入，被更新...)等动态信息。  
Fiber 节点是怎么连接起来的呢，举个例子
```html
function App() {
  return (
    <div>
      i am
      <span>KaSong</span>
    </div>
  )
}
```
对应的 fiber 树结构
![](./assets//fiber.png)  

### 双缓存
使用 Fiber 树后，怎么把 Fiber 树 和 DOM 有什么关系？React 又是怎么通过Fiber 树更新 DOM 的呢？这需要使用 *双缓存* 的技术。  
React 中最多会同时存在两棵 Fiber 树，一颗是当前页面显示内容对应的 Fiber 树，被称之为 current Fiber 树，另一棵正在内存中构建的 Fiber 树称之为 workInProgress Fiber 树。  
current fiber 树中的节点，在构建 workInProgress Fiber 树的时候，可能会被复用(diff 算法判断无 diff)，通过 *alternate* 属性连接，即
```js
 workFiberNode.alternate = currentFiberNode
 currentFiberNode.alternate = workFiberNode 
```
React 应用的 root node 通过 current 指针在不同的 Fiber 树的 rootFiber 节点之间切换，即当 workInProgress Fiber 树完成构建，并交给 Renderer 渲染在页面后，应用根节点的 current 指针就指向 workInProgress Fiber 树，此时 workInProgress Fiber 树就变成 current Fiber 树。  
每次状态更新都会产生新的 workInProgress Fiber 树, 通过 应用根节点的 current 指针替换，完成 DOM 更新。
#### mount
首次执行 *ReactDOM.render()* 时，React 会构建 fiberRootNode(应用根节点) 和 rootFiber(current Fiber 树节点)。  
首屏加载时，页面还没有挂载任何 DOM, 所以 此时 rootFiber 还没有任何节点。接下来进入 reconciler 阶段，根据组件返回的 JSX 构建对应的 workInProgress Fiber 树，构建完成后，在 commit 阶段 渲染到页面，此时 DOM 更新为 workInProgress Fiber 树所对应的 内容，workInProgress Fiber 树也变成了 current Fiber 树。
#### update
接下来任意的状态更新，都会导致新一轮的 render(reconciler 阶段，执行函数，diff JSX) 和 commit(把 render 结果渲染到页面上), 最后 workInProgress Fiber 树变成 current Fiber 树

### Reconciler(render 阶段)
render 阶段其实就是执行执行函数，生成 JSX, diff, 找出发生变化的 Fiber node
#### workLoop
```js
function workLoopSync() {
  // Perform work without checking if we need to yield between fiber.
  while (workInProgress !== null) {
    performUnitOfWork(workInProgress);
  }
}

function workLoopConcurrent() {
  if (workInProgress !== null) {
    const yieldAfter = now() + (nonIdle ? 25 : 5);
    do {
      performUnitOfWork(workInProgress);
    } while (workInProgress !== null && now() < yieldAfter);
  }
}

function workLoopConcurrentByScheduler() {
  // Perform work until Scheduler asks us to yield
  while (workInProgress !== null && !shouldYield()) {
    performUnitOfWork(workInProgress);
  }
}

// performUnitOfWork 就是对当前 Fiber 节点执行 beginWork, 如果当前节点的 next 节点为 null, 表示执行到叶子节点，则开始执行 completeUnitOfWork
function performUnitOfWork(unitOfWork: Fiber): void {
  const current = unitOfWork.alternate;

  let next;
  // 开始当前节点的 work, 根据传入的  Fiber 节点创建子 Fiber 节点，并将两个节点连起来
  next = beginWork(current, unitOfWork, entangledRenderLanes);

  unitOfWork.memoizedProps = unitOfWork.pendingProps;
  if (next === null) {
    // If this doesn't spawn new work, complete the current work.
    // 结束当前节点的 work
    // 如果当前节点有兄弟节点，则进入兄弟节点的 beginWork, 如果不存在，则进入父节点的 completeUnitWork
    completeUnitOfWork(unitOfWork);
  } else {
    workInProgress = next;
  }
}

// 执行 commit 阶段
export function performWorkOnRoot(
  root: FiberRoot,
  lanes,
  forceSync: boolean
) {
  // 首先检查是否需要 shouldTimeSlice
  let exitStatus = shouldTimeSlice
    ? renderRootConcurrent(root, lanes)
    : renderRootSync(root, lanes, true);
  // 省略
  finishConcurrentRender() // 调用 commitRoot
}
```
#### beginWork
```js
// beginWork 和  completeUnitWork 交替执行，直到最后 rootFiber completeUnitOfWork. 至此，render 阶段结束
function beginWork(
  current: Fiber | null, // 上一次更新时的 Fiber 节点，在首次 mount 时为 null, update 时不为 null
  workInProgress: Fiber,// 当前更新的 workInProgress Fiber node
  renderLanes: Lanes
): Fiber | null {
  // ...省略函数体
  if (current != null) {
    // 表示是 update
    // 首先检测 current.memorizedProps 和 workInProgress.pendingProps 是否相同
    // 然后检查是否有 legacy context 改动
    // 如果有，则表示需要更新，如果没有，则直接复用之前的 current.child 作为 workInProgress.child
  } else {
    // 表示是 mount
  }
  // 根据 fiber node 的 tag 来更新 workInProgress 节点 并创建新的 子 Fiber 节点
    switch (workInProgress.tag) {
      case FunctionComponent:
      case ClassComponent:
      case HostRoot:
      case HostComponent:
      ...
    }
    // 最终会进入 reconcileChildren 方法  
}

export function reconcileChildren(
  current: Fiber | null,
  workInProgress: Fiber,
  nextChildren: any,
  renderLanes: Lanes,
) {
  if (current === null) {
    // If this is a fresh new component that hasn't been rendered yet, we
    // won't update its child set by applying minimal side-effects. Instead,
    // we will add them all to the child before it gets rendered. That means
    // we can optimize this reconciliation pass by not tracking side-effects.
    // mountChildFibers 不会给生成的 fiber 节点带上 effectTag 属性，只会在最后把所有子 Fiber node 赋值给 child
    // 如果所有 mount 的 fiber node 都赋予 effectTag(首屏加载时)，那么在 commit 阶段每个节点都会执行一次插入操作，这样大量操作 DOM 是极低效的
    workInProgress.child = mountChildFibers(
      workInProgress,
      null,
      nextChildren,
      renderLanes,
    );
  } else {
    // If the current child is the same as the work in progress, it means that
    // we haven't yet started any work on these children. Therefore, we use
    // the clone algorithm to create a copy of all the current children.

    // If we had any progressed work already, that is invalid at this point so
    // let's throw it out.
    workInProgress.child = reconcileChildFibers(
      workInProgress,
      current.child,
      nextChildren,
      renderLanes,
    );
  }
}

// render 阶段如果 fiber 需要更新，会赋予 effectTag, 只有有 effectTag 的 fiber node 才需要 renderer
// DOM需要插入到页面中
export const Placement = /*                */ 0b00000000000010;
// DOM需要更新
export const Update = /*                   */ 0b00000000000100;
// DOM需要插入到页面中并更新
export const PlacementAndUpdate = /*       */ 0b00000000000110;
// DOM需要删除
export const Deletion = /*                 */ 0b00000000001000;
```
![](./assets/beginWork.png)

#### completeWork
beginWork 会创建子节点，节点上可能赋予 effectTag, completeWork 类似于 beginWork, 也是基于 fiber.tag 调用不同的处理逻辑
```js
function completeWork(
  current: Fiber | null,
  workInProgress: Fiber,
  renderLanes: Lanes,
): Fiber | null {
  const newProps = workInProgress.pendingProps;

  switch (workInProgress.tag) {
    case IndeterminateComponent:
    case LazyComponent:
    case SimpleMemoComponent:
    case FunctionComponent:
    case ForwardRef:
    case Fragment:
    case Mode:
    case Profiler:
    case ContextConsumer:
    case MemoComponent:
      return null;
    case ClassComponent: {
      // ...省略
      return null;
    }
    case HostRoot: {
      // ...省略
      updateHostContainer(workInProgress);
      return null;
    }
    case HostComponent: {
      // ...省略
      return null;
    }
  // ...省略

// 以 HostComponent 为例
    case HostComponent: 
      const type = workInProgress.type;
      if (current !== null && workInProgress.stateNode != null) {
        // update 的情况
        // 这个时候 Fiber 节点对应的 DOM 节点已经生成，所以不需要生成 DOM 节点，这里需要做的就是处理 props
        // 比如 onClick, onChange 等回调函数的注册
        // 处理 style
        // 处理 children prop
        // 最后被处理完的 props 会被赋值给 workInProgress.updateQueue, 并最终会 commit 阶段被渲染到页面上
        updateHostComponent(...);
      } else {
        // mount 的情况
        // 为 Fiber 节点生成对应的 DOM 节点，即 workInProgress.stateNode
        const instance = createInstance(...)
        // 把子孙节点的 DOM 节点插入刚生成的 DOM 节点中
        // 当最后执行 rootFiber 的 completeWork 时，一个完整的 离屏 DOM 树已经构建完成，然后只需要给 rootFiber 设置 effectTag, 渲染器只需要执行一次 DOM 插入操作
        appendAllChildren(...)
        workInProgress.stateNode = instance
      }
```
#### effectList
至此 render 阶段的工作基本上已经完成了，commit 阶段需要找到所有 有 effectTag 的 fiber node 并以此执行 effectTag 对应的操作，但如果在 commit 阶段再次遍历一次 Fiber 树的话比较低效，所以在 completeWork 阶段，每个执行完 completeWork 且存在 effectTag 的 fiber 节点会被保存到一条称为 *effectList* 的单向链表中。  
这样在 commit 阶段，就只需要遍历 effectList 就可以了。  
![](./assets/completeWork.png)  

### commit 阶段
commit 阶段的主要工作分为三部分
* before mutation 阶段，执行 DOM 操作前
* mutation 阶段，执行 DOM 操作
* layout 阶段，执行 DOM 操作后
在 reconciler 阶段结束后，就需要进行 commit 阶段。
```js
function commitRoot(
  root,
  finishedWork,
) {
    do {
    // `flushPassiveEffects` will call `flushSyncUpdateQueue` at the end, which
    // means `flushPassiveEffects` will sometimes result in additional
    // passive effects. So we need to keep flushing in a loop until there are
    // no more pending effects.
    // 执行 useEffect 回调，因为这些任务可能触发新的渲染，所以要一直遍历直到没有任务
    flushPendingEffects();
  } while (pendingEffectsStatus !== NO_PENDING_EFFECTS);

 // before mutation phase
    try {
      // The first phase a "before mutation" phase. We use this phase to read the
      // state of the host tree right before we mutate it. This is where
      // getSnapshotBeforeUpdate is called.
      commitBeforeMutationEffects(root, finishedWork, lanes);
    } finally {
      // Reset the priority to the previous non-sync value.
    }

// mutation phase
 // 调用 commitMutationEffects, 遍历 effectList, 处理 fiber 节点的 effectTag, 且执行 useLayoutEffect 的销毁函数
    flushMutationEffects()
// layout phase
// 该阶段的代码都是在 DOM 修改完成后执行，该阶段可以参与 DOM layout
// 调用 useLayoutEffect 的回调函数
    flushLayoutEffects()
}

// 在 before mutation 阶段，会遍历 effectList, 依次执行
  // 处理 DOM 节点渲染删除后的 autoFocus, blur 逻辑
  // 调用 getSnapshotBeforeUpdate 钩子
  // 调度 useEffect
function commitBeforeMutationEffects() {
  // 省略
if ((effectTag & Passive) !== NoEffect) {
  if (!rootDoesHavePassiveEffects) {
    rootDoesHavePassiveEffects = true;
    // 通过 scheduler 调度 useEffect 回调，这里通过异步调用，是因为 effect 函数需要在浏览器绘制之后执行
    scheduleCallback(NormalSchedulerPriority, () => {
      // 触发useEffect
      flushPassiveEffects();
      return null;
    });
  }
}
}


```