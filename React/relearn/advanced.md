# React ref 和 DOM
除了使用 ref 存储一些和渲染无关的数据，还可以通过 ref 绑定 DOM 元素，从而操作 DOM.
```js
const containerRef = useRef();

// React 会在创建 该 div 元素时(即提交阶段，而不是渲染阶段)，将 DOM 节点传递给 ref
// 在删除该 DOM 元素时，将 该 ref.current 置为 null
return (
    <div ref={containerRef}></div>
)
// 在有的情况下，我们可能需要对数组多个元素进行 ref, 但是我们又不能提前知道数组的大小，而且 useRef 必须在组件的顶层调用，那么该怎么办呢？
return (
    <>
        {items.map(item => (
            // 怎么在这里把 ref 记录下来呢？
            // 可以使用 ref 的回调函数, 元素的 ref 属性也支持 回调函数，在创建元素后，会调用该回调函数，参数就是元素本身
            // 这样就可以把所有 item 的 node 节点收集起来
            <li ref={(node) => {}}></li>
        ))}
    </>
)
```
# React ref 和 自定义组件
和原生 HTML 节点不同，React 自定义组件并不直接支持 ref。要想让自定义组件支持 ref, 则需要显式使用*forwardRef*
```js
// MyInput 把 外部传入的 ref 绑定到了内部的 input 元素上
const MyInput = forwardRef((props, ref) => {
    return (
        <input ref={ref}/>
    )
})

// 父组件就可以这样传入 ref
const inputRef = useRef()

<MyInput ref={inputRef}/>

// 但是大多数时候，其实并不想直接暴露内部的元素节点本身，那么组件就可以通过 *useImperativeHandler* 自定义需要暴露的对象
const MyInput = forwardRef((props, ref) => {
    // 外部传入的 ref 会绑定到 useImperativeHandler 返回的自定义对象
    useImperativeHandler(ref, () => ({
        focus() {

        },
    }))
    return (
        <input />
    )
})
```

# React-DOM flushSync
因为 React 在处理回调函数时，所有的更新都不是立即更新到 DOM 的，需要在事件处理结束后，再重新渲染，提交。  
但是有的事件响应需要在最新的 DOM 上执行，那么该怎么办呢？ react-dom 提供了 *flushSync* 来解决这个问题
```js
const handleAdd = (newTodo) => {
    setTodos((todos) => [...todos, newTodo]);
    // 这里其实并不会滚动到真正的最后一个元素，因为这个 时候 setTodos 其实并没有更新到 DOM, 但是 listRef.current 是直接操作当前 DOM 节点的
    listRef.current.lastChild.scrollIntoView()
}

// 为了能够真正滚动到最后一个节点，需要保证在 滚动之前，DOM 已经是最新的, 那么就需要使用 *flushSync* 来立即同步 DOM
const handleAdd = (newTodo) => {
    flushSync(() => {
        setTodos((todos) => [...todos, newTodo]);
    })
    listRef.current.lastChild.scrollIntoView()
}

```

# React 不推荐在 effect 里请求数据
在 effect 里请求数据有一些缺点
1. useEffect 不会在服务器端运行，这就意味着服务器端渲染的 HTML 只会包含 loading, 但是没有实际数据。客户端必须在下载完所有 JS 以及渲染结束后，才会请求数据
2. 如果多个组件都有 effect 去请求数据，容易造成网络瀑布，网络卡顿时，反而不如并行获取所有数据速度快
3. 组件卸载后，如果重新挂载，需要再次请求数据
为了解决这些问题，可以使用框架提供的数据获取机制，或者使用一些前端数据缓存方案，比如 react-query, useSWR, 等

# React 不推荐在 effect 里调整 state
当 props 或者 state 改变时，如果通过 effect 去更新部分状态，那么就需要等待 react 先以当前的 state 去更新 DOM, 在提交之后，才会去执行 effect.  
然后 effect 如果更新了状态，有需要重新渲染，提交一次。这样并不高效。  
一种办法是在渲染过程中，直接修改 state
```js
function List({ items }) {
  const [isReverse, setIsReverse] = useState(false);
  const [selection, setSelection] = useState(null);

  // 好一些：在渲染期间调整 state
  const [prevItems, setPrevItems] = useState(items);
  if (items !== prevItems) {
    setPrevItems(items);
    setSelection(null);
  }
  // 如果在渲染期间直接修改了 state, 那么 react 会在该组件 return 后立即重新渲染该组件
  // 这个时候，react 还没开始渲染子组件，意味着子组件可以跳过渲染旧的 state
}
```
为了避免级联式重新渲染，React 也要求，在渲染期间只能修改组件自己的状态，不能修改其它组件的状态。  
但不管怎样，在渲染期间更改 state 总是会使数据流更难理解和调试，最好的就是通过 key 来重置状态，或者在渲染期间计算所需内容。

# React useEffectEvent
有的时候，effect 并不希望所有依赖项都能触发 effect.
```js
  const [count, setCount] = useState(0);
  const [increment, setIncrement] = useState(1);

  useEffect(() => {
    const id = setInterval(() => {
      setCount(c => c + increment);
    }, 1000);
    // 当 increment 改变时，effect 也会被触发
    // 这就导致，如果 increment 一直变化，interval 就会一直创建，取消，创建，取消，导致 counter 好像在 increment 变化期间没起作用
    return () => {
      clearInterval(id);
    };
  }, [increment]);

  // 为了解决这个问题，需要把 increment 移除出 effect 依赖列表
  // 一个办法是使用 useEffectEvent, 这样 onStep 就是一个 effect event, 其内部的代码是非响应的
  const onStep = useEffectEvent(() => {
    setCount(c => c + increment)
  })

  useEffect(() => {
    const id = setInterval(() => {
      onStep();
    }, 1000);
    return () => {
      clearInterval(id);
    };

  }, [])

  // 还有一种方式就是使用 useRef

  const onStep = () => setCount(c => c + increment)
  const stepRef = useRef(onStep)
  stepRef.current = onStep;

  useEffect(() => {
    const id = setInterval(() => {
      stepRef.current();
    }, 1000);
    return () => {
      clearInterval(id);
    };

  }, [])  
```