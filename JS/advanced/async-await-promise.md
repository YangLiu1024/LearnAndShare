# Promise + async + await
## promise
JS 为了支持异步操作，最开始加入了 Promise, Promise 支持 then() 和 catch() 异步操作。  
一个 Promise 在创建的时候，它的函数体其实是同步代码，会同步执行，这个时候 promise 的状态是 unknown, promise 一经创建则不可撤销，状态只能是 unknown/resolved/rejected 三者之一。  
且状态是不可查询的，只有当发生了状态改变时，回调才会被触发。  

## async + await
Promise 的问题在于，当操作比较复杂时，会有很多个 then, 以及错误处理很麻烦。为了解决这个问题，加入了 async 和 await.  
async 用来标记一个函数为 async 函数，await 用于等待 async 函数执行结束。  
什么叫做一个 async 函数执行结束呢，就是这个函数返回的 promise 要么 reject, 要么执行完所有的 then, 到最后返回一个确切的值。  
通过 async 和 await 的配合使用，developer 就可以通过 await 来让 一个 async 函数以看作同步的形式返回最后的返回值，而不用写大量的 then. 这样也让代码看起来像是同步执行一样，阅读性也提高了。

## JS 异步 - event loop
那么 JS 是怎么实现异步的呢？这就需要引入微任务队列和宏认为队列了。  
宏任务队列放置一些 setTimeout 和 setInterval 以及 IO 请求 和 网络请求的任务，在 nodejs 端还有 setImmediate 方法，在浏览器端有 requestAnimationFrame()  
微任务则放置一些 promise then/catch 添加的任务。nodejs 端还有 process.nextTick 方法，通过该方法添加的微任务优先级更高，比 then 更先执行。    
宏任务和微任务的区别就在于，当主线程执行完当前代码时，会切换到任务队列，且每次在切换到任务队列的时候，都会优先处理微任务队列，只有当微任务队列为空时，才会处理宏任务队列。  
需要注意的是，所有任务都是在主线程完成，当主线程和任务队列都为空时，程序结束。当在执行任务时增加了新的任务，则会添加到对应的任务队列中。  

# 执行顺序
## ***await expression***
await 总是会把后续代码添加到微任务队列，区别在于当 expression 的 类型不一样时，该 await 等待的时间不一样
1. 如果 expression 是 一个确切的值，比如 1，undefined, 无需等待
2. 如果 expression 是一个 thenable, 比如 {then(cb){cb()}}, 但是又不是 Promise, 则该微任务需要等待一个 then
3. 如果 expression 是一个 Promise, 效果和 #1 一样(TC39 对 await 后直接跟 Promise 做了优化，无需等待时间)
4. 如果 expression 是一个普通函数，其返回值等同于#1，#2，#3 的处理方式
5. 如果 expression 是一个 async 函数，如果其返回值是确切的值，则和 #1 一样，如果其返回值是一个 thenable, 则和 #2 一样，如果是一个 promise, 则其效果是在 promise 执行结束后等待两个 then 的时间，await expression 之后的代码才可以继续执行
### case1 await 确切的值
```js
// 1 3 2 4 7 5 6
async function test() {
    console.log(1)
    await null;
    console.log(2)
}

test().then(() => console.log(7))
console.log(3)

Promise.resolve()
    .then(() => console.log(4))
        .then(() => console.log(5))
            .then(() => console.log(6))
```
### case2 await thenable
```js
// 1 3 4 2 5 7 6
async function test() {
    console.log(1)
    await {
        then(cb) {
            cb()// must call cb to resolve this thenable
        }
    };
    console.log(2)
}

test().then(() => console.log(7))
console.log(3)

Promise.resolve()
    .then(() => console.log(4))
        .then(() => console.log(5))
            .then(() => console.log(6))
```
### case3 await promise
```js
// 1 3 2 4 7 5 6
async function test() {
    console.log(1)
    await new Promise((resolve) => {
        resolve()
    })
    console.log(2)
}

test().then(() => console.log(7))
console.log(3)

Promise.resolve()
    .then(() => console.log(4))
        .then(() => console.log(5))
            .then(() => console.log(6))
```
### case4 await async 函数返回确切值
```js
// 1 3 2 4 7 5 6
async function func() {
    return 1
}
async function test() {
    console.log(1)
    await func()
    console.log(2)
}

test().then(() => console.log(7))
console.log(3)

Promise.resolve()
    .then(() => console.log(4))
        .then(() => console.log(5))
            .then(() => console.log(6))
```

### case5 await async 函数返回 thenable
```js
// 1 3 4 2 5 7 6
async function func() {
    return {
        then(cb) {
            cb()
        }
    }
}
async function test() {
    console.log(1)
    await func()
    console.log(2)
}

test().then(() => console.log(7))
console.log(3)

Promise.resolve()
    .then(() => console.log(4))
        .then(() => console.log(5))
            .then(() => console.log(6))
```
### case6 await async 函数返回 promise
```js
// 1 3 4 5 2 6 7
async function func() {
    return Promise.resolve()
}
async function test() {
    console.log(1)
    await func()
    console.log(2)
}

test().then(() => console.log(7))
console.log(3)

Promise.resolve()
    .then(() => console.log(4))
        .then(() => console.log(5))
            .then(() => console.log(6))
```

### case7 await async 函数返回 promise
```js
// 1 [then8]
// 3 [then8 then4]
// 8 [then4 then9]
// 4 [then9 then5]
// 9 [then5, await func(2)]
// 5 [await func(1) then6]
// 6 [await func, then10]
// 2 [then10 then7]
// 10 [then7 then11]
// 7
// 11
async function func() {
    return Promise.resolve()
        .then(() => console.log(8))
        .then(() => console.log(9))
}
async function test() {
    console.log(1)
    await func()
    console.log(2)
}

test().then(() => console.log(7))
console.log(3)

Promise.resolve()
    .then(() => console.log(4))
        .then(() => console.log(5))
            .then(() => console.log(6))
                .then(() => console.log(10))
                    .then(() => console.log(11))
```
## **async + then**
如果不使用 await，只是 async 函数加上 then，那么
* 如果 async 函数返回值是确切值，那么无需等待，后续的 then 直接添加到微任务队列
* 如果 async 函数返回 thenable, 那么后续的 then 需要等待一个 then
* 如果 async 函数返回 Promise, 那么后续的 then 需要等待两个 then
* 如果 async 函数返回一个函数，那么该函数继续执行，其返回值的处理同 #1， #2， #3

### case1 async 返回确切值
```js
// 1 2 3
async function test() {
  return 1
}

test().then(() => console.log(1))
Promise.resolve()
  .then(() => console.log(2))
    .then(() => console.log(3))
```
### case2 async 返回thenable
```js
// 2 1 3
async function test() {
  return {
    then(cb) {
      cb()
    }
  }
}

test().then(() => console.log(1))
Promise.resolve()
  .then(() => console.log(2))
    .then(() => console.log(3))
```
### case3 async 返回 promise
```js
// 2 3 1
async function test() {
  return new Promise((resolve) => {
      resolve()
  })
}

test().then(() => console.log(1))
Promise.resolve()
  .then(() => console.log(2))
    .then(() => console.log(3))
```
### case4 async 返回 promise
```js
// 4 2 3 1 5
// 主线程执行 test, 添加 then4 到微任务队列
// 主线程添加 then2
// 执行then 4, 打印 4，test() 返回，因为返回的是 promise, 它之后的 then 添加到微任务队列，但需要等待两个 then
// 执行 then2, 打印 2， 添加 then3
// 执行 then3, 打印 3， 添加 then5
// 因为 test() 的 then 已经等待了两个 then, 所以可以执行，打印 1
// 执行 then5, 打印 5
async function test() {
  return new Promise((resolve) => {
      resolve()
  }).then(() => console.log(4))
}

test().then(() => console.log(1))
Promise.resolve()
  .then(() => console.log(2))
    .then(() => console.log(3))
        .then(() => console.log(5))
```
### case5 普通函数返回 promise
```js
// 4 2 1 3 5
function test() {
  return new Promise((resolve) => {
      resolve()
  }).then(() => console.log(4))
}
// 普通函数其实就相当于同步代码块
test().then(() => console.log(1))
Promise.resolve()
  .then(() => console.log(2))
    .then(() => console.log(3))
        .then(() => console.log(5))
```
所以 promise 的 then 就总是相当于 执行 await promise， 因为 await 的底层实现，其实就是 promise.then.  
换句话说，await expression 就总是相当于把之后的代码放在 expression 的 then 里面执行。  
## Test
```js
// 5 1 3 4 7 11 8 9 AAA 10 6
async function async1() {
    console.log(1)
    await async2();
    console.log('AAA')
}

async function async2() {
    console.log(3)
    return new Promise((resolve) => {
        resolve()
        console.log(4)
    })
}

console.log(5)
setTimeout(() => {
    console.log(6)
}, 0)

async1()

new Promise((resolve) => {
    resolve('')
    console.log(7)
})
.then(() => console.log(8))
.then(() => console.log(9))
.then(() => console.log(10))

console.log(11)
```