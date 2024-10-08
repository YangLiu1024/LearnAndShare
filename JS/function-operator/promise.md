# Introduction to Promise

Promise 对象代表了未来将要发生的事件，用来传递异步操作的消息。

## Promise 的特点
它有两个特点：
 * 对象的状态不受外界影响。 它有三种状态，
   - pending 初始状态，既不是成功，也不是失败
   - fulfilled 操作成功状态
   - rejected 操作失败
   只有异步操作的结果，可以决定当前是哪一种状态
 * 一旦状态改变，就不会再变，任何时候都可以得到这个结果。即当状态改变后，对同一个 promise 对象调用多次回调函数，它的结果是相同的
 
 ## Promise 的优缺点
 Promise 的优点在于可以将异步操作以同步操作的流程表达出来，避免了层层嵌套的回调函数。此外，Promise 对象提供统一的接口，使得控制异步操作更加容易。
 
 Promise 的缺点在于，
  * promise 无法取消，一旦新建，它就会执行，无法中途取消
  * 如果不设置回调函数，promise 内部抛出的错误，不会反应到外部
  * 当处于 pending 状态时，无法得知当前进展到哪一个阶段，是刚刚开始，还是即将完成
## Promise 的创建和调用
```javascript
var promise = new Promise((resolve, reject) => {
  //do something here
  //if succeed, call resolve(data)
  //if fail, call reject(error)
});
//if succeed, will enter then, if fail, will enter catch
promise.then(data => {}).catch(error => {}) //equal to promise.then(onFulfilled, onReject)
```
需要注意的是，
* 当 resolve(data) 中， data 本身也是一个 Promise 对象时，那么 data 的状态会传递给 promise， 如果 data是 pending, promise的回调函数就需要等待 data的状态改变，才会调用自己的回调函数
* 每一个 catch 都只会尝试抓住它之前的操作的 error，如果执行到它时没有 error 发生，那么这个 catch 就会被跳过。
* 如果在 then 里面 return 一个返回值，当这个返回值是 promise 对象，则会直接返回，且后续的回调需要等待该返回值状态改变，如果不是，则会将返回值包裹成一个新的 promise 对象，且状态为 fulfilled,之后的回调函数会立即执行

## Promise all/race/any/allSettled
Promise.all 方法将多个 promise 包裹成一个新的 promise 实例。
```js
var p = Promise.all([p1,p2,p3])//p1,p2,p3 is promise object
```
p 的状态由 p1,p2,p3 共同决定
 * 只有当 p1,p2,p3 的状态都变成 fulfilled 时， p 的状态才会变成 fulfilled。且将 p1,p2,p3 的返回值组成数组，作为参数，传给 p 的回调
 * 当 p1,p2,p3 中任何一个变成 rejected, p 的状态就会变成 rejected，且将它的返回值传给 p 的回调函数

```js
var promises = [1,2,3,4,5].map(i => return axios(i));

Promise.all(promises).then(datas => {}).catch(err => {})
```
race 和 all 类似，都是把多个 promise 对象包裹成一个新的 promise 对象，区别在于，race 的 promises 中，race 的状态总是由数组中第一个敲定的状态决定。  
当 all/race 方法参数里包含不是 promise 的参数，则会调用 Promise.resolve(param) 尝试把 param 转换为 promise对象。如果该参数不具有 then 方法, 则将它直接包裹在 fulfilled 的 promise 对象里返回，对于 race, 后续的回调会直接执行，对于 all, 则还需要等待其它 promise 参数。

数组元素都是同步一起执行的, 比如 Promise.all([p1, p2, p3]), p1, p2, p3 是同步开始执行的

* all 是等待数组所有元素 fulfiled, 如果有一个 promise rejected, 则 reject. 如果数组为空，则返回 fulfilled promise. 如果不为空，则先返回 pending promise.
* race 是竞争关系，返回的 promise 状态由第一个敲定状态的 promise 决定。如果数组为空，那么 返回的 promise 状态会一直是 pending, 如果不为空，不管元素状态是不是 settled, 首先返回 pending 的 promise, 之后再根据第一个 settled 的 promise 敲定状态。
* allSettled 是等待数组所有元素敲定状态。如果数组为空，则返回 fulfilled promise, 否则返回 pending promise first. 最后的结果是所有 元素的结果的数组，结果类型为 {status: 'fulfilled' | 'rejected', value?: any, reason?: any}
* any 是等待数组中某一个元素 fulfilled, 直到所有元素都 reject. 如果数组为空，则直接返回 rejected promise. 如果最后所有元素都 reject, 则返回一个 AggregateError
一个比较奇妙的用法是使用 race 检查 promise 的状态
```js
function promiseState(promise) {
  const pendingState = { status: "待定" };

  // Promise.race 会找到第一个 settled 的 promise, 如果 promise 已经 settled, 则 value 将会是 promise 的 value, 否则 value 将会是 pendingState
  return Promise.race([promise, pendingState]).then(
    (value) => (value === pendingState ? value : { status: "已兑现", value }),
    (reason) => ({ status: "已拒绝", reason }),
  );
}

```