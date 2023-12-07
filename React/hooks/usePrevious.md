# usePrevious
通常版本的 usePrevious 是用来记录，上一次渲染时 state 的值
```js
function usePrevious<T>(value: T) {
    // previous 的初始值是 undefined
    const previous = useRef<T>();

    // 当 value 改变时，更改 previous 的 current
    useEffect(() => {
        previous.current = value;
    }, [value])

    // 返回当前 previous 的 current
    return previous.current;
}

// 初始渲染 结果  Count: 0, Previous:
// 这是因为首先，在执行 usePrevious 的时候，返回的是 previous.current, 因为 useEffect 是在渲染后才会执行，所以此时 previous.current 还是 undefined
// 在渲染之后，执行 useEffect, 此时 previous.current 被更新为了 count: 0. 又因为 修改 ref.current 并不会触发重新渲染，所以此时 渲染结果并不会改变

// 当点击 Increase button 之后，状态 count 被更新，页面重新渲染，这个时候 count 是 1， 但是 previous.current 返回 0, 渲染结果是 Count: 1, Previous: 0
// 渲染结束后，previous 的 effect 被执行，其 current 被更新为 1
function Count() {
    const [count, setCount] = useState();
    const previous = usePrevious(count);

    return (
        <div>
            Count: {count}. Previous: {previous}
            <button onClick={() => setCount(c => c + 1)}>Increate</button>
        </div>
    )
}
// 综上，usePrevious 的魔法。其实就在于利用了 1. ref 对象 current 的更新不会触发渲染 2. useEffect 是在渲染之后才会执行
```
但在实际使用中，usePrevious 并不像上述例子那么简单，因为控件的状态不会这么单一。
```js
// 在下面例子里，控件还会有其它状态，在初始渲染时，结果是 Now: 0, before:, Count2: 0
// 如果我们这个时候点击 Increase 来改变 count2, 就会看到渲染结果是 Now: 0, before: 0, Count2: 1， 
// 这个时候，因为重新渲染了，previous 的值被渲染了出来, 可见 previous 的渲染结果并不总是 value 的上一个值
const Count = () => {
  const [count, setCount] = useState(0);
  const [count2, setCount2] = useState(0);
  const prevCount = usePrevious(count);

  return (
    <div>
      <h1>
        Now: {count}, before: {prevCount}, Count2: {count2}
      </h1>
      <button onClick={() => setCount(count - 1)}>Decrement</button>
      <button onClick={() => setCount2(count2 + 1)}>Increase</button>
    </div>
  );
};
```