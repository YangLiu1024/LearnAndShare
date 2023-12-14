# useDefferedValue
`useDefferedValue` 是一种延迟渲染的机制
```js
// 初始渲染，defferedValue 会和 value 值一样
// 在后续 defferedValue 接收到新的值时，它首先会保持当前渲染(以之前的值渲染的结果)，然后 schedule 一个 background re-render with new value.
// 这个 background re-render 是可以打断的，即如果在 background re-render 期间，defferedValue 又接收到了一个新的 value, 则会打断当前的 background re-render, 然后以最新的值重新开始 background re-render
// 看起来 useDefferedValue 和防抖操作类似，区别在于 useDefferedValue 没有固定的等待时间
const defferedValue = useDefferedValue(value)// value 必须是 primitive 类型，或者定义在函数外部的对象，否则每次渲染，都会创建新的对象，导致冗余的 background-render
```
useDefferedValue 的使用场景在于，当一个状态改变时，依赖于该状态的组件重新渲染很花时间，但状态很可能会持续变化。为了让 UI 能够流畅的响应状态变化，则需要将 耗时的组件渲染推迟。
```js
export default function App() {
  const [text, setText] = useState('');
  const deferredText = useDeferredValue(text);
  // SlowList 本身的渲染很花时间，如果不使用 useDeferredValue 将该组件的渲染推迟，那么 text 的每次改动，都会导致 SlowList 重新渲染，继而阻塞 UI
  // 使用 useDeferredValue 后，SlowList 的重新渲染将在后台进行，且可以被取消。UI 将不会卡顿
  return (
    <>
      <input value={text} onChange={e => setText(e.target.value)} />
      <SlowList text={deferredText} />
    </>
  );
}
```
# useDefferedValue vs debouncing vs throttling
和 useDefferedValue 类似的优化方案，常用的是 防抖 以及 节流。防抖是希望在状态停止变化一定时间后响应，节流是在一定时间内，状态变化只响应一次。  
useDefferedValue 相对来说总是更优的选择
1. 不需要设置固定的等待时长，useDefferedValue 的等待时长是自适应的。用户机器性能好，则不需要等待，反之，则等待足够的时间
2. useDefferedValue 导致的重新渲染是在后台运行的，且可以被取消的。但 debouncing / throttling 的响应则是不可取消的，且会 block UI.
3. 只有当需要优化的操作，不是发生在渲染期间，那么 debouncing / throttling 会是 useful. 比如，当状态改变时，需要重新发起网络请求
