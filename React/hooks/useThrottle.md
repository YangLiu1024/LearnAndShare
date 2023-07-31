# throttle
节流是为了在固定时间段，只执行第一次触发时的回调。即在触发后，必须在固定时间段后，才允许触发下一次
```js
type Callback = (...args: any[]) => void

function useThrottle(callback: Callback, delay: number = 1000) {
    const timer = useRef<number>(0);

    const throttle = useCallback((...args: any[]) => {
        if (!timer.current) {
          timer.current = setTimeout(() => {
            timer.current = 0;  
          }, delay)
          // 在 timer.current 为 0 的情况下，第一次触发的时候，就需要直接执行 回调
          // 在一段时间后，timer.current = 0 后，才允许下一次触发
          callback.call(this, ...args);
        }
    }, [])

}
```
当函数可变，或者有其它依赖项时
```js
function useThrottle(callback: Callback, delay: number = 1000, deps = []) {
    const {current} = useRef({fn: callback, timer: null})

    useEffect(() => {
        // 当传入的 callback 改变时，需要更新 record 里的 fn
        current.fn = callback;
    }, [callback])
    
    return useCallback((...args: any[]) => {
        if (!current.timer) {
            current.timer = setTimeout(() => {
                current.timer = null
            }, delay)
            current.fn.call(this, ...args)
        }
    }, [...deps, delay])
}
```