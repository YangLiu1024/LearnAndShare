# debounce
防抖是在某一时间段后再执行回调，如果在这个时间段内事件再次发生，则重新计时等待
```ts
// 可以让一个函数变成 debounced
type Callback = (...args: any[]) => void

export function useDebounce(callback: Callback, delay: number = 1000) {
    const timer = useRef<number>;

    const debounced = useCallback((...args: any[]) => {
        if (timer.current) {
            clearTimeout(timer.current)
        }
        timer.current = setTimeout(() => callback(..args), delay)
    }, [callback, delay])
    return debounced;
}

// 也可以让一个 value 变成 debounce 的
export function useDebounce<Value>(value: Value, delay: number = 1000) {
    const [debounced, setDebounced] = useState<value>(value);
    useEffect(() => {
        const timer = setTimeout(() => setDebounced(value), delay)
        return () => clearTimeout(timer)
    }, [value, delay])
    return debounced;
}
```
如果传入的函数也是可变的，或者还有其它的依赖项
```js
export function useDebounce(callback: Callback, delay: number = 1000, deps = []) {
    const {current} = useRef({fn: callback, timer: null})

    useEffect(() => {
        // 当传入的 callback 改变时，需要更新 record 里的 fn
        current.fn = callback;
    }, [callback])

    return useCallback((...args: any[]) => {
        if (current.timer) {
            clearTimeout(current.timer)
        }
        current.timer = setTimeout(() => current.fn.call(this, ...args), delay)
    }, [delay, ...deps])
}
```
防抖的应用场景在于，需要等待用户的输入停下来一段时间后，才响应变化，比如搜索框的输入，窗口的 resize.  
节流的应用场景在于，即使用户的输入一直不停，也需要进行响应，但一段时间内只响应一次，比如 滚动加载，搜索框的联想功能