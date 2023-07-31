JS 里内置了很多对象
# 全局值
* Infinity
* NaN
* undefined
# 全局函数
* eval
* isFinite
* isNaN
* parseFloat
* parseInt
* decodeURI
* encodeURI
* decodeURIComponent
* encodeURIComponent
```js
// encodeURI,decodeURI,encodeURIComponent,decodeURIComponent
URI 是 uniform resoure identifier, URL 是 uniform resource locator  
URL 不仅意味着是 URI,它也同时指明了该 resource 该怎么被 access(the protocol).  
那为什么需要 encode 呢？是因为 URL 只能包含 128 ASCII set, 其它的字符都必须被 encode.  
那么 encodeURI 和 encodeURIComponent 就提供了这个功能，将一个字符进行转义。它们的区别在于对一些字符的转义是不一样的
```
| | encodeURI | encodeURIComponent|
| :---:| :---:| :---:|
| # | # | %23|
| $ | $ | %24|
| & | & | %26|
| + | + | %2B|
| , | , | %2C|
| / | / | %2F|
| : | : | %3A|
| ; | ; | %3B|
| = | = | %3D|
| ? | ? | %3F|
| @ | @ | %40|

两者都不转义的字符有 `A-Z a-z 0-9 - _ . ( ) * ! ~ '` ，encodeURI 不转义的字符还有 `@ # $ & = : + , /  ; ?`  
两者的使用场景是，如果你已经有了一个完整的 URL，并且其中有一些特殊字符，比如空格，你需要使用 encodeURI 来编码  
如果你只有 URL 的一部分，比如 query 的值，你需要把 query 里面所有的特殊字符都转义，那你需要使用 encodeURIComponent 来
# 基础对象
基础对象是其它对象的基础
* Object
* Function
* Boolean
* Symbol

# 工具对象
* Number
* Bigint
* Math
* Date
* String
* RegExp

# 可索引的集合对象
普通数组以及各种带类型的数组，比如 Int8Array， Float32Array 之类的 
# 键值集合对象
常见的就是 Map, Set 之类。  
WeakMap 也是 Map, 但是它的 key 要求必须是 object 类型，且持有的是对象的弱引用。正因如此，当该对象没有被其它地方引用时，该对象可以被正确回收。也因如此，WeakMap 的 key 是不可枚举的。如果可枚举，其结果会受 GC 影响从而变得不确定。  
WeakSet 里面也只能存储对象，且持有的也是对象的弱引用，因为 WeakSet也是不可枚举的
# 结构化数据

# 内存管理对象
WeakRef
```js
let a = {x: 1}
const weak = new WeakRef(a)// 为 a 创建一个 weakref, WeakRef 的 target 不可改，只能在 GC 的时候被置为 undefined
weak.deref()// 返回 weak 指向的 target, 此时返回的引用是强引用
```
# 异步相关抽象对象

# 反射
提供了 Reflect 和 Proxy. Reflect 是一个工具类，不是函数对象，类似于 Math.
```js
// 对一个函数进行调用, target 是想调用的函数，thisArgument 是作为 this 传入，argumentsList 是函数参数数组
// 功能和 Function.prototype.apply 一样
Reflect.apply(target, thisArgument, argumentsList)

function func(a, b) {console.log('func', a + b)}
func.apply(null, [1, 2]) // 以 null 作为 this, 调用 func(1, 2)
Reflect.apply(func, null, [1, 2]) // 以 Reflect 的形式调用 func

// 传入一个 函数对象，以及它的参数
// 可以在 runtime 创建对象
Reflect.construct(target, argumentsList) // 相当于执行 new target(...args)

// Object.defineProperty(target, propertyKey, attributes) 在失败时抛异常，而不是返回 boolean
Reflect.defineProperty(target, propertyKey, attributes)// 如果成功，返回 true, 否则返回 false
Reflect.deleteProperty(target, propertyKey) // 相当于执行 delete target[propertyKey]
Reflect.get(target, propertyKey) // target[propertyKey]
Reflect.set(target, propertyKey, value) // target[propertyKey] = value
Reflect.getPrototypeOf(target) // Object.getPrototypeOf
Reflect.setPrototypeOf(target, prototype) // Object.setPrototypeOf
Reflect.preventExtensions(target) // Object.preventExtensions()
Reflect.isExtensible(target) // Object.isExtensible()

Reflect.getOwnPropertyDescriptor(target, propertyKey) // Object.getOwnPropertyDescriptor()
Reflect.has(target, propertyKey) // 与  key in target 一样
//Objetc.keys(target) 仅仅返回 enumerable 的属性
Reflect.ownKeys(target) // 返回 target own 的所有的属性名的数组，包括 non-enumerable
```
那么问题来了，既然所有的功能我们都能通过 其它方式做到，我们为什么需要 `Reflect` 呢？
1. 所有的方法都放在同一 namespace 下面，方便使用
2. Object.defineProperty 在失败的时候会抛异常，而 Reflect.defineProperty 是返回 false
3. 和 Proxy handler 对应

# Proxy
Proxy 对象可以截取对 target 的访问，并执行一些自定义行为。  
需要注意的是，如果没有配置代理操作，那么会保留默认行为。
```js
const target = {x: 1}
const p = new Proxy(target, handler)

// 拦截 get 操作
const handler = {
    get(obj, name) {// obj 是 proxy 对象代理的对象， name 是访问的属性名
        return name in obj ? obj[name] : 'default value'
    }
}
p.x // 1
p.y // default value

// 拦截 set 操作
const handler = {
    set(obj, name, value) {
        Reflect.set(obj, name, value)
    }
}

// 拦截 in 操作， 比如 'x' in p
const handler = {
    has(obj, name) {
        return Reflect.has(obj, name)
    }
}

// 拦截 查询对象 keys/symbols 的操作
const handler = {
    ownKeys(obj, name) {
        return Reflect.ownKeys(obj, name)
    }
}
const monster = {
  _age: 111,
  [Symbol('secret')]: 'I am scared!',
  eyeCount: 4
};
const proxy = new Proxy(monster, handler)
Object.keys(proxy) // [_age, eyeCount]
Object.getOwnPropertyNames()// [_age, eyeCount]
Object.getOwnPropertySymbols()// [Symbol(secret)]
Reflect.ownKeys()// [_age, eyeCount, Symbol(secret)]

// 其它拦截操作
const handler = {
    defineProperty(obj, name, descriptor) {// 拦截 Object.defineProperty, Reflect.defineProperty
        
    },
    deleteProperty(obj, name) { // 拦截 delete 操作 和 Reflect.deleteProperty

    },
    getPrototypeOf(obj) {// 拦截 Object.getPrototypeOf, Reflect.getPrototypeOf, Object.prototype.isPrototypeOf(), instanceof

    },
    setPrototypeOf(obj, proto) {// 拦截 Object.setPrototypeOf, Reflect.setPrototypeOf

    },
    getOwnPropertyDescriptor(obj, name) {// 拦截 Object.getOwnPropertyDescriptor 和 Reflect.getOwnPropertyDescriptor

    },
    isExtensible(obj) {// 拦截 Object.isExtensible, Reflect.isExtensible

    },
    preventExtensions(obj) {//拦截 Object.preventExtensions, Reflect.preventExtensions

    }
}

```
除了这些之外，proxy 还可以拦截函数对象的调用
```js
const hander = {
    // target 是目标函数，thisArg 是被调用时的上下文
    apply(target, thisArg, argumentsList) {// 可以拦截 proxy(...args), Function.prototype.apply, Function.prototype.call, Reflect.apply

    },
    construct(target, args) { // target 需要是有[[Construct]] 方法的函数对象，可以拦截 new proxy(...args) 和 Reflect.construct(proxy, args)

    }
}
```