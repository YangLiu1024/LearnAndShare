# computed
计算值可以通过其它可观测对象派生而来，只有当它的依赖项发生变化，它的值才会自动随之变化。  
mobx 对计算值采用惰性求值，只有当真正被使用时，才会计算，且计算结果会被缓存。  
```js
class OrderLine {
    price = 0
    amount = 1

    constructor(price) {
        makeObservable(this, {
            price: observable,
            amount: observable,
            total: computed
        })
        this.price = price
    }

    get total() {
        console.log("Computing...")
        return this.price * this.amount
    }
}

const order = new OrderLine(0)

const stop = autorun(() => {
    console.log("Total: " + order.total)
})
// Computing...
// Total: 0

console.log(order.total)
// (不会重新计算!)
// 0

order.amount = 5
// Computing...
// (无需 autorun)

order.price = 2
// Computing...
// Total: 10
```
只有当计算值真正的改变，才会触发依赖该计算值的 effect

## 使用 computed(expression)
常规的 computed 都是作用于 getter, 没有参数，但是有的场景下，计算需要参数。比如对于一个 list, 判断 元素是否选中
```js
// 通常我们直接使用 store.isSelected(id), 这里 observer 组件会检测并订阅 isSelected 内部的任何 可观测对象的变化
// 因为 store.isSelected 函数的执行，时作为被跟踪的渲染函数的一部分
const Item = observer(({ item, store }) => (
    <div className={store.isSelected(item.id) ? "selected" : ""}>
        {item.title}
    </div>
)
// 上述例子的问题在于，所有 Item 选项，都会去订阅 isSelected 所涉及到的可观测对象，在对象变化时，所有 Item 组件都需要重新运行。
// 一般情况下，这种方式完全 ok, 并且是一种默认的优秀策略

// 一种优化方式是，将计算结果缓存起来
// 这样，即使组件仍然订阅了 isSelected 涉及到的可观测对象，但是只会在 其值发生变化时，才会重新渲染 Item 组件
const Item = observer(({ item, store }) => {
    const isSelected = computed(() => store.isSelected(item.id)).get()
    return (
        <div className={isSelected ? "selected" : ""}>
            {item.title}
        </div>
    )
}
```
