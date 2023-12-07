# ref vs state
React 提供的 `useRef` 其返回值是一个引用，具有属性 `current`. `useRef` 的值在不同次 render 下返回值是一样的，且 useRef 的 current 在改变时，不会引起重新渲染。  
可以理解为 useRef 返回值就是一个函数外定义的变量，该变量不会随函数重新执行而改变，且变量的 current 改变时，不会引起重新渲染。但和函数外变量的区别在于，每一个函数组件，都会生成一个自己的 ref, 而函数外的变量就会被共享。  

useRef 还有一个作用就是用于将组件或者元素直接 引用起来
```js
const btn = useRef()

return (
    // 这样 ref.current 就会指向 button 这个元素
    <button ref=btn>ABC</button>
)
```

`useState` 就是一个状态，当状态改变时，会触发重新渲染。  

`useRef` 和 `useState` 还有一个区别，在于 useRef 的修改是同步的，在当前帧就会被感知到，但是 useState 状态改变时，需要到下一帧，才会生效
```js
const [count, setCount] = useState(0)
const r = useRef(0)

return (
    <>
        <button onClick={() => {
            setCount(count + 1)
            console.log(count) // 在点击的时候，count = 0, 即使调用 setCount(1), 当前帧的 count 仍然为 0
        }}>State</button>
        <button onClick={() => {
            r.current += 1
            console.log(r.current) // 
        }}>Ref</button>
    </>
)
```

