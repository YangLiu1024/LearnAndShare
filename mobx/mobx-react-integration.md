# React integration
mobx, mobx-react, mobx-react-lite 通常是一起使用的，mobx-react-lite 提供了一种 observe 的 HOC 方法
```js
import React from "react"
import ReactDOM from "react-dom"
import { makeAutoObservable } from "mobx"
import { observer } from "mobx-react-lite"

class Timer {
    secondsPassed = 0

    constructor() {
        makeAutoObservable(this)
    }

    increaseTimer() {
        this.secondsPassed += 1
    }
}

const myTimer = new Timer()

//被`observer`包裹的函数式组件会被监听在它每一次调用前发生的任何变化
const TimerView = observer(({ timer }) => <span>Seconds passed: {timer.secondsPassed}</span>)

ReactDOM.render(<TimerView timer={myTimer} />, document.body)

setInterval(() => {
    myTimer.increaseTimer()
}, 1000)
```
observer HOC 会自动监测组件渲染过程中使用的所有 mobx 可观测对象，当观测对象改变时，组件会触发 re-render.   
组件使用的 可观测对象可来自于：
* 参数传递 => 由上层组件传递
* 全局变量 => 通过 import 方式导入，在组件中直接使用
* react context => 将 可观测对象包裹在一个 context 里，通过 react context 传递
* 组件里构造 

## 传递可观测对象到非 observer 组件
一般来说，组件如果使用了可观测对象，那么就应该使用 observer 包裹。因为在 可观测对象引用不变(通常都是对象的属性值被改变)的情况下，非 observer 子组件并不能感知到属性值的变化。
```js
class Todo {
  title = ''
  status = 'not start'

  constructor() {
    makeAutoObservable(this, {})
  }
}

const todo = new Todo();

// case1
const App = observer(() => {
  return (
    <>
    parent
    <br/>
    <Child item={todo}/>
    </>
  )
})

// App 是 observer 组件，但是 Child 不是。 当 todo 属性改变时，因为 App 本身没有引用 todo, 所以 App 不会重新渲染
// App 不重新渲染，Child 也不是 observer 组件，所以 Child 也不会重新渲染
const Child = ({item}) => {
  return (
    <div className='App'>
      <h1>{item.title}</h1>
      <h2>{item.status}</h2>
    </div>
  );
}

// case 2
// App 组件引用了 todo 属性, 当 todo.title 改变时，App 会重新渲染，Child 也会随着重新渲染
// 这里如果把 Child 改成 memo 的组件，那么 Child 则不会重新渲染
const App = observer(() => {
  return (
    <>
    parent {todo.title}
    <br/>
    <Child item={todo}/>
    </>
  )
})

// case3
// Child 不再以 todo 为参数，而是以 todo 的属性作为参数。 那么当属性变化时，App 会重新渲染，从而 Child 也会重新渲染
const App = observer(() => {
  return (
    <>
    parent
    <br/>
    <Child title={todo.title} status={todo.status}/>
    </>
  )
})

function Child({title, status}) {
  return (
    <div className='App'>
      <h1>{title}</h1>
      <h2>{status}</h2>
    </div>
  );
}

// case4
// Child 不是直接使用可观测对象，而是通过 callback 调用. 对于 App 来说，它并没有真正使用可观测属性，这个函数的调用，其实发生在 Child 组件里
// 这就导致 可观测属性改变时，App 并不会重新渲染。 而 Child 组件在渲染时，虽然真正访问了可观测对象，但是它本身并不是 observer 组件，所以也不会重新渲染
// 和 case 1 类似，当 Child 组件是 observer 组件后，Child 组件就能正确刷新 
const App = observer(() => {
  return (
    <>
    parent
    <br/>
    <Child title={() => todo.title} status={() => todo.status}/>
    </>
  )
})

function Child({title, status}) {
  return (
    <div className='App'>
      <h1>{title()}</h1>
      <h2>{status()}</h2>
    </div>
  );
}

///////////////////////////////////////////////////
setTimeout(() => {
  console.log('changed title')
  todo.title = 'hello mobx'
  todo.status = 'Done'
}, 2000)
```
## 子组件的回调函数引用了可观测对象
如果子组件定义了回调函数，该回调函数可以访问父组件的可观测对象，但是子组件本身并不是 observer 组件
```js
const TodoView = observer(({ todo }: { todo: Todo }) => {
    // 错误: GridRow.onRender 不能获得 todo.title / todo.done 中的改变
    //        因为它不是一个观察者（observer） 。
    return <GridRow onRender={() => <td>{todo.title}</td>} />

    // 正确: 将回调组件通过Observer包裹将会正确的获得变化。
    return <GridRow onRender={() => <Observer>{() => <td>{todo.title}</td>}</Observer>} />
})
```

## mobx-react vs mobx-react-lite
mobx-react 是 mobx-react-lite 的超集，它囊括了一些其它不太需要的功能，比如对 react class component 的支持，等等

## observer vs memo
observer 会自动使用 memo. memo 的作用在于，当 props 被监测到并没有改变时，则不会触发重新渲染。当然，memo 不能处理 context 变化所带来的重新渲染。  
observer 则还会监测组件渲染过程中，调用的可观测对象。

## useEffect & observable
如果想在 effect 里访问可观测对象，一个是把使用的所有属性，全部列在依赖项里，react 会保证 effect 正确执行。  
另一个就是在 effect 里使用 autorun 或者 reaction, 访问的可观测属性则没有必要写在依赖列表里。
```js
// effect 返回的是 autorun 的 disposer, 这样保证在组件卸载时，可以清除掉相关监听
useEffect(() => autorun(() => {
    console.log(todo.title)
}), []);
```
同样的，如果组件不是 observer 的，那么组件并不会监听引用的可观测对象的变化，继而重新渲染组件。
```js
// App 不是 observer 的，那么即使内部访问了 timer.time, 且 timer.time 一直在改变，但是组件并不会重新渲染，effect 也不会触发
function App() {
  useEffect(() => {
    const handle = setInterval(() => timer.increase(), 1000);
    return () => {
      clearInterval(handle)
    }
  }, [])

  useEffect(() => {
    console.log('time ')
  }, [timer.time])

  // 如果想在 非 observer 组件 effect 里响应可观察对象的变化，那么可以使用 autorun 或者 reaction
  useEffect(() => autorun(() => console.log(timer.time)), [])
  return (
    <div className='App'>
        {timer.time}
    </div>
  );
}
```