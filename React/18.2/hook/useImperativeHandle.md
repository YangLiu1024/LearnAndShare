# useImperativeHandle
 React 函数组件本身是没有 ref 的，那么怎么可以为组件添加 ref 呢？  
 ```js

 function Child() {
    return (
        <div>
            <button>Click</button>
        </div>
    )
 }


 function Parent() {
    const ref = useRef(null)

    // 给 Child 子组件使用  ref 是会报错的，因为函数组件不支持 ref 属性
    return (
        <Child ref={ref}/>
    )
 }
 ```
 为了能够给函数组件使用 ref 属性， 使用 forwardRef 方法。这样就可以在组件中传递 ref 属性，也可以将 ref 绑定在组件里的任意元素上。  
 该函数会返回一个 react 组件
 ```js
const Child = forwardRef(function Child(props, ref) {
    return (
        <div>
            <button ref={ref}>Click</button>
        </div>
    )
})
 ```
 如果我们不想把 ref 绑定在任何 元素上，而是想绑定在一个自定义的对象上，就需要使用 useImperativeHandle
 ```js
 // useImperativeHandle(ref, handleFactory, dependencies)
 const Child = forwardRef(function Child(props, ref) {

    const handleClick = useCallback(() => {}, [])

    // 让父组件可以直接调用子组件里的方法
    useImperativeHandle(ref, () => ({
        click: handleClick,
    }), [handleClick])

    return (
        <div>
            <button onClick={handleClick}>Click</button>
        </div>
    )
})
 ```