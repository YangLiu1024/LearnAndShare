# 状态批处理

```js
      <button onClick={() => {
        setNumber(number + 1); // 将 replace with number + 1 压入队列
        setNumber(number + 1); // 将 replace with number + 1 压入队列
        setNumber(number + 1); // 将 replace with number + 1 压入队列
      }}>+3</button>
```
在 react button 的 click handler 里，对同一状态调用三次状态变化，那么 react 会怎么处理呢？  
对于每一个更新，其实效果都是一样的，都是 replace with number + 1, 这里的 number 在三个语句里都是一样的。那么最后相当于只执行了一次 replace with number + 1

首先，在调用回调时，传入回调的参数都只是当前帧状态的快照值。即回调函数里的 number 一直都是 0.   
另外，react 会等到所有事件处理函数执行完毕，然后再处理状态更新，这就允许在重新渲染之前，可以接收到来自不同组件，或者不同状态的改变，继而避免触发过多的重新渲染。这称之为批处理。    
这也是为什么只有当回调执行结束，才会触发重新渲染。这也意味着，在事件处理程序及其中的任何代码完成之前，UI 不会更新。  
需要注意的是，React 不会批处理用户的点击事件，每一个点击事件都是单独处理的。  

还有一种情况是在下一次渲染之前，多次更新相同的状态. react 会把 ***n => n + 1*** 这个函数排入队列，在事件响应程序其它代码执行结束之后再来处理这类状态更新函数。***状态更新函数都是在渲染期间执行的***, 所谓的渲染期间，即是执行 function component。在执行完所有渲染代码后，会进行比较，然后提交 diff. 最后才是真正的 DOM 渲染。    
在这个例子中，因为有三个状态更新函数压入队列，每一个状态更新函数的输入都是上一个函数的输出，而且由于批处理，最后只会根据最后的状态来渲染 UI
```js
      <button onClick={() => {
        setNumber(n => n + 1);
        setNumber(n => n + 1);
        setNumber(n => n + 1);
      }}>+3</button>
```
对于另一种情况，如果直接设置状态，和状态更新函数一起使用，又会有什么效果呢？这个时候，首先将 replace value with n+5 压入队列，最后再执行 n => n + 1, 那么最后相当于 n + 6
```js
      <button onClick={() => {
        setNumber(n + 5);
        setNumber(n => n + 1);
      }}>+3</button>
```
那么如果将这两句代码顺序调换一下呢？最后的结果会是 n + 5, n => n + 1 并不会生效
```js
      <button onClick={() => {
        setNumber(n => n + 1);
        setNumber(n + 5);
      }}>+3</button>
```
所以总结一下 state update 的规律：
1. 状态更新函数会被添加到队列，并在渲染期间执行。这也就要求状态更新函数必须是纯函数，不能包含任何副作用
2. 如果有 replace with value, 那么队列中之前的所有的状态更新都会被忽略

还有一个问题，当状态更新函数被忽略后，它们还会被执行吗？如果执行，它的返回值会怎么处理？答案是，状态更新仍然会被执行，其返回值会被直接丢弃

# 状态的引用
如果状态本身是一个对象，如果想触发重新渲染，则必须改变状态的引用，改变状态内部的值，并不能触发重新渲染。
```js

const [person, setPerson] = useState({
    name: 'abc',
    age: 10,
})

// 直接修改 person 的内部属性并不会触发重新渲染
function changeAge(age) {
    person.age = age;
}

// 这个 effect 却会在 changeAge 后触发，因为它的依赖项是 person.age, 而不是 person
useEffect(() => {}, [person.age])
```
在 react 的各种 hook, 一定要清楚它的依赖项是针对于引用。