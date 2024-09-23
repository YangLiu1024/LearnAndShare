当可观察状态发生改变时，其观察者的回调应该自动调用。

# autorun
```js
// autorun 会自动检查 effect 回调里所引用的所有可观测状态(比如 observable 或者 computed)，当状态改变时，该 effect 会自动再次执行
// 值得注意的是，autorun 第一次执行时，里面的回调也会执行一次
const disposer = autorun(effect: (reaction) => void, options?)

// for example
autorun(() => {
    console.log(obs.name)
})
```

# reaction
```js
// reaction 和 autorun 很类似，但是有几点不同
// 1. autorun 自动监测 effect 回调里所有使用的状态，reaction 只依赖在 data 函数返回的数据，但其 effect 函数可以使用data 函数返回值以外的其它数据
// 2. reaction 在执行时，不会默认调用 effect 回调，只会在依赖项发生改变时，才触发回调

const disposer = reaction(data: () => value, effect: (value, previousValue, reaction) => {}, options?)
```
reaction 类似于 autorun, 但是让你可以更精细的控制依赖项。第一个函数，返回需要观察的对象，该观察对象的值，会作为参数传入第二个回调函数。  
需要注意的是，回调只会在第一个函数中所返回的状态发生改变时，才会触发回调，但是回调实际上可以使用更多的状态。  
一般的模式下 data 函数会返回 effect 回调里所有使用到的状态，并以这种方式，精确控制回调触发的时机。

# when
```js
// when 会观察并运行 predicate 函数，一旦 predicate 函数返回 true, effect 函数就会被执行，且执行后将被清理
// 如果不传入 effect 函数，那么 when 函数将会返回一个带有 cancel 的 Promise, 后续代码可以使用 then 或者 cancel 来处理返回的 promise
const disposer = when(predicate: () => boolean, effect?: () => void, options?)
when(predicate: () => boolean, options?): Promise<void> & {cancel(): void}
```

# options
reaction 函数都有一个 options, 该 options 可以用来进一步定制 reaction/autorun/when 的行为
* fireImmediately: boolean => 作用于 reaction, 指示在第一次运行 data 函数时，是否执行 effect 函数
* delay: number,=> 单位是 ms, 用来指定 reaction/autorun effect 函数节流的毫秒数
* timeout: number => 作用于 when, 用来指定需要等待的时间，当等待时间到达，条件仍没有达到，那么 when 将会 reject 或者 抛出错误
* equals => 作用于 reaction, 用来判断 data 函数返回的上一个值和下一个值，默认使用 comparer.default, 当且仅当该函数返回为 false 时，才会触发 effect 函数


# 规则

1. 如果可观测对象状态发生变化，那么该对象的 reactions 会在当前 action（如果嵌套在 action 里，那么会在最外层 action ） ***执行结束后*** 开始同步运行(mobx 并不保证运行顺序)
2. autorun 只会跟踪在在 effect 里同步执行过程中读取的可观测对象，不会跟踪异步执行过程中的可观测对象
3. autorun 不会跟踪在 effect 里调用的 action 中读取的可观测对象， action 
4. 传递给 reaction, autorun, when 的 effect 函数，只会在它所观察的所有可观测状态都 GC 之后，才会被 GC. 为了自动 GC, 它们都会返回一个 disposer 函数，调用该函数，可以取消订阅
5. reaction 不应该在 effect 函数里调用 action, 它只是用来触发副作用。而且，一般只在引起状态变化的一方和副作用的效果没有直接联系时，才会使用 reaction.
6. reaction 之间应该是 独立 的，不能有依赖关系，因为 reaction 的触发是不保证先后顺序的

```js
    const target = observable({
      x: 1,
      y: 2,
      changeX() {
        this.x++;
      },
      changeY() {
        this.y += 2;
      },
    });
    // 代码执行到这里，await 1 会立即执行，但是之后的代码会进入微任务队列。这里同步过程中，只访问了 target.x
    // queue: [await 1]
    autorun(async () => {
      console.log('ax', await target.x);
      console.log('ay', await target.y);
    })
    // 代码执行到这里，同步打印 x1, y2
    autorun(() => {
      console.log('x', target.x);
      console.log('y', target.y);
    })
    // 这改变了 x 的值，触发第一和第二个 reaction. 
    // 第一个 reaction 往 微任务队列里继续添加， queue: [await 1, await 2]
    // 第二个 reaction 打印 x2, y2
    target.changeX();
    // 这里改变了 y 的值，因为 第一个 reaction 依赖项并没有捕获到 y, 所以第一个 reaction 并不会触发
    // 第二个 reaction 触发，打印 x2, y4
    target.changeY();

    // 最后开始执行微任务队列中 第一个 await 1
    // 打印 ax1, 然后微任务队列添加 task [await 2, await 4]
    // 处理第二个 await 2, 打印 ax2, 添加 task [await 4, await 4]
    // 继续处理，打印 ay4, ay4
```