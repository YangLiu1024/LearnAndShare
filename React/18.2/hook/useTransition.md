# useTransition
useTransition 允许你在不阻塞 UI 的情况下更新状态。  
```js
// useTransition() 会返回两个值，第一个是标志是否正在进行过渡, 第二个是一个 startTransition 函数
const [isPending, startTransition] = useTransition()
const [value, setValue] = useState()

function handleClick() {
    // 当在 startTransition 里改变状态时，该状态更新会被标志为 过渡，UI 渲染会是 非阻塞的。并且该渲染是可以被取消的
    // 如果不使用 transition, 那么 UI 的渲染就会是 阻塞的，在 UI 渲染执行之前，UI 会是 blocked
    startTransition(() => {
        setState(null)
    })
}
```

除此之外，startTransition 的函数体必须是同步的。  

过渡也会避免 Suspense 显示回退内容，但是 过渡不会等待渲染完全完成，它只会等待足够长的时间，来避免 hide 一些已经渲染的内容。  
比如，在 路由 切换时，可能一些 layout 布局是共用的，那么使用 过渡就可以避免 hide 掉这些共用的内容。

# useTransition vs useDefferedValue
React18 引入了新的概念，'并发'。 并发涉及同时更新多个状态。通过将不同的状态改变赋予不同的优先级，低优先级的渲染将不会阻塞 UI 响应  
useTransition 是告诉 react, 通过 startTransition 进行的 状态改变，具有更低的优先级，且在后台完成渲染。UI 的渲染不需要等待这些低优先级的渲染，当其它具有更高优先级的状态改变完成渲染时，就应该渲染屏幕。  
useDefferedValue 也可以达到类似的效果，在状态改变时，react 会在后台进行对应的渲染。如果状态持续改变，之前的后台渲染会被取消。当后台渲染完成，页面才会发生改变。  
两者主要的区别在于，useTransition 需要你在 startTransition 函数体内执行状态变换函数，那么你需要在该组件内能访问改变状态的函数。  
如果组件只是能访问状态，不能访问 setter, 那么就可以使用 useDefferedValue。 