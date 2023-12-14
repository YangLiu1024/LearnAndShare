# useEffect

在开发模式中，react 会将 effect 执行两次，即 effect => clear => effect, 来帮助你尽快的发现错误。  
现在大多数情况下，开发者通常会在 effect 里去加载数据，但是这种方式存在一些缺点：
1. fetch 发生在客户端，导致用户必须在加载完所有的 JS code 以及初始渲染结束后，才能去加载数据，然后重新渲染。这样并不是很有效
2. 如果父组件在加载一些数据后，子组件也需要继续去加载数据，这在网络速度不是很快的情况下，会比直接在顶点并行获取全部数据慢得多
3. 在 effect 里加载数据，则意味着不能预加载数据，也不能缓存数据。当组件卸载后再次挂载时，又需要再次提取数据

为了解决这些问题，可以依赖于框架内置的数据提取机制，也可以使用客户端数据缓存，比如 React Query, useSWR, React Route 6.4+ 等  

在 有的 effect 里，可能会调用一些 promise, 那么就需要在 下一次 effect 执行之前，清理掉上一次 promise 的 then
```js
useEffect(() => {
    let cancel = false;

    asyncCall().then(() => {
        if (!cancel) {
            // do something is not cancelled
        }
    })

    return () => {
        cancel = true; // cancel previous effect thenable method
    }
}, [deps])
```

# effect 的生命周期
react 组件的生命周期可以描述为
1. 组件挂载
2. 组件响应状态更新
3. 组件卸载

effect 的生命周期和组件挂钩，但侧重点不太一样。effect 是针对于，当依赖状态改变时，会触发对应的清理函数，以及执行对应的 effect.  
当然，在组件挂载时，一定会执行 effect, 在组件卸载时，也一定会执行 清理函数。  

对于父子组件，如果它们的 effect 依赖于相同的状态，那么 它们 effect 的执行顺序：
1. 当状态改变时，子组件的清理函数 => 父组件的清理函数 => 子组件的 effect 函数体 => 父组件的 effect 函数体
2. 当组件挂载时，子组件的 effect 函数体 => 父组件的 effect 函数体。在 strict mode 开发模式下，因为 effect 在挂载时会被自动再次执行，那么还会执行 子组件的清理函数 => 父组件的清理函数 => 子组件的 effect 函数 => 父组件的 effect 函数
3. 当组件卸载时，会先执行父组件的 effect 清理函数 => 子组件的清理函数

# useEffectEvent(beta)
effect 有时候会依赖于多个状态，但是我们可能并不希望依赖列表里每一个状态改变时，都触发 effect, 但 effect 在运行时，确实会用到依赖列表里所有的状态。那这个时候改怎么处理呢？  
可以参考 自定义 hook useChanged(state, handler), state 是真正想监听的状态，handler 是当 state 改变时，触发的回调。  
react 官方推出了实验版本的 useEffectEvent, 可以达到类似的目的