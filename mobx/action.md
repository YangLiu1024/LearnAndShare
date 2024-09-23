# action 注解
action 就是修改 state 的代码。action 在 transaction 里运行，在 最外层的 action 结束之前，任何可观测对象的修改都不会被外界可见。  
最外层的 action 函数起着决定性作用，一个未被标记的，会连着调用两个 action 的事件处理函数，仍然会生成两个事务。    
默认情况下，所有状态的修改，都应该在 action 里， 这样有助于清楚的对状态更新发生的位置进行定位。  
action 另一个特征是它们是不可追踪的，即如果从 effect(autorun, reaction) 里，或者从计算值(很少见) 里调用 action, action 所访问的可观测状态，并不会被当作该副作用的依赖项。   

```js
class Doubler {
    value = 0

    constructor(value) {
        makeObservable(this, {
            value: observable,
            increment: action
        })
    }

    increment() {
        // 观察者不会看到中间状态.
        this.value++
        this.value++
    }
}
```
# action 函数
和 observable 类似，除了作为注解使用，还可以直接作为函数使用， 其返回值是与参数函数签名相同，且带有 action 注解的函数。  这在可观测对象本身没有设置 action 时很方便。
```js
const state = observable({ value: 0 })

// 将状态改变，包裹在  action 里面
const increment = action(state => {
    state.value++
    state.value++
})

increment(state)

// 除此之外，还可以直接调用 mobx 的 runInAction util
const state = observable({ value: 0 })

runInAction(() => {
    state.value++
    state.value++
})
```
如果在一个副作用里调用一个 action, 该 action 读取的可观测对象并不会被副作用当作依赖来监测。
# 异步 action
当在异步执行的情况下，需要更改 可观测对象的状态，那么需要将状态更改用 action 包装起来
```js
reaction(() => data, async () => {
    const res = await fetchResult();
    runInAction(() => {
        action1()
        action2()
    })
})
```