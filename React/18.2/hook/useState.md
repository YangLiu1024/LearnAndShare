# useState

```js
// 传入 useState 的初始值只会在挂载的时候，进行初始化的时候会使用，在之后的重新渲染中，并不会被采用
// 这里的问题在于，即使不被采用，这个函数仍然会在每一个渲染期间都执行。如果该函数耗时较久，也会损耗性能
const [options, setOptions] = useState(createOptions())

// 为了解决这个问题，可以直接传入初始化函数，而不是函数的返回值
// react 只会在初始化时调用该函数一次
const [options, setOptions] = useState(createOptions)
```