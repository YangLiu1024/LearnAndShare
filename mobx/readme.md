# Readme
mobx 类似于 redux, 定义了 可观测对象 observable，以及改变对象属性的 action, 派生属性 computed, 等等。  
在进行组件渲染时，在使用 *observer* wrap 组件后，mobx 可以自动监测到该组件使用的所有可观测对象，在可监测对象改变时，触发重新渲染。  
mobx 的 observable 就替代了 useState, action 就类似 state dispatcher 或者 useCallback, computed 就类似于 useMemo  

# 将对象转换为可观测对象
## makeObservable(target, annotations, options)
将 target 通过 annotations 变成 observable, 通常，想观察的域会注解为 observable, 计算属性注解为 computed, 改变状态的函数注解为 action.  
## makeAutoObservable(target, overrides, options)
这是 makeObservable 的自动注解版本，省去了手动注解的麻烦。但是 makeAutoObservable 不能在子类中使用，而且，如果一个类使用了该方法，它将不能有子类
## observable(source, overrides, options)
observable 除了用作注解，还可以直接当作函数使用，效果和 makeAutoObservable 类似，都是自动注解。区别在于，它会把 source 对象复制，然后返回一个 Proxy 对象。而 makeAutoObservable 会修改原对象，在原对象上直接进行配置。

# 注解
## observable
将属性标记为 可观察对象，具体的还有 observable, observable.deep, observable.ref 的区别
## computed
将一个 getter 标记为 计算属性。当 getter 里的依赖项发生改变时，计算属性将重新计算。computed.struct 则表示，如果当计算结果与之前的结果相同，那么则不会触发依赖于该计算属性的 副作用
## action
action 是标记用于改变可观测对象状态的函数。对于可观测对象状态的改变，都需要包裹在 action 里

# 副作用
副作用就是在观测状态改变的时候，调用的回调函数。mobx 提供了几个常用的工具函数
## autorun(() => {})
在第一次执行时，就会执行一次回调。并且分析在调用过程中访问的所有的可观测状态，action 除外。当依赖的状态发生改变时，该回调会再次自动调用。
## reaction(() => data, (nv, ov) => {})
第一个函数，返回要观察的状态。第二个函数，是在观察的状态发生改变的时候，执行的回调。和 autorun 的主要区别在于，reaction 只会监测第一个函数返回的数据的状态，而不管回调函数真正访问的状态。  

## when(() => boolean, () => {})
## when(() => boolean): Promise<void> & {cancel: () => void}
当传入回调时，那么在第一个函数返回值变为 true 时，自动执行回调，执行结束后，清理监听。  
当不传入回调时，返回一个 带有 cancel 的 Promise. user 可以 then, 也可以 cancel。  
### 参数
这些工具函数都有第三个可选参数，支持配置已下项：
* reaction 支持配置 fireImmediately: boolean, 表示是否立即执行回调
* reaction/autorun 支持 delay: number, 表示回调执行的延迟，用于节流，单位是毫秒
* when 支持 timeout: number, 用于指定需要等待的时间，当时间超出，则抛出异常或者 reject
### 返回值
工具返回的返回值都是 disposer: () => void, 执行后，清理掉监听 