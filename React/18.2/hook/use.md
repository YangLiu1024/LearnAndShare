# use(实验版本)

***use*** 是一个 hook, 可以使用 Promise 或者上下文。 和其它 hook 类似，hook 只能在 functional component 顶层 或者 hook 里调用，不同的是，use 可以在 loop 或者 condition 语句里使用。  
use 不能在 try-catch 里调用， 所以为了 handle suspending 和 error 状态，还和 *Suspense* 以及 *Error Boundray* 集成，会根据 use 的 promise 的状态自动回退由 *Suspense* 以及 *Error Boundray* 包装的组件。