# Iterator
迭代器，当一个对象实现了属性 *[Symbol.iterator]*, 那么该对象就是可迭代的。可以使用 *spread* 语法，也可以使用 *for ... in ...*， *for ... of ...* 来迭代该对象.  
```js
// 在 TS 里表示可迭代对象， 该对象需要实现 Symbol.iterator 属性，返回一个 Iterator 对象
interface Iterable<T> {
    [Symbol.iterator](): Iterator<T>;
}
// 当对象是可迭代的, 那么就可以应用于
// 1. for of, for in
// 2. spread into array
// 3. spread into parameter list
// 4. be used in API thats accepts Iterable, such as Array.from, Map, Set

// 可迭代对象在进行迭代时，会返回一个 Iterator，该对象包含三个方法 *next*, *return(optional)*, *throw(optional)*,
// 泛型里 TYield 表示每次迭代，但是还没有结束时， next() 返回的 value 类型
// TReturn 表示在迭代结束时(在调用 最后一次next() 或者 直接调用return()) 返回的 value 的类型
// TNext 表示在 调用 next() 时可能传入的参数类型
interface Iterator<TYield, TReturn = any, TNext = undefined> {
    // NOTE: 'next' is defined using a tuple to ensure we report the correct assignability errors in all places.
    next(...args: [] | [TNext]): IteratorResult<TYield, TReturn>;
    return?(value?: TReturn): IteratorResult<TYield, TReturn>;
    throw?(e?: any): IteratorResult<TYield, TReturn>;
}

interface IteratorYieldResult<TYield> {
    done?: false;
    value: TYield;
}

interface IteratorReturnResult<TReturn> {
    done: true;
    value: TReturn;
}

type IteratorResult<T, TReturn = any> = IteratorYieldResult<T> | IteratorReturnResult<TReturn>;
```
值得注意的是，通常可迭代对象返回的迭代器，本身也需要是可迭代的. 比如 Array, 它的迭代器函数返回的就是 *IterableIterator<T>*.  
这是为了方便使用，即对象以及对象返回的迭代器，都可以被迭代，效果是一样的
```js
interface IterableIterator<T> extends Iterator<T> {
    [Symbol.iterator](): IterableIterator<T>;
}
```
举例来说， range 对象实现了 [Symbol.iterator] 之后，就是可迭代的了
```js
// range 现在只是一个普通对象，还不是可迭代的
let range = {
  from: 1,
  to: 5
};

// 实现了 [Symbol.iterator] 之后，就是可迭代的了
// 1. for..of 调用首先会调用这个：
range[Symbol.iterator] = function() {

  // ……它返回迭代器对象（iterator object）：
  // 2. 接下来，for..of 仅与下面的迭代器对象一起工作，要求它提供下一个值
  return {
    current: this.from,
    last: this.to,

    // 3. next() 在 for..of 的每一轮循环迭代中被调用
    next() {
      // 4. 它将会返回 {done:.., value :...} 格式的对象
      if (this.current <= this.last) {
        return { done: false, value: this.current++ };
      } else {
        return { done: true };
      }
    }
  };
};
```
但如果我们显示调用迭代器函数，返回的迭代器本身并不是可迭代的
```js
// 这里的 iter 对象本身不是可迭代的
const iter = range[Symbol.iterator]()
// 那么 for (const value of iter) 就会报错，因为 iter 本身并不可迭代
```
为了方便使用，通常让迭代器函数返回的迭代器本身也是可迭代的. 以数组为例
```js
interface Array<T> {

    [Symbol.iterator](): IterableIterator<T>;

    entries(): IterableIterator<[number, T]>;

    keys(): IterableIterator<number>;

    values(): IterableIterator<T>;
}
```
这里还有一个问题，为什么 迭代器函数需要返回一个新的对象，而不是让对象本身成为一个迭代器对象，比如
```js
let range = {
  from: 1,
  to: 5,

  [Symbol.iterator]() {
    this.current = this.from;
    return this;
  },

  next() {
    if (this.current <= this.to) {
      return { done: false, value: this.current++ };
    } else {
      return { done: true };
    }
  }
};
```
这里 range 对象的迭代器函数返回本身，那么 *range[Symbol.iterator]()* 返回的就是 *range* 本身了，如果现在同时在该对象上迭代，那么这两次迭代将共享迭代状态。虽然在 JS 环境里，很难出现同时在同一个对象上迭代的场景。  

## 类数组对象和可迭代对象
* 类数组对象 => 该对象具有 索引和 length 属性的对象
* 可迭代对象 => 实现了 [Symbol.iterator] 的对象

类数组对象和可迭代对象 都不是数组，它们没有 push 等数组方法。但如果我们想像数组一样操作它们，该怎么处理呢？可以使用 *Array.from(可迭代对象 | 类数组对象)*
```js
// Array.from(obj, mapFn, thisArg)
const arr = Array.from(range)
```

# Async Iterable
异步迭代，表示迭代器的值是一个 promise。普通迭代器的值是任意的。它的使用方法
* for await (const v of iter)
* spread(not allowed) => 异步迭代器不支持 spread 语法


实现异步迭代，需要实现 [Symbol.asyncIterator], 它的 next() 方法返回的是 promise. 至于它的类型定义，和 Iterator 很类似，只是返回值指定为 Promise
```ts
interface AsyncIterator<T, TReturn = any, TNext = undefined> {
    // NOTE: 'next' is defined using a tuple to ensure we report the correct assignability errors in all places.
    next(...args: [] | [TNext]): Promise<IteratorResult<T, TReturn>>;
    return?(value?: TReturn | PromiseLike<TReturn>): Promise<IteratorResult<T, TReturn>>;
    throw?(e?: any): Promise<IteratorResult<T, TReturn>>;
}
```

# Generator
大多数时候，当我们想创建一个可迭代对象时，generator 会比 iterator 方便一些。Generator 本身就是继承自 Iterator 的
```js
interface Generator<T = unknown, TReturn = any, TNext = unknown> extends Iterator<T, TReturn, TNext> {
    next(...args: [] | [TNext]): IteratorResult<T, TReturn>;
    return(value: TReturn): IteratorResult<T, TReturn>;
    throw(e: any): IteratorResult<T, TReturn>;
    [Symbol.iterator](): Generator<T, TReturn, TNext>;
}
```
生成器函数的声明方式如下所示
```js
function* generateSequence() {
    yield 1
    yield 2
}
```
*generateSequence()* 会返回一个 Generator, 可以对其进行迭代。 通常，[Symbol.iterator] 的实现就是通过 Generator, 这样会使代码更简洁
```js
let range = {
  from: 1,
  to: 5,

  *[Symbol.iterator]() { // [Symbol.iterator]: function*() 的一种简写
    for(let value = this.from; value <= this.to; value++) {
      yield value;
    }
  }
};
```
# Async Generator
异步生成器也是类似的，只是规定了返回值是 promise
```js
interface AsyncGenerator<T = unknown, TReturn = any, TNext = unknown> extends AsyncIterator<T, TReturn, TNext> {
    // NOTE: 'next' is defined using a tuple to ensure we report the correct assignability errors in all places.
    next(...args: [] | [TNext]): Promise<IteratorResult<T, TReturn>>;
    return(value: TReturn | PromiseLike<TReturn>): Promise<IteratorResult<T, TReturn>>;
    throw(e: any): Promise<IteratorResult<T, TReturn>>;
    [Symbol.asyncIterator](): AsyncGenerator<T, TReturn, TNext>;
}
```

# Sample case
现在服务器在响应数据请求时，大概率都会对数据进行分页，每一个只返回一页的数据，然后在返回的 data 里添加一个指向下一页的链接。  
这样也就保证了数据不会一次性传输太多
```js
// 比如 Github 在查询 repo commits 的时候，每次会返回 30 个 commits 的数据
// 然后 在 headers 里，添加一个 Link, 用以表示指向下一页以及最后一页的 commits 的 链接
// 比如 <https://api.github.com/repositories/201714974/commits?page=2>; rel="next", <https://api.github.com/repositories/201714974/commits?page=8>; rel="last"
async function* fetchCommits(repo) {
  let url = `https://api.github.com/repos/${repo}/commits`;

  while (url) {
    const response = await fetch(url, { // (1)
      headers: {'User-Agent': 'Our script'}, // github 需要任意的 user-agent header
    });

    const body = await response.json(); // (2) 响应的是 JSON（array of commits）

    // (3) 前往下一页的 URL 在 header 中，提取它
    let nextPage = response.headers.get('Link').match(/<(.*?)>; rel="next"/);
    nextPage = nextPage?.[1];

    url = nextPage;

    for(let commit of body) { // (4) 一个接一个地 yield commit，直到最后一页
      yield commit;
    }
  }
}
```
