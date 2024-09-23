# observable 
observable 是 mobx 最核心的概念， 如果一个变量被声明为 observable, 那么它的值将会被监测，当值发生改变，就会触发对应的 reactions.   
在此基础上，也衍生了一些其它的概念，比如说 *computed*, 计算属性，即该属性是通过其它属性计算得来，和 react useMemo 类似。  
然后 mobx 的状态更新，都需要被包装在一个 *action* 里面. action 对状态的改变是原子性的，在 action 完成之前，外部感知不到状态的变化。    

那么怎么创建这些可观测对象呢？ mobx 提供了几种方法
* makeObservable(target, annotions?, options?)  
这个函数可以捕捉对象 target 的属性，并且使它们可观察。 一般，这个函数在类的构造函数中调用，并且它的第一个参数为 *this*.  
annotions 是为属性提供注解，
```js
import { makeObservable, observable, computed, action } from "mobx"

class Doubler {
    value: number

    constructor(value) {
        makeObservable(this, {
            value: observable,
            double: computed,
            increment: action,
        })
        this.value = value
    }

    get double() {
        return this.value * 2
    }

    increment() {
        this.value++
    }
}
```
* makeAutoObservable(target, overrides?, options?)  
这个是自动版的 makeObservable, 即target 的所有自有属性，都会被标记为 observable， 所有 setter 都会被标记为 action, 所有 getter 都会被标记为 computed.  
为了取消某一些属性的默认行为，可以在 overrides 里进行配置， 将属性配置为 false, 那么该属性将不会被注解，适用于一些只读字段。  
```js
import { makeAutoObservable } from "mobx"

function createDoubler(value) {
    return makeAutoObservable({
        value,
        get double() {
            return this.value * 2
        },
        increment() {
            this.value++
        }
    })
}
```
另外，makeAutoObservable 不能在子类中使用，另外，使用了 makeAutoObservable 的类，也不能再有子类
```js
class A {
    constructor() {
        // 如果 A 有子类，那么在构造 子类的时候，那么必将以 子类的 this 来运行 A 的构造函数，那么将会违反 mobx 的限制
        makeAutoObservable(this)
    }
}
// 即使 A 没有调用 makeAutoObservable， B 也不可以再使用 makeAutoObservable， 因为它是子类
class B extends A {

}
```
* observable(source, overrides?, options?)  
*observable* 除了用作注解，还可以直接当作函数使用, 用法和 makeAutoObservable 类似
```js
import { observable } from "mobx"

const todosById = observable({
    "TODO-123": {
        title: "find a decent task management system",
        done: false
    }
})

todosById["TODO-456"] = {
    title: "close all tickets older than two weeks",
    done: true
}

const tags = observable(["high prio", "medium prio", "low prio"])
tags.push("prio: for fun")
``` 
*observable* 和其它两种方式的区别在于，
* observable 会复制 source 对象，返回一个 proxy 副本对象，而 makeObservable/makeAutoObservable 都是在原对象上进行修改进而达到监控。 
* observable 是创建一个 Proxy 对象，以遍能够监控添加/删除字段，使新添加的字段也成为可观察的属性 

# options
上述的 api 都支持 *options* 配置, 但 autoBind/deep 一般都只在 makeAutoObservable 里使用，因为 makeObservable 本身就要求显示使用 annotations，那么 options 就对这些显示指定注解的 field 不起作用。
* autoBind: boolean, 表示是否自动 bind action/flow 中的 this, 默认值为 false, 大部分情况下，都需要设置为 true, 或者手动使用 action.bound, flow.bound 注解
* deep: boolean, 表示是否对嵌套的属性继续往下遍历监测，默认值为 true
* proxy: boolean, 对 observable 起作用，表示是否使用 proxy 版本，如果为 false, 则不会返回 Proxy 对象

# annotations

| Annotation | Description |  
| :-----------:| :------------|
| *observable*<br/>*observable.deep* |  字段将会根据自己类型，被深度转换为 可观察对象|
| *observable.ref* |  类似于 observable, 但是并不会递归转换，只监测最外层引用|
| *observable.shallow* |  类似于 observable.ref, 但是只作用于集合, 集合元素的增删会被监测，但是元素内部的变化不会|
| *action*| 把一个函数标记为 修改 state 的函数|
| *action.bound* | 类似于 action, 并且会把函数里的 this 绑定到实例 |
| *computed* | 将一个 getter 标记为可缓存的计算属性|
| *computed.struct | 类似于 computed, 但是如果重新计算得到的值在结构上与之前的结果相等，那么观察者将不会得到通知 |
| *true* | 自动推断为最佳注解，和应用了 makeAutoObservable 一样 |
| *false* | 该属性不添加任何注解 | 
| *flow* | 创建一个 flow 管理异步进程 |
| *flow.bound* | 类似于 flow, 但是将 this 绑定到实例|
| *override* | 用于子类覆盖继承的 action/flow/computed/action.bound |
| *autoAction* | 不应被显示调用 |

```js
// 测试一下 observable.ref/shallow/struct 的使用
const source = () => ({
  x: 1,
  y: {
    z: 2
  },
  flag: false,

  items: [] as {value: number}[],

  changeX() {
    this.x++;
  },

  changeY() {
    this.y = {z: this.y.z}
  },

  changeZ() {
    this.y.z = this.y.z + 1;
  },

  toggle() {
    this.flag = !this.flag;
  },

  addItem() {
    this.items.push({value: Math.random()});
  },
  replaceItem() {
    this.items[0] = {value: Math.random()};
  },
  updateItemValue() {
    this.items[0].value = 100;
  },

  get sum() {
    return this.x + this.y.z;
  }
})
    const target = makeObservable(source(), {
      x: observable,
      changeX: action.bound,
      y: observable.ref,
      changeY: action.bound,
      changeZ: action.bound,
      items: observable.shallow,
      addItem: action.bound,
      replaceItem: action.bound,
      updateItemValue: action.bound,
    });
    autorun(() => {
      console.log('make observable x ', target.x, ' z ', target.y.z, ' items ', target.items.map(v => v.value).join(','));
    });
    const {
      changeX,
      changeY,
      changeZ,
      addItem,
      replaceItem,
      updateItemValue,
    } = target;
    changeX();
    // 当使用 observable.struct 时，changeY 并不会触发 autorun, mobx 会认为该对象没有变化. 如果使用 observable.ref, 则会触发
    changeY();
    // 不管使用 observable.struct 还是 observable.ref, 改变 z 都不会触发 autorun, 因为 此时的 observable 并不是 deep 的，那么底层的改动，并不会触发 autorun
    changeZ();
    // observable.shallow 可以监测集合对象 添加/删除 元素， 以及整个元素的变化，但是监测不了元素内部的变化
    // 所以 addItem 和 replaceItem 都会触发 autorun, 但是 updateItemValue 因为是更新内部的值，所以并不会被感知
    addItem();
    replaceItem();
    updateItemValue();
```
# 局限性
* make(Auto)Observable 仅支持 target 已经定义的属性，确保在调用 make(Auto)Observable 之前，所有属性都已经被赋值。如果没有正确配置，已经声明，但是没有初始化的属性，并不会被正确侦测到
* makeAutoObservable 只能在基类中使用，且不能有子类
* makeObservable 只能注解由它自己声明的属性，每个字段只能被注解一次(除了 override)。
* 父类的 action,action.bound, computed, flow 可以被子类 覆盖
* 尽量不使用继承，多使用组合。

```js
class Timer {
    time

    constructor(time) {
        // 如果在调用 makeAutoObservable 之前，属性并没有被初始化， 那么该属性并不会被转化为可观测属性
        makeAutoObservable(this, {})
        this.time = time;
    }

}
```