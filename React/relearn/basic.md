# 为什么 react 组件只能返回一个元素
因为 react 组件本质上是一个函数，函数的返回值只能有一个。 而 JSX 其实在底层会被转化为 JS 对象。所以多个 JSX 标签必须用一个父元素或者 Fragment 来包裹
# 使用驼峰命名大部分属性
JSX 最终会转化为 JS 对象，JSX 中的属性，也会变成 JS 对象中的键值对。在组件里，也需要用变量的方式读取这些属性。 JS 对变量名有限制，比如不能包含 - 符号。  
这就是为什么 react 中大部分 html 和 svg 属性都是使用驼峰，比如用 strokeWidth 代替 stoke-width, className 代码 class(class 是保留名字)  
内联的 style 属性也需要用驼峰命名
```js
<div style={{backgroundColor: 'red'}}>
</div>
```
# React 中为什么需要 key
React 需要一些辅助信息来标记元素，以便决定元素是否可以重用等。一个妥当的 key 可以帮助 react 提高性能。

# React 为什么侧重于纯函数
纯函数可以带来一些利好的特性：
1. 组件可以在不同环境下运行，比如服务器上，相同的输入，总是会导致相同的结果，因此一个组件可以满足多个用户请求
2. 对于输入没有变化的组件，可以跳过渲染，从而节省时间
3. 如果在渲染深层次组件的时候，某些数据发生了变化，可以直接安全地停止当前的计算，重新开始渲染

# React 渲染树, 依赖树
渲染树其实就是组件树，描述了组件之间的嵌套关系，有助于理解数据的流向和调试性能问题。  
之所以不是叫 DOM 树，是因为 React 不仅仅可以把组件树 apply 到 DOM, 还可以 apply 到其它环境，比如移动端，等等。  
依赖树是指每一个模块之间的依赖关系，在生产环境下构建时，bundler 会根据依赖树，捆绑所有需要的 JS。有助于调试大型捆绑包带来的渲染速度过慢的问题，以及发现那些捆绑代码可以被优化。   

# React 渲染和提交
在 React 应用中，一个屏幕更新，会包含一下步骤：
1. 触发， 状态改变， 请求一次新的渲染
2. 渲染，执行状态改变涉及到的组件，收集和上一次渲染之间的 diff。如果没有 diff, 则没有下一步操作
3. 提交，把 diff 提交(同步) 到 DOM
更新好 DOM 之后，浏览器就开始绘制屏幕了

# React state 更新
当触发重新渲染时，react 会根据当前 states 生成快照（useState 会返回当前状态的快照），该快照用于重新调用组件。  
每个渲染，以及其中的函数，始终看到的都是 React 提供给这个渲染的快照。这就意味着，当组件渲染时，使用的都是快照中固定的状态，并且，事件处理函数捕获的都是快照中的状态。当处理函数被调用时，使用的都是之前捕获的快照中的值。    
下一次渲染，事件处理函数都会重新生成。    
## 批处理
React 会等到事件处理函数中的所有代码都执行完毕，再去处理状态更新。这就会允许 React 在重新渲染之前，安全的收集到足够多的状态更新，从而避免不必要的重新渲染。  
状态更新有两种方式，一种就是直接替换值，一种就是更新函数，使用上一个值，来获取下一个值
```js
setCount(1) // 替换值
setCount(c => c + 1) // 更新函数
```
实际上，替换值其实也是按照更新函数的形式运行的，当 react 执行这些 set*** 函数时，它并不是去立即更新状态，而是会把这些状态更新函数存入队列。  
当事件处理函数执行完毕，进行重新渲染时，下一次运行 useState，react 会根据该队列，计算出 state 最新的值，然后保存该值并返回。  
有一个比较有意思的示例
```js
// 在点击 button 时，事件回调函数开始执行，这个时候 pending = 0
// #1 执行，压入队列， delay 需要等待 3s. 事件执行结束，开始重新渲染， pending 变成 1. 如果多次点击，则会以当前 state 快照 运行多次回调
// 当 3s 过去，第一个回调函数的 #3 开始执行，因为 当时事件回调函数捕获的 pending 是 0, 所以会执行 setPending(-1). 后续的回调也会由它们捕获的状态值执行 #3. 这就导致了第一次执行 #3 时，其值会是 -1， 最后一次执行时，其值也会是 N - 2
// 其实 #3 想达到的效果，是在执行到当前代码行时，对当前的 pending 数量 -1, 所以需要改成 setPending(p => p -1)
// 对于 #4，在多次点击后，每一次重新渲染的快照中，completed 其实都是 0， 那么就会导致，最后的一次回调，其实也只是执行 setCompleted(0 + 1), 那么就需要改成 setCompleted(c => c + 1)
  async function handleClick() {
    setPending(pending + 1); // #1
    await delay(3000); // #2
    setPending(pending - 1); // #3
    setCompleted(completed + 1); // #4
  }
  
  // 当第一次点击，pending 0 => 1, 由 1 重新渲染，以及后续多次点击
  // 第一次 delay 后，会基于当前的 状态值(而不是当时捕获的值)，进行 #3， #4
   async function handleClick() {
    setPending(p => p + 1); // #1
    await delay(3000); // #2
    setPending(p => p - 1); // #3
    setCompleted(c => c + 1); // #4
  } 
```

# 为什么 React 不推荐直接修改 state
1. 直接修改 state 内部属性，会导致难以调试， 不知道两次渲染之间，state 的值发生了什么变化
2. 难以优化。很多优化策略依赖于，如果 state 没有发生变化，就直接跳过渲染
3. 直接修改对象属性，并不会触发重新渲染，并且会修改上一次渲染快照中状态的值