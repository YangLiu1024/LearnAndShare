# useSyncExternalStore

当不是从 state/props/context， 而是从第三方读取数据，就可以使用 useSyncExternalStore
```js
// subscribe 是一个函数，该函数接收一个 listener, 返回一个取消订阅的 handler. 
// getSnapshot 用于返回当前 store 的一个快照
const state = useSyncExternalStore<T>(subscribe: (listener: () => void) => () => void, getSnapshot: () => T)
```

# 订阅浏览器公开信息
当想要订阅浏览器某些公开信息时，这些值会随着时间而变化。即这个值可以在 react 不知情的情况下修改，比如 navigator.onLine。这个时候就可以使用 useSyncExternalStore
```js
function subscribe(callback) {
  window.addEventListener('online', callback);
  window.addEventListener('offline', callback);
  return () => {
    window.removeEventListener('online', callback);
    window.removeEventListener('offline', callback);
  };
}

function getSnapshot() {
    return navigator.onLine;
}

function NetworkIndicator() {
    // 在组件的顶层订阅 外部信息， subscribe 就是去订阅，让外界在值变化的时候，能够通知到该组件。 至于 callback, 是 react 提供，只是为了通信
    // getSnapshot 就是当 react 组件接收到更新通知时，就需要调用该函数来获取最新的值的快照
    const online = useSyncExternalStore(subscribe, getSnapshot)

    return online ? 'Online' : 'Offline'
}
```