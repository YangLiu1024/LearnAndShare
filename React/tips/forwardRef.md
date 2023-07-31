# forwardRef
ref 可以指向基础元素，也可以指向自定义 class 组件, function 组件是不可以接收 ref 参数的。那么当我们想给自定义 function 组件添加 ref 时，该怎么做呢？就需要使用到 `forwardRef`
```js
import {forwardRef} from 'react'

// 当使用 forwardRef 后，函数组件就会多一个参数 ref, 然后父组件就可以在该函数子组件上使用 ref，且绑定的 ref 就会传递到子组件里重新定向的元素上
// 在下面所示案例里，父组件的 ref 就会绑定到 button 元素上
function forwardRef(Counter(props, ref) {
    return (
        <div>
            <button ref={ref}></button>
        </div>
    )
})
```
除了使用 `forwardRef`, 函数组件还可以直接定义一个参数，该参数就是一个 ref object
```js
// 比如定义了一个 innerRef 的参数，外部可以直接使用该参数，绑定到 Counter 想暴露的元素上
// 注意，参数名不能是 ref
function Counter({innerRef, ...rest}) {
    return (
        <div>
            <button ref={innerRef}></button>
        </div>
    )
}
```
# useImperativeHandle
`forwardRef` 只能重定向到某一个基础元素上，但是有的时候，函数组件并不想暴露元素本身，而是一些其它的自定义的东西，这个时候，就需要使用 `useImperativeHandle`. imperative 是 *至关重要的* 意思
```js
function forwardRef(Counter(props, ref) {

    // useImperativeHandle(ref, createHandle, dependencies?), dependencies 是在 createHandle 里使用的依赖
    useImperativeHandle(ref, () => {
        //return something that you want to exposure to outside
    }, [])
    return (
        <div>
            <button></button>
        </div>
    )
})
```