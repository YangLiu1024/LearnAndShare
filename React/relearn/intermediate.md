# 既支持 controlled, 又支持 uncontrolled 
有的控件，用户既有可能以受控组件的形式去使用，又有可能不需要控制，仅仅只是响应 value change.  
那么这个时候，它的参数可能就是
```js
type Props<T> = {
    defaultValue?: T;
    value?: T;
    onChange?: (nv: T) => void;
    controlled?: boolean;
}
```
*defaultValue* 用于非受控组件，父组件仅仅只提供初始值。 value 用于受控组件，onChange 用于响应 value change
```js
const useUncontrolled = ({
    defaultValue,
    value,
    onChange,
    // 如果没有显式设置(通常如此)，那么如果传入 value, 则认为当前是 controlled mode, 否则则是非受控模式
    controlled = value !== undefined,
}: Props)：[T, (v: T) => void] => {
    const [uncontrolled, setUncontrolled] = useState<T>(defaultValue);
    const onChangeValue = useCallback((v: T) => {
        setUncontrolled(v);
        onChange?.(v); // 如果没有传入，通常意味着是非受控模式
    }, [onChange])；

    if (value !== undefined && controlled) {
        // 当前是受控模式
        return [value, onChangeValue];
    }
    return [uncontrolled, onChangeValue]

}
```
正常使用场景下，user 可能传入 value 和 onChange, 那么就是 受控模式。

# React state 的保存和重置
直觉上我们可能认为，组件的状态是存在组件里的，其实并不是。组件的状态，是存在 react 里的。  
react 通过渲染树中组件的位置，将组件和状态关联在一起。 如果在每一次渲染中，在***相同的位置，渲染相同的组件***，状态就会被保留。  
如果组件被移除，或者一个不同的组件，渲染在了该位置，那么状态就会被丢掉。   
那怎么判断组件是否相同呢？
1. 相同组件，即相同的函数
2. 如果有 key, 则 key 需要相同。如果不同，则认为是两个不同的组件

```js
// 有三个 counter， button 用于控制 第二个 counter 是否渲染。
// 当 第二个 counter hide 时，它会被卸载，所以它的状态会丢掉。但是第一个和第三个 counter 的状态会被保留。
// 第一个被保留很正常，位置和组件都没有变。第三个为什么没有丢掉状态呢？明明第二个 counter 被卸载了啊
// 这是因为 {show && <Counter/>} 仍然占位了，即使它的值是 false, 在渲染树里仍然占据一个位置。
return (
    <>
        <Counter/>
        {show && <Counter/>}
        <Counter/>
        <button onClick={() => setShow(s => !s)}>Toggle</button>
    </>
)

// 这种情况下，切换 show, Counter 的状态仍然会被保留，因为在渲染树里，Counter 的位置并没有变
// 如果给 Counter 加上 key, 则会导致状态重置
return (
    {show ? <Counter id={1}/> : <Counter id={2}/>}
)

// 这种写法和上面本质上其实一样
if (show) {
    return <Counter id={1}/>
}
return <Counter id={2}/>

// 在下面这种条件下，生成的 渲染树其实是一样的，那么 Counter 的状态将被保留
// 如果渲染树结构不同，则状态会被丢弃
return (
    {show ? <div><Counter id={1}/></div> : <div><Counter id={2}/></div>}
)

// 如果我们在遍历数组时，使用索引作为 key, 当数组元素顺序发生变化的时候，组件的状态就会发生混乱
// 比如如下例，我们修改某个元素的状态后，将数组倒序，那么之前的状态将被继承到该位置新的元素上
return (
    items.map((i, idx) => <Item key={idx} item={i}/>)
)
```

# React 为什么不推荐把组件定义在组件内部
如果把组件定义在组件内，通常会引起 bug 和 性能问题
```js
// 如果把 B 定义在 A 的内部，那么 在 A 重新渲染时，B 的状态都会丢失，因为 每一次渲染，B 都是重新定义的新函数
// 那么对于 react 来说，B 就是一个新组件， B 的状态就会被丢失和重置
function A() {

    function B() {

    }

    return (
        <>
            <B/>
            <A/>
        </>
    )
}
```