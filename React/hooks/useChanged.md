# useChanged
很多时候，我们需要监测一个变量的值，当它改变时，我们希望触发回调。如果使用 useEffect, 其回调再最开始就会被调用，可能不符合实际需求。  
因此实现一个类似于 Javafx observable 的 change listener, 只有当 value 改变时，才触发回调。
```js
export function useChanged<T = unknown>(
  value: T,
  handler: (ov: T, nv: T) => void,
  equal?: (v1: T, v2: T) => boolean,
): void {
  // 使用 ref 来存储回调，因为当回调改变时，并不希望触发回调调用
  const handlerRef = useRef<(ov: T, nv: T) => void>(handler);
  // 使用 previous 来存储 value 的上一次值，且使用 value 作为 previous 的初始值
  const previousRef = useRef<T>(value);

  useEffect(() => {
    handlerRef.current = handler;
  }, [handler]);

  // 使用 callback 来存储 isEqual
  const isEqual = useCallback((v1: T, v2: T): boolean => (equal ? equal(v1, v2) : v1 === v2), [equal]);

  useEffect(() => {
    const previousValue = previousRef.current;
    // 当 value 真正改变时，才触发回调
    if (!isEqual(previousValue, value)) {
      previousRef.current = value;
      handlerRef.current?.(previousValue, value);
    }
  }, [value, isEqual, handlerRef]);
}
```