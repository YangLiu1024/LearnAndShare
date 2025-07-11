# React 时间切片
react 时间切片的目的，在于能够
* 暂停 JS 执行，让主线程可以执行渲染任务，浏览器可以更新页面
* 在未来某个时刻，可以继续调度任务，执行上次没执行完的任务
而宏任务会在下次事件循环中执行，不会阻塞本次页面渲染更新。所以使用宏任务包裹剩下未完成的 task 就可以 yield to main thread, 让浏览器可以完成渲染/交互任务，然后再执行 task.  
微任务则不可以，因为在一次事件循环中，必须执行完所有微任务，才可以让出主线程。

## 创建宏任务的方式
* requestIdleCallback, 希望在 浏览器空闲的时候，执行 callback. rIC 的执行时间不是完全受控，且 rIC 是利用帧之间的空闲时间来执行 js, 是一个低优先级策略。但是 react 在 fiber 的处理上，并不算是一个低优先级任务
* setTimeout, 存在延迟不稳定的问题，而且如果递归层次过深的话，延迟会达到 4ms, 效率浪费
* MessageChannel 的执行时机比 setTimeout 靠前，而且执行实际准确，但是也有兼容性问题
* setImmediate, 执行时机在 MessageChannel 之前，但是有兼容性问题
  

```js
// boolean 是当前任务是否已经过期，返回值是一个 Callback 或者 undefined
export type Callback = boolean => ?Callback;

// 对于 task 的封装
export opaque type Task = {
  id: number,
  callback: Callback | null,
  priorityLevel: PriorityLevel,
  startTime: number,
  expirationTime: number,
  sortIndex: number,
  isQueued?: boolean,
};

// Tasks are stored on a min heap
var taskQueue: Array<Task> = []; // 已经 ready 但是还没执行的 task
var timerQueue: Array<Task> = []; // 还没有 ready, 还在等待的 task

// This is set while performing work, to prevent re-entrance.
// 当前是否正在执行任务的 flag
var isPerformingWork = false;

// 如果 timerQueue 里面的 task 等待时间到了，且被 handleTimeout 移到了 taskQueue, 则 isHostCallbackScheduled = true
var isHostCallbackScheduled = false;
// 如果 taskQueue 清空，则尝试提交一个 setTimeout 宏任务，等待 timerQueue 里面的 任务 ready, 这个时候 isHostTimeoutScheduled = true
var isHostTimeoutScheduled = false;

// 尝试从 timerQueue 中获取已经 ready 的 task, 推到 taskQueue 里
function advanceTimers(currentTime: number) {
  // Check for tasks that are no longer delayed and add them to the queue.
  let timer = peek(timerQueue);
  while (timer !== null) {
    if (timer.callback === null) {
      // Timer was cancelled.
      // 当在真正执行 task 的 callback 时，会传给它一个 Boolean flag, 表示当前 task 是否已经过了 expiration time.
      // task 可以选择把 callback 置为 null, 表示该 task 被取消
      pop(timerQueue);
    } else if (timer.startTime <= currentTime) {
      // Timer fired. Transfer to the task queue.
      pop(timerQueue);
      // taskQueue 中的 task 通过 expiratationTime 来排序
      timer.sortIndex = timer.expirationTime;
      push(taskQueue, timer);
    } else {
      // Remaining timers are pending.
      return;
    }
    timer = peek(timerQueue);
  }
}

// 在 taskQueue 已经清空，且 timerQueue 里面的 first task 等待时间到达后执行该函数
function handleTimeout(currentTime: number) {
  isHostTimeoutScheduled = false;
  // 在等待时间到了后，按道理现在 timerQueue 里面应该有 task ready 了
  advanceTimers(currentTime);

  // 正常来说，这里 isHostCallbackScheduled 应该为 false. 但是 host callback 有两种方式可以触发
  // 一种是 handle timeout, timerQueue 里面的 task ready 之后触发
  // 一种是 调用了 unstable_scheduleCallback 后立即触发
  // 所以这里需要再次 check isHostCallbackScheduled，避免在 timeout 等待时间到达时，已经有通过 unstable_scheduleCallback 触发的 宏任务了
  if (!isHostCallbackScheduled) {
    if (peek(taskQueue) !== null) {
      // 如果当前 taskQueue 里面确实有 task ready 了，则尝试进行任务调度
      isHostCallbackScheduled = true;
      requestHostCallback();
    } else {
      // 如果在等待时间后，仍然没有 task ready, 则再次进行等待
      // 这里之所以可能为 null, 是因为在 handleTimeout 真正被执行时，该 task 已经被 unstable_scheduleCallback 触发的宏任务 resolve 了
      const firstTimer = peek(timerQueue);
      if (firstTimer !== null) {
        requestHostTimeout(handleTimeout, firstTimer.startTime - currentTime);
      }
    }
  }
}

// 表示是否提交了想执行 work loop 的宏任务，由 requestHostCallback 置为 true, 在 performWorkUtilDeadline 里，如果 hasMoreWork = false, 置为 false
let isMessageLoopRunning = false;
let taskTimeoutID: TimeoutID = (-1: any);

// Scheduler periodically yields in case there is other work on the main
// thread, like user events. By default, it yields multiple times per frame.
// It does not attempt to align with frame boundaries, since most tasks don't
// need to be frame aligned; for those that do, use requestAnimationFrame.
let frameInterval = frameYieldMs; // 5ms
// 在开始执行 performWorkUtilDeadline 时，startTime 被置为 currentTime
let startTime = -1;

// 默认值是 5ms, 如果希望根据指定帧率来控制 frameInterval 的值，则调用该函数
function forceFrameRate(fps: number) {
  if (fps < 0 || fps > 125) {
    // Using console['error'] to evade Babel and ESLint
    console['error'](
      'forceFrameRate takes a positive int between 0 and 125, ' +
        'forcing frame rates higher than 125 fps is not supported',
    );
    return;
  }
  if (fps > 0) {//如果 fps = 60, 那么 frameInterval 就会是 16.7ms
    frameInterval = Math.floor(1000 / fps);
  } else {
    // reset the framerate
    frameInterval = frameYieldMs;
  }
}

// 在 work loop 被调用，在每次尝试执行 task 之前，check 是否需要 yield to host
function shouldYieldToHost(): boolean {
  // 当前任务执行的时间，还不到 5ms. 那么就不需要 yield 给主线程，而是继续执行下一个任务
  const timeElapsed = getCurrentTime() - startTime;
  if (timeElapsed < frameInterval) {
    // The main thread has only been blocked for a really short amount of time;
    // smaller than a single frame. Don't yield yet.
    return false;
  }
  // Yield now.
  return true;
}

function workLoop(initialTime: number) {
  let currentTime = initialTime;
  // 在尝试执行任务的时候，首先去把 timerQueue 的任务拿出来，看看是否 ready
  advanceTimers(currentTime);
  // 拿到expirationTime最小的一个 task，但并没有 pop
  currentTask = peek(taskQueue);
  while (currentTask !== null) {
    if (!enableAlwaysYieldScheduler) {
      // 如果当前任务还没有过期(可能优先级比较低，导致过期时间很靠后)，且当前 work loop 花费的时间已经超过了 frameInterval, 则需要退出，yield to host
      // 对于已经过期的任务，则不会 yield to host, 会一直尝试执行
      if (currentTask.expirationTime > currentTime && shouldYieldToHost()) {
        // This currentTask hasn't expired, and we've reached the deadline.
        break;
      }
    }
    const callback = currentTask.callback;
    if (typeof callback === 'function') {
      // 先把 task callback 置为 null
      currentTask.callback = null;
      currentPriorityLevel = currentTask.priorityLevel;
      // 当前任务是否已经过期
      const didUserCallbackTimeout = currentTask.expirationTime <= currentTime;
      const continuationCallback = callback(didUserCallbackTimeout);
      // 更新 currentTime 为执行完上次任务之后的当前时间
      currentTime = getCurrentTime();
      if (typeof continuationCallback === 'function') {
        // 如果 返回的是 function, 则不管当前 time slice 还剩下多少时间，都直接 yield to main thread
        // 下一次执行的时候，该任务执行的 callback 则是 continuationCallback
        currentTask.callback = continuationCallback;
        advanceTimers(currentTime);
        return true;
      } else {
        // 如果不是，则表示任务已经执行完毕，则将任务 弹出
        if (currentTask === peek(taskQueue)) {
          pop(taskQueue);
        }
        advanceTimers(currentTime);
      }
    } else {
      // 如果 callback 不可执行，则直接弹出
      pop(taskQueue);
    }
    // 获取下一个任务
    currentTask = peek(taskQueue);
    if (enableAlwaysYieldScheduler) {
      if (currentTask === null || currentTask.expirationTime > currentTime) {
        // This currentTask hasn't expired we yield to the browser task.
        break;
      }
    }
  }
  // Return whether there's additional work
  if (currentTask !== null) {
    return true;
  } else {
    // 当前 ready 的 task 已经做完了，尝试从 timerQueue 里面获取一个 task, 然后申请一个 hostTimeout
    // 希望在 firstTimer.startTime - currentTime 之后，即 firstTimer startTime 到达之后，执行 handleTimeout
    const firstTimer = peek(timerQueue);
    if (firstTimer !== null) {
      requestHostTimeout(handleTimeout, firstTimer.startTime - currentTime);
    }
    return false;
  }
}

// flushWork 是 workLoop 的 trigger, 额外做了一些清理工作
function flushWork(initialTime: number) {
  // We'll need a host callback the next time work is scheduled.
  isHostCallbackScheduled = false;
  if (isHostTimeoutScheduled) {
    // We scheduled a timeout but it's no longer needed. Cancel it.
    isHostTimeoutScheduled = false;
    cancelHostTimeout();
  }

  isPerformingWork = true;
  const previousPriorityLevel = currentPriorityLevel;
  try {
      return workLoop(initialTime);
  } finally {
    currentTask = null;
    currentPriorityLevel = previousPriorityLevel;
    isPerformingWork = false;
  }
}

// 向浏览器发起申请，将 isMessageLoopRunning 置为 true, 表示现在想执行任务了，然后添加了一个延时为 0 的宏任务
// 该方法可以被两种方式触发
// 一种是 handleTimeout 在等待时间到达后将 task 挪到 taskQueue 后触发
// 一种是 unstable_scheduleCallback 后触发
function requestHostCallback() {
  if (!isMessageLoopRunning) {
    isMessageLoopRunning = true;
    schedulePerformWorkUntilDeadline();
  }
}

// performWorkUntilDeadline 是压入宏任务队列的回调，当添加的宏任务执行时，就是开始执行 performWorkUntilDeadline
const performWorkUntilDeadline = () => {
  if (enableRequestPaint) {
    needsPaint = false;
  }
  // 回调执行的时候，先判断当前是否想执行任务
  if (isMessageLoopRunning) {
    const currentTime = getCurrentTime();
    // Keep track of the start time so we can measure how long the main thread has been blocked.
    // 记录当前尝试执行任务的时间点
    startTime = currentTime;

    // If a scheduler task throws, exit the current browser task so the
    // error can be observed.
    //
    // Intentionally not using a try-catch, since that makes some debugging
    // techniques harder. Instead, if `flushWork` errors, then `hasMoreWork` will
    // remain true, and we'll continue the work loop.
    // 这里不 catch err, 因为 如果 flushWork 抛错，需要让外部感知，而且在抛错的情况下，hasMoreWork 会为 true, 可以继续 schedule 后面的 work
    let hasMoreWork = true;
    try {
      hasMoreWork = flushWork(currentTime);
    } finally {
      if (hasMoreWork) {
        // If there's more work, schedule the next message event at the end
        // of the preceding one.
        // 完成一个任务后，如果还有 more work, 则继续 schedule 一个新的 宏任务
        schedulePerformWorkUntilDeadline();
      } else {
        // 如果没有任务了，则退出 work loop
        isMessageLoopRunning = false;
      }
    }
  }
};
// schedulePerformWorkUntilDeadline 是把回调包装为一个宏任务
// React 优先使用 setImmediate, 该 API 接收一个回调，表示当前并不直接回调，而是在浏览器完成后续工作后，再执行回调
// 如果浏览器不支持，则尝试使用 MessageChannel. MessageChannel 允许同源的通信，且也是通过宏任务来实现的。
// 如果MessageChannel 也不支持，则使用最基础的 setTimeout. setTimeout 不被优先使用，是因为它有0-4ms 的延迟，会产生浪费
let schedulePerformWorkUntilDeadline;
if (typeof localSetImmediate === 'function') {
  // Node.js and old IE.
  // There's a few reasons for why we prefer setImmediate.
  //
  // Unlike MessageChannel, it doesn't prevent a Node.js process from exiting.
  // But also, it runs earlier which is the semantic we want.
  // If other browsers ever implement it, it's better to use it.
  // Although both of these would be inferior to native scheduling.
  schedulePerformWorkUntilDeadline = () => {
    localSetImmediate(performWorkUntilDeadline);
  };
} else if (typeof MessageChannel !== 'undefined') {
  // DOM and Worker environments.
  // We prefer MessageChannel because of the 4ms setTimeout clamping.
  const channel = new MessageChannel();
  const port = channel.port2;
  channel.port1.onmessage = performWorkUntilDeadline;
  schedulePerformWorkUntilDeadline = () => {
    port.postMessage(null);
  };
} else {
  // We should only fallback here in non-browser environments.
  schedulePerformWorkUntilDeadline = () => {
    // $FlowFixMe[not-a-function] nullable value
    localSetTimeout(performWorkUntilDeadline, 0);
  };
}

// 执行该函数，则是添加一个延迟为 ms 的 host timeout 宏任务
function requestHostTimeout(
  callback: (currentTime: number) => void,
  ms: number,
) {
  taskTimeoutID = localSetTimeout(() => {
    callback(getCurrentTime());
  }, ms);
}

// 执行该函数则是取消当前的 host timeout
function cancelHostTimeout() {
  // $FlowFixMe[not-a-function] nullable value
  localClearTimeout(taskTimeoutID);
  taskTimeoutID = ((-1: any): TimeoutID);
}

function unstable_scheduleCallback(
  priorityLevel: PriorityLevel,
  callback: Callback,
  options?: {delay: number},
): Task {
  var currentTime = getCurrentTime();

  var startTime;
  if (typeof options === 'object' && options !== null) {
    var delay = options.delay;
    if (typeof delay === 'number' && delay > 0) {
      startTime = currentTime + delay;
    } else {
      startTime = currentTime;
    }
  } else {
    startTime = currentTime;
  }

  var timeout;
  switch (priorityLevel) {
    case ImmediatePriority:
      // Times out immediately
      timeout = -1;
      break;
    case UserBlockingPriority:
      // Eventually times out
      timeout = userBlockingPriorityTimeout; // 250ms
      break;
    case IdlePriority:
      // Never times out
      timeout = maxSigned31BitInt;
      break;
    case LowPriority:
      // Eventually times out
      timeout = lowPriorityTimeout; // 10000ms
      break;
    case NormalPriority:
    default:
      // Eventually times out
      timeout = normalPriorityTimeout; // 5000ms
      break;
  }

  var expirationTime = startTime + timeout;

  var newTask: Task = {
    id: taskIdCounter++,
    callback,
    priorityLevel,
    startTime,
    expirationTime,
    sortIndex: -1,
  };
  if (enableProfiling) {
    newTask.isQueued = false;
  }

  if (startTime > currentTime) {
    // This is a delayed task.
    newTask.sortIndex = startTime;
    push(timerQueue, newTask);
    if (peek(taskQueue) === null && newTask === peek(timerQueue)) {
      // All tasks are delayed, and this is the task with the earliest delay.
      if (isHostTimeoutScheduled) {
        // Cancel an existing timeout.
        cancelHostTimeout();
      } else {
        isHostTimeoutScheduled = true;
      }
      // Schedule a timeout.
      requestHostTimeout(handleTimeout, startTime - currentTime);
    }
  } else {
    newTask.sortIndex = expirationTime;
    push(taskQueue, newTask);
    if (enableProfiling) {
      markTaskStart(newTask, currentTime);
      newTask.isQueued = true;
    }
    // Schedule a host callback, if needed. If we're already performing work,
    // wait until the next time we yield.
    if (!isHostCallbackScheduled && !isPerformingWork) {
      isHostCallbackScheduled = true;
      requestHostCallback();
    }
  }

  return newTask;
}
```