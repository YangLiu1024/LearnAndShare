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
4. 同一组件中的不同 effect, 则按照声明顺序就行调用

## useEffect 调用
组件挂载时，所有组件里面的所有 useEffect 都会调用一次回调函数。对于组件本身，回调函数按照声明顺序从上往下执行。对于父子组件，子组件的 effect 回调会优先于 父组件的 effect 回调执行。如果是开发模式下的 *StrictMode*, React 还会对挂载的组件默认执行一次 unmount + mount. 即卸载再挂载一次。这种情况下，是会先执行子组件的 effect cleanup 回调，再执行父组件的 cleanup 回调。接着执行 子组件的 effect 回调，最后是父组件的 effect 回调。  
当状态发生改变，则只有依赖于该状态的 effect 回调会被执行，且也是子组件优先于父组件。  
当组件卸载时，会***先执行父组件的 cleanup 回调，再执行子组件的 cleanup***
```js
import {useState, useEffect, useCallback} from 'react';
// 当组件挂载时，会先执行 Child2 的 effect 函数，接着 Child1 的 effect 函数，最后是 App 的 effect 函数
// 如果是 dev mode, 则在初始挂载时，就会先 卸载一次，再重新挂载。那么在卸载时，也是先调用 Child2 的 effect 清理函数， 接着 Child1 的清理函数，最后是 App 的清理函数
// 重新挂载时，和初始挂载的 effect 执行顺序相同

// 在正常卸载时，则会先调用 Child1 的清理函数，接着才是 Child2 的清理函数
const App = () => {
  const [state, setState] = useState(true);
  useEffect(() => {
    console.log('mount app')

    return () => {
      console.log('unmount app')
    }
  }, [])
  return (
    <div>
      <button onClick={() => setState(s =!s)}>{state ? 'Hide' : 'Show'}</button>
      {state && <Child1/>}
    </div>
  );
}

const Child1 = () => {
    useEffect(() => {
    console.log('mount Child1')

    return () => {
      console.log('unmount Child1')
    }
  }, [])
  return (
    <div>
      Child1
      <Child2/>
    </div>
  )
}

const Child2 = () => {
    useEffect(() => {
    console.log('mount Child2')

    return () => {
      console.log('unmount Child2')
    }
  }, [])
  return (
    <div>Child2</div>
  )
}
export default App;
```

# useEffectEvent(beta)
effect 有时候会依赖于多个状态，但是我们可能并不希望依赖列表里每一个状态改变时，都触发 effect, 但 effect 在运行时，确实会用到依赖列表里所有的状态。那这个时候改怎么处理呢？  
可以参考 自定义 hook useChanged(state, handler), state 是真正想监听的状态，handler 是当 state 改变时，触发的回调。  
react 官方推出了实验版本的 useEffectEvent, 可以达到类似的目的

# useEffect 的依赖项
当 effect 的依赖项是通过级联访问，比如 *a.b.c*, 那么 effect 是怎样才能触发呢？
```js
import {useState, useEffect, useCallback} from 'react';

const App = () => {
  const [state, setState] = useState({x: {y: {z: 2}}});

  useEffect(() => {
    console.log('state ', state)
  }, [state])

  useEffect(() => {
    console.log('state x', state.x)
  }, [state.x])

  useEffect(() => {
    console.log('state x.y', state.x.y)
  }, [state.x.y])

  useEffect(() => {
    console.log('state x.y.z', state.x.y.z)
  }, [state.x.y.z])

  const {x} = state;
  
  useEffect(() => {
    console.log('x', x)
  }, [x])

  const {y} = x;
  
  useEffect(() => {
    console.log('y', y)
  }, [y])

  const {z} = y;
  console.log('z-render ', z)
  useEffect(() => {
    console.log('z', z)
  }, [z])
  
  const onChange = useCallback(() => {
    // 如果不改变 state 的 引用，不管 state 内部的属性怎么改变，react 会认为当前组件没有状态改变，不需要重新渲染，组件不会重新执行，所有 effect 也都不会触发
    // 如果这个时候，有其它状态改变，导致组件重新渲染，那么对 state 的改变将变得可见
    setState(v => {
      v.x = {y: {z: 4}}
      return v
    })

    // 因为 state 的引用改变，那么组件将重新渲染。又因为 state 内部的 x, y, z 都没有改变，则只有 依赖于 state 的 effect 被触发
    setState((v) => {
        const r = {...v};
        return r;
    })

    // 现在 state 和 内部的 z 的值都被改变，那么依赖于 state 的 state.x.y.z 或者 z 的 effect 都将被触发
    setState((v) => {
        const r = {...v};
        r.x.y.z = 5;
        return r;
    })
  }, [])

  return (
    <div>
    <button onClick={onChange}>Change State with different </button>
    {state.x.y.z}
    </div>
  )

}

export default App;
```
综上所述，级联调用，和解构后再依赖，其实没有区别。关键点只在于，组件是否会重新渲染。当组件重新渲染，则会根据依赖项，去判断当前依赖项的值是否改变，如果改变，则 effect 会触发。  
如果依赖项是对象，则判断引用是否相同，如果是基础值，则判断值是否相同。