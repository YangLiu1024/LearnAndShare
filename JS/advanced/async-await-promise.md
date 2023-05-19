# Promise + async + await
## promise
JS 为了支持异步操作，最开始加入了 Promise, Promise 支持 then() 和 catch() 异步操作。  
一个 Promise 在创建的时候，它的函数体其实是同步代码，会同步执行，这个时候 promise 的状态是 unknown, promise 一经创建则不可撤销，状态只能是 unknown/resolved/rejected 三者之一。  
且状态是不可查询的，只有当发生了状态改变时，回调才会被触发。  

## async + await
Promise 的问题在于，当操作比较复杂时，会有很多个 then, 以及错误处理很麻烦。即使已经比 callback 好了很大，但为了解决这个问题，加入了 async 和 await.  
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
<s>await 总是会把后续代码添加到微任务队列，区别在于当 expression 的 类型不一样时，该 await 等待的时间不一样
1. 如果 expression 是 一个确切的值，比如 1，undefined, 无需等待
2. 如果 expression 是一个 thenable, 比如 {then(cb){cb()}}, 但是又不是 Promise, 则该微任务需要等待一个 then
3. 如果 expression 是一个 Promise, 效果和 #1 一样(TC39 对 await 后直接跟 Promise 做了优化，无需等待时间)
4. 如果 expression 是一个普通函数，其返回值等同于#1，#2，#3 的处理方式
5. 如果 expression 是一个 async 函数，如果其返回值是确切的值，则和 #1 一样，如果其返回值是一个 thenable, 则和 #2 一样，如果是一个 promise, 则其效果是在 promise 执行结束后等待两个 then 的时间，await expression 之后的代码才可以继续执行

这里所谓的等待时间，到底是什么呢？等待机制又是怎样的呢？  
* 对于 thenable, 当执行到 await thenable 的任务时，底层实现其实是先用一个 promise 包装了 thenable 对象，并且执行了它的 then 方法，这样就消耗了一次调度时间，在执行完 thenable 的 then 方法后，再把 await 之后的代码添加到微任务队列末尾。  
* 对于 返回 promise 的 async 函数，两次等待是底层对 promise 对象的处理，两次等待对于 developer 来说没做什么事情，作用只是每次等待都把任务重新添加到微任务队列末尾。

需要注意的是，node10 的版本对于这种机制有修改，之后的版本又改了回去。
```js
const p = Promise.resolve();

(async () => {
  await p; console.log('after:await');
})();

p.then(() => console.log('tick:a'))
 .then(() => console.log('tick:b'));

// node 10
// tick:a
// tick:b
// after:wait

// other node version
// after:wait
// tick:a
// tick:b
```
</s>
1. 对于 *expression*，如果是确切值，比如 1，null, await f() 就不需要等待，后续代码直接添加到微任务队列
2. 对于 *expression*，如果是 thenable, 那么会先将 thenable.then 方法推进任务队列，当 thenable.then 执行结束，将之后的代码推进任务队列
3. 对于 *expression*， 如果是 Promise,如果状态时 fulfilled, 则直接将后续代码加入到任务队列，如果不是，则等当该 promise 的状态变成 fulfilled 时，将后续代码添加到微任务队列
4. 对于 *expression*，如果是 普通函数，对于其返回值，其处理和 #1，#2，#3，#4 一样
5. 对于 *expression*，如果是 async 函数，如果函数返回值是确切值，其处理和 #1 一样，如果是 thenable,处理和 #2 一样，如果是 Promise, 首先正常处理该 promise, 在函数返回时，async function 会返回一个新的 pending promise p2, 同时把 p.then 方法添加到微任务队列。然后 在执行 p.then 后，如果 p的状态变成了 fulfilled, 则添加一个新的任务来 resolve p2。否则等待 p 在 fulfilled 时，添加一个新的任务来 resolve p2. 在 resolve p2 后，则将后续代码添加到微任务队列。

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
// 1 [await-then]
// 3 [await-then then4]
// execute thenable [then4 await-then]
// 4 [await-then then5]
// 2 [then5 then7]
// 5
// 7
// 6
async function test() {
    console.log(1)
    await {
        then(cb) {
            console.log('execute thenable')
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
### case6 await async 函数返回 settled promise
```js
// 1 3 4 5 2 6 7
// 1 [await-p]
// 3 [await-p then4]
// [then4 await-p]
// 4 [await-p then5]
// [then5 await-p]
// 5 [await-p  then6]
// 2 [then6 then7]
// 6
// 7
async function func() {
    // 返回的 promise 已经 settled
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

### case7 await async 函数返回 unsettled promise
```js
// 1 [then8] 'after func'
// func 返回的 p 是 unsettled 的 promise, await func() 需要挂起
// 3 [then8 then4]
// 8 [then4 then9]
// 4 [then9 then5]
// 9, 当then9 执行结束时，p 的状态就变成了 settled, [then5, await-func]
// 5, [await-func then6]
//  [then6 await-func]
// 6 [await-func then10]
// 2 [then10 then7]
// 10
// 7
// 11
async function func() {
    const p = Promise.resolve()
        .then(() => console.log(8))
        .then(() => console.log(9))
    console.log('after func')
    return p;
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
```js
// await pending promise in async function
// start [then5] resolved [then5 then12] 'after promised'
// async function f() 会把返回值 promise p wrap 成新的 pending promise p2, 同时执行 p.then [then5 then12 p.then then2 thenA]
// 5 [then12 p.then then2 thenA then6]
// 12, 执行完 then12 后，p 的状态变成 fulfilled, [p.then then2 thenA then6]
// 执行 p.then, 然后 p 的状态已经是 fulfilled, 添加 resolve-p2 任务 [then2 thenA then6 resolve-p2]
// 2 [thenA then6 resolve-p2 then3]
// A [then6 resolve-p2 then3 thenB]
// 6 [resolve-p2 then3 thenB then7]
// [then3 thenB then7 then1]
// 3 [thenB then7 then1 then4]
// B [then7 then1 then4 thenC]
// 7 [then1 then4 thenC then9]
// 1 4 C 9 8 
async function f() {
    const p = new Promise((resolve) => {
	    resolve('')
        console.log('resolved')
    })
    .then(() => console.log(12))
    console.log('after promised')
    return p;
}

async function test() {
    await f()
    console.log(1)
}
console.log('start')

Promise.resolve()
    .then(() => console.log(5))
        .then(() => console.log(6))
            .then(() => console.log(7))
                        .then(() => console.log(9))

test().then(() => console.log(8))
Promise.resolve()
    .then(() => console.log(2))
        .then(() => console.log(3))
            .then(() => console.log(4))

Promise.resolve()
    .then(() => console.log('A'))
        .then(() => console.log('B'))
            .then(() => console.log('C'))
```
```js
//await thenable
// start [then5] 'after thenable'
// async function 返回的是 thenable 对象p, 会把它包裹为 promise p2, 这个时候会把 thenable.then 方法推到任务队列，[then5 p.then]
// [then5 p.then then2 thenA]
// 5 [p.then then2 thenA then6]
// executed thenable method, 因为没有额外的 promise, 这个时候 p2 状态变为 fulfilled, [then2 thenA then6 then1]
// 2 [thenA then6 then1 then3]
// A [then6 then1 then3 thenB]
// 6 [then1 then3 thenB then7]
// 1 [then3 thenB then7 then8]
// 3 [thenB then7 then8 then4]
// B [then7 then8 then4 thenC]
// 7 [then8 then4 thenC then9]
// 8 4 C 9
async function f() {
    const p = {
	then(cb) {
          console.log('execute thenable method')
          cb()
        }
    }
    console.log('after thenable')
    return p;
}
async function test() {
    await f()
    console.log(1)
}
console.log('start')

Promise.resolve()
    .then(() => console.log(5))
        .then(() => console.log(6))
            .then(() => console.log(7))
                        .then(() => console.log(9))

test().then(() => console.log(8))
Promise.resolve()
    .then(() => console.log(2))
        .then(() => console.log(3))
            .then(() => console.log(4))

Promise.resolve()
    .then(() => console.log('A'))
        .then(() => console.log('B'))
            .then(() => console.log('C'))
```
```js
// await unsettled promise directly
// start [then1]
// f() await 一个 pending promise directly, not wrapped in async function
// 因为 p 现在没有 fulfilled, 所以不能添加到任务队列
// [then1 then4]
// 1， 执行 then1 之后，p 变成 fulfilled, [then4 thenf then7]
// 4 [thenf then7 then5]
// f, 因为 f() 本身返回的是 undefined, 没有额外开销，[then7 then5 then3]
// 7 5 3 8 6 10 9 11 
const p = new Promise((resolve) => {
    resolve()
    console.log('start')
}).then((v) => console.log('1'))

async function f() {
    await p;
    console.log('f')
}

f().then(() => console.log(3)).then(() => console.log(10))

p.then(() => console.log(7)).then(() => console.log(8)).then(() => console.log(9))
Promise.resolve()
    .then(() => console.log(4))
        .then(() => console.log(5))
            .then(() => console.log(6))
                .then(() => console.log(11))
```
```js
//await pending promise
// start [then1]
// t() 本来返回 p, p 此时还是 pending, 因为是 async function, 所以需要执行 p.then [then1 p.then]， 返回新的 pending promise p2
// p 现在还是 pending, then7 还不能被添加 [then1 p.then then4]
// 1, then1 执行结束时， p 的状态变成 fulfilled, then7 被添加 [p.then then4 then7]
// p.then 被执行，因为现在 p 的状态已经是 fulfilled, 所以添加 resolve-p2 [then4 then7 resolve-p2]
// 4 [then7 resolve-p2 then5]
// 7 [resolve-p2 then5 then8]
// resolve-p2 被执行后，p2 状态变成 fulfilled, 后续代码被添加 [then5 then8 thenf]
// 5 [then8 thenf then6]
// 8 [thenf then6 then9]
// f [then6 then9 then3]
// 6 9 3 11 10
const p = new Promise((resolve) => {
    resolve()
    console.log('start')
}).then((v) => console.log('1'))

async function t() {
    return p;
}
async function f() {
    await t();
    console.log('f')
}

f().then(() => console.log(3)).then(() => console.log(10))

p.then(() => console.log(7)).then(() => console.log(8)).then(() => console.log(9))
Promise.resolve()
    .then(() => console.log(4))
        .then(() => console.log(5))
            .then(() => console.log(6))
                .then(() => console.log(11))
```
```js
// await pending promise
// start [then5] resolved [then5 then12] 'after promised'
// f() 会返回 p, p 的状态是 pending, async function 会执行 p.then [then5 then12 p.then],返回 p2
// [then5 then12 p.then then2 thenA]
// 5 [then12 p.then then2 thenA then6]
// 12 [p.then then2 thenA then6 then13]
//  p.then 会被调用， [then2 thenA then6 then13], 因为 此时 p 的状态还是 pending, 所以不在这里添加 resolve promise 的任务 
// 2 [thenA then6 then13 then3]
// A [then6 then13 then3 thenB]
// 6 [then13 then3 thenB then7]
// 13 [then3 thenB then7 resolve-p2]， 执行then13 后，p 的状态变成 fulfilled, 添加一个 resolve-p2 的任务
// 3 [thenB then7 resolve-p2 then4]
// B [then7 resolve-p2 then4 thenC]
// 7 [resolve-p2 then4 thenC then9]
//  [then4 thenC then9 then1]， resolve-p2 后，await f() 被激活，添加 then1
// 4 C 9 1 8
async function f() {
    const p = new Promise((resolve) => {
	    resolve('')
        console.log('resolved')
    })
    .then(() => console.log(12))
    .then(() => console.log(13))
    console.log('after promised')
    return p;
}

async function test() {
    await f()
    console.log(1)
}
console.log('start')

Promise.resolve()
    .then(() => console.log(5))
        .then(() => console.log(6))
            .then(() => console.log(7))
                        .then(() => console.log(9))

test().then(() => console.log(8))
Promise.resolve()
    .then(() => console.log(2))
        .then(() => console.log(3))
            .then(() => console.log(4))

Promise.resolve()
    .then(() => console.log('A'))
        .then(() => console.log('B'))
            .then(() => console.log('C'))
```
```js
// await fulfilled promise
async function f() {
    return new Promise((resolve) => resolve(''))
}

async function test() {
    await f()
    console.log(1)
}
console.log('start')

Promise.resolve()
    .then(() => console.log(5))
        .then(() => console.log(6))
            .then(() => console.log(7))
                        .then(() => console.log(9))

test().then(() => console.log(8))
Promise.resolve()
    .then(() => console.log(2))
        .then(() => console.log(3))
            .then(() => console.log(4))
            
// [then5] start 
// f() 本来应该返回 fulfilled pormise p, 但async function 会包裹一层新的 promise p2, 状态为 pending 
// 首先会执行 p 的 then 方法 p.then
// [then5 p.then then2]
// 5 [p.then then2 then6]
// p.then 执行结束时，因为 p 已经 fulfilled, 所以只需要再 resolve-p2 就行
// [then2 then6 resolve-p2]
// 2 [then6 resolve-p2 then3]
// 6 [resolve-p2 then3 then7]
// 当 resolve-p2 后，await f() 被激活，添加then1
// [then3 then7 then1]
// 3 [then7 then1 then4]
// 7 [then1 then4 then9]
// 1 [then4 then9 then8]
// 4 9 8
```
```js
// await fulfilled promise
// start resolved 'after promised' 5 2 A 6 3 B 7 1 4 C 9 8
// start [then5] resolved 'after promised'
// f() 返回 fulfilled promise p, async function 会创建一个新的 promise p2 包裹该 p, 且会执行 p.then
// [then5 p.then then2 thenA]
// 5 [p.then then2 thenA then6]
// p.then 会执行 p.then 方法， 因为 p 此时已经 fulfilled, 因为 p2 还是 pending, 需要一个 task 来fulfill p2
// [then2 thenA then6 resolve-p2]
// 2 [thenA then6 resolve-p2 then3]
// A [then6 resolve-p2 then3 thenB]
// 6 [resolve-p2 then3 thenB then7]
// resolve-p2 会激活 await f()
// [then3 thenB then7 then1]
// 3 B 7 1 4 C 9 8
async function f() {
    const p = new Promise((resolve) => {
	    resolve('')
        console.log('resolved')
    })
    console.log('after promised')
    return p;
}

async function test() {
    await f()
    console.log(1)
}
console.log('start')

Promise.resolve()
    .then(() => console.log(5))
        .then(() => console.log(6))
            .then(() => console.log(7))
                        .then(() => console.log(9))

test().then(() => console.log(8))
Promise.resolve()
    .then(() => console.log(2))
        .then(() => console.log(3))
            .then(() => console.log(4))

Promise.resolve()
    .then(() => console.log('A'))
        .then(() => console.log('B'))
            .then(() => console.log('C'))
```
```js
// change to use then instead of await
// [then5 then10 p.then]
// 1 [then5 then10 p.then then8 then2] 
// 5 [then10 p.then then8 then2 then6]
// 10 [then8 then2 then6 resolve-p2]
// 8 2 6 [then3 then7 then11]
// 3 7 11 4 9 
async function f() {
  return new Promise((resolve) => {
    resolve('')
  }).then(() => console.log(10))
}
async function test() {
    // f() 是一个 async function, 其返回值本身是 pending promise p,async function 会 wrap 进一个新的 pending promise 里
    // 所以在执行 f() 的时候，会先添加其 then10, 然后因为 f() 返回，将 p.then 添加到任务队列，又因为 p2 本身还是 pending,所以 then11 此时还不能添加
    f().then(() => console.log(11))
    console.log(1)
}
console.log('start')
Promise.resolve()
    .then(() => console.log(5))
        .then(() => console.log(6))
            .then(() => console.log(7))
                        .then(() => console.log(9))
// test 本身返回了一个 Promise.resolve(undefined), 所以 then8 会直接添加到任务队列
test().then(() => console.log(8))
Promise.resolve()
    .then(() => console.log(2))
        .then(() => console.log(3))
            .then(() => console.log(4))
```
```js
// 如果将上一个 case 的 f() 函数变成普通函数 
// 1 [then5 then10 then8 then2]
// 5 10 8 2 6 11 3 7 4 9
function f() {
  return new Promise((resolve) => {
    resolve('')
  }).then(() => console.log(10))
}
async function test() {
    // f() 只是一个普通函数，相当于同步代码块
    f().then(() => console.log(11))
    console.log(1)
}
console.log('start')
Promise.resolve()
    .then(() => console.log(5))
        .then(() => console.log(6))
            .then(() => console.log(7))
                        .then(() => console.log(9))
// test 本身返回了一个 Promise.resolve(undefined), 所以 then8 会直接添加到任务队列
test().then(() => console.log(8))
Promise.resolve()
    .then(() => console.log(2))
        .then(() => console.log(3))
            .then(() => console.log(4))
```
```js
// 如果 test 方法也返回 promise
// f() 函数本身返回 pending promise pf, 在 f() 返回时，会返回新的 pending promise pf2，只有当 pf2 resolve 时，then11 才可以添加
// test() 里面， test 本身会返回 pending pt, 包装后返回 pending pt2 ，只有当 pt2 resolve 时，then8 才可以添加
// [then5 then10 pf.then pt.then then2]
// 5 10 [pf.then pt.then then2 then6]
// pf.then 执行结束时，pf 已经 fulfilled, 所以添加 resolve-pf2 [pt.then then2 then6 resolve-pf2]
// pt.then 执行结束时，pt 和 pt2 是 pending 状态，什么任务都不添加
// [then2 then6 resolve-pf2]
// 2 6 [then3 then7 then11], resolve-pf2 后，then11 就可以添加到微任务队列
// 3 7 11 [then4 then9 resolve-pt2]，执行then11 后，pt 就 fulfilled, 就可以添加 resolve-pt2
// 4 9 [then5 then8]
async function f() {
  return new Promise((resolve) => {
    resolve('')
  }).then(() => console.log(10))
}
async function test() {
    return f().then(() => console.log(11))
}
console.log('start')
Promise.resolve()
    .then(() => console.log(5))
        .then(() => console.log(6))
            .then(() => console.log(7))
                        .then(() => console.log(9))
test().then(() => console.log(8))
Promise.resolve()
    .then(() => console.log(2))
        .then(() => console.log(3))
            .then(() => console.log(4))
                .then(() => console.log(5))
```
```js
// 如果 test 函数直接返回 f 函数呢
// f() 函数本身返回 pending pf, async 包装后返回 pending pf2
// test() 函数本身返回 pf2, async 包装后返回 pending pt2
// [then5 then10 pf.then pf2.then then2]
// 5 10 [pf.then pf2.then then2 then6],then10 执行结束后 pf fulfilled,pf.then 就可以添加 resolve-pf2
// [pf2.then then2 then6 resolve-pf2], 执行 pf2.then 时，pf2 还是 pending 状态
// [then2 then6 resolve-pf2]
// 2 6 [then3 then7 resolve-pt2]， 当执行了 resolve-pf2 时，就可以触发 resolve-pt2 了
// 3 7 [then4 then9 then8]
// 4 9 8 5
async function f() {
  return new Promise((resolve) => {
    resolve('')
  }).then(() => console.log(10))
}
async function test() {
    return f();
}
console.log('start')
Promise.resolve()
    .then(() => console.log(5))
        .then(() => console.log(6))
            .then(() => console.log(7))
                        .then(() => console.log(9))
test().then(() => console.log(8))
Promise.resolve()
    .then(() => console.log(2))
        .then(() => console.log(3))
            .then(() => console.log(4))
                .then(() => console.log(5))
```