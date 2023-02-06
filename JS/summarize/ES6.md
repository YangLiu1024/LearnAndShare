# const & let
为了避免变量作用域提升带来的各种问题，引入了块级作用域。
* 不存在变量提升
* 块级作用域，作用于外不能访问
* 变量只能在声明后才能访问
* 不能重复声明相同变量
* 不再是全局对象的属性
* let 变量在 for 循环中，其实每一轮迭代都是新的变量


# 解构
```js
const person = {
    name: 'liu',
    age: 12,
}
const {name: lastName, age, sex='male'} = person

const fruits = ['apple', 'banana']

const [app, , orange] = fruits
```
解构可以快速的提取对象的属性，并且对变量进行赋值。  
变量还可以进行重命名，以及设置默认值。值得注意的是，默认值只有当对应的属性的值 === undefined 时才生效。

# 扩展运算符与 rest 运算符
这两种运算符可以很好的解决函数参数和数组元素长度未知情况下的编码问题。  
扩展运算符可以将数组或者类数组对象展开为值序列。
```js
const a = [1, 2, 3]
console.log(...a) // 1 2 3

const b = [...a]

const c = []
c.push(...a)

const name = {name: 'liu'}
const age = {age: 12}

const p = {...name, ...age}
```
rest 运算符作用与扩展运算符相反.  
需要注意的是， rest 只能作用域最后一个变量，表示将剩下的所有值序列都赋给该变量
```js
const arr = [1, 2, 3, 4]
// 第一个元素赋给 first, 剩下的所有赋给 rest
const [first, ...rest] = arr

const {name, ...rest} = person
```
# 模板字符串
模板字符串可以 format 表达式，将表达式的值转换为字符串，并且保留字符串里的空白，缩进和换行符。
# 箭头函数
箭头函数的 this 指向自己上一级作用域里的 this, 普通 function 里的 this 指向函数调用者。  
需要注意的是，箭头函数里的 this 在定义箭头函数的时候，就已经确定了。并不会受 apply, call 的影响。
```js
const person = {
    name: 'liu',
    sayHi: function() {
        setTimeout(() => console.log(this.name), 1000)
    }
}
// setTimeout 是箭头函数，所以 它的 this 绑定的是上级作用域里 的 this
// 而箭头函数的上级作用域是 sayHi 函数，该函数是被 person 调用，所以 this 绑定在 person 上
person.sayHi() // liu

const person2 = {
    name: 'liu',
    sayHi: () => {
        setTimeout(() => console.log(this.name), 1000)
    }
}
// setTimeout 是箭头函数，所以 它的 this 绑定的是上级作用域里 的 this
// 而 sayHi 也是箭头函数，所以 它的 this 绑定的是上级作用域里 的 this
// person2 本身不构成作用域，所以 this 绑定在 window 对象上
person2.sayHi()
```
## 箭头函数不适合的场景
* 不适合作为对象的函数 -> 因为箭头函数的 this 会绑定到父作用域，而不是函数调用者本身
* 没有 prototype 属性
* 不能作为构造函数
* 不适合在原型对象上定义箭头函数

# ES6 对对象的扩展
## 属性简写
直接将变量写入对象，而不需要指定 key-value
```js
const name = 'liu'
const p = {
    name,// p 具有 name 属性，且值为 liu
    fullName() {// 省略 关键字 function
        return this.name
    },
}
```
## 属性遍历
对象属性遍历有以下几种方法
* for key in obj -> 会拿到所有可 enumerable 的属性，包括 own 的以及原型链上的属性，不包含 symbol 属性
* Object.keys(obj) -> 拿到对象 own 的 且 enumerable 的 属性， 不包含 symbol 属性
* Object.getOwnPropertyNames(obj) -> 拿到对象 own 的属性， 不包含 symbol 属性
* Object.getOwnPropertySymbols(obl) -> 拿到对象 own 的 symbol 属性
* Relect.ownKeys(obj) -> 拿到对象 own 的属性，包括 symbol 属性

## Object.assign
assign 方法可以将多个 source 对象得属性复制到 target 对象里
```js
// 需要注意得是，source own 的 可枚举属性(包括 symbol 属性) 会被复制到 target 对象
Object.assign(target, ...source)
```
## Symbol 类型
Symbol 类型表示的是一个独一无二的值，类似于唯一标志性的 ID, typeof Symbol() 返回的是 'symbol'.  
Symbol 函数可以接收一个字符串作为参数，主要是为了对 symbol 进行解释，以区分不同的 symbol, 但任何通过 Symbol 函数创建的 symbol 对象都是唯一的，即使传递了相同的字符串。  
Symbol 函数不是构造函数，不能使用 new 操作符。  
Symbol 对象也不能参与类型运算。  
当想使用同一个 Symbol 对象时，可以使用 Symbol.for(name), 如果存在具有相同名字的 symbol, 则返回该 symbol, 如果没有，则创建新的symbol. Symbol.for 与 Symbol() 的区别在于，Symbol.for 创建的symbol 会在全局环境中登记，以便下次 Symbol.for 查询，但是 Symbol() 就不会进行登记。
### 用作对象属性名
为了避免属性名被覆盖，则使用 symbol 对象作为属性名。  
需要注意的是，不能通过<code>.</code> 来访问 symbol 属性， symbol 是一个变量，必须通过中括号来访问。
```js
// 通常需要将 Symbol 定义为一个变量，方便之后读取
// 否则只能通过 Object.getOwnPropertySymbols() 来返回 symbols
const symbol = Symbol();
// 第一种给对象添加 symbol 属性的方法
const obj = {
    [symbol]: 'hello'
}
// 第二种
obj[symbol] = 'world'

//第三种
Object.defineProperty(obj, symbol, {
    value: 'java',
    enumerable: false
})
```
### Symbol 属性的访问
* 预先定义好 Symbol 变量，然后将该变量用作属性名，通过该 Symbol 变量即可访问 Symbol 属性值
* 通过 Object.getOwnPropertySymbols(obj) 返回 obj own 的 symbol keys, 继而访问对象 Symbol 属性值
* 通过 Reflect.ownKeys(obj) 返回对象 own 的 keys, 包括 symbol 属性，继而访问对象的 Symbol 属性值

## Set & Map
Set 方法有 add, delete, has, clear, 属性有 size.  
可以使用数组和类数组对象构建 Set 对象，也可以使用 Array.from(set) 或者 ...set 转换 Set 为数组.  
### 对 Set 对象的遍历
* forEach((item,index) => {}) -> 对于 Set 而言，没有索引的概念，本质上是索引和值相同的集合，所以 forEach 里的 item 和 index 会是相同的值
* keys() -> 返回所有 keys 的遍历器
* values() -> 返回所有 values 的遍历器
* entries() -> 返回所有键值对的遍历器, 每个键值对是 [key, value] 的 tuple

拿到遍历器后，即可以通过 for...of... 来遍历每一项
### Map 与对象字面量
Map 与对象字面量很类似，不同点在于，对于对象字面量，key 只能是字符串，如果不是，也会隐式的转换为字符串来作为 key 值。  
而对于 Map 对象，key 可以是任意类型。
```js
const div = document.getElementByTagName('div')
const data[div] = 'hello world'
// div 不是字符串变量，使用对象字面量存储时，会转换为字符串作为 key
console.log(data) // {[object HTMLDivElement]: 'hello world'}

const m = new Map()
m.set(div, 'hello world')
```
Map 方法有 set, get, has, delete, clear, 属性有 size.  
Map 对象转换为数组可以直接使用扩展运算符，得到键值对的数组
### Map 对象的遍历
同样的，对 map 对象的遍历有 4 种方法
* forEach((value, key) => {})
* keys()
* values()
* entries()

## Proxy
Proxy 就是对对象的访问增加一层拦截，在拦截中，可以增加自定义的行为。
```js
// proxy 的基本语法
const proxy = new Proxy(target, handler);
```
通过 Proxy 构造函数，可以构建代理对象 proxy. 任何对 proxy 实例属性的访问，都会自动转发到 target 对象上，我们可以针对访问的行为配置自定义的 handler 对象。因此外界通过 proxy 访问 target 对象的属性时，都会执行 handler 对象自定义的拦截操作。  
### Proxy handler 支持的拦截操作
* get(target, prop, receiver) -> 对 target 对象属性 prop 的读取, receiver 是配置对象 handler
* set(target, prop, value, receiver) -> 对 target 对象属性 prop 值的写入
* has(target, prop) -> 对 hasProprty 的拦截, 且只对 in 操作符有效, 比如 if ('name' in person) {}， 对于 for...in... 则没有作用
* deleteProperty(target, prop) -> 拦截 delete proxy[prop] 的操作
* ownKeys(target) -> 拦截对 proxy 对象属性名的遍历
* getOwnPropertyDescriptor(target, prop) -> 拦截 Object.getOwnPropertyDescriptor(proxy, prop)
* defineProperty(target, prop, desc) -> 拦截 Object.defineProperty(proxy, prop, desc)
* apply(target, obj, args) -> target 是函数时，可以定义该 handler, 在 proxy 直接运行，或者通过 proxy.apply, proxy.call 运行时进行拦截
* others...

### Proxy 的基本使用
1. 读取不存在的属性  
有的场景下，如果访问某个对象不存在的属性，我们不想返回 undefined，而是直接抛出异常，就可以通过Proxy 来实现
2. 对于数组对象，还可以通过设置代理来支持负数的索引查询
3. 禁止访问特定属性
4. 拦截对属性的赋值，比如检查将要设置的值是否符合要求
5. 函数的拦截

### Proxy 属性读取的限制
一般来说，读取 proxy 对象属性应该返回 target 对象对应属性的真实值，但有的时候可能会返回和真实值不同的值，这个时候就有一定的约束。
* 在 target 上，属性需要是 writable
* 如果不是 writable，那必须是 configurable
  
否则会抛出异常

## Reflect
Reflect 包含所有 Proxy handler 支持配置的所有方法，与 Proxy 不同的是，Reflect 本身并不是构造函数，而是直接提供静态函数以供调用。
* Reflect.apply(target, thisArg, args) -> 以 thisArg 作为 this, args 作为参数数组来调用函数 target
* Reflect.construct(target, args) -> 以 target 作为构造函数，args 作为参数调用， 等同于 new target(...args)
* Reflect.defineProperty(target, propName, desc) -> 与 Object.defineProperty 基本相同, 区别在于 Object.defineProperty 在失败时会抛出异常，但是 Reflect.defineProperty 会 返回 false
* Reflect.deleteProperty(target, propName) -> 等同于执行 delete target[propName], 只是将操作符调用替换为了函数调用
* Reflect.getOwnPropertyDescriptor(target, propName) -> 等同于执行 Object.getOwnPropertyDescriptor
* Reflect.getPrototypeOf(target) -> 等同于执行 Object.getPrototypeOf(target)
* Reflect.has(target, propName) -> 等价于执行 propName in target
* Reflect.isExtensible(target) -> 等价于执行 Object.isExtensible(target)
* Reflect.ownKeys(target) -> 获取对象所有的 own 的属性，包括 symbol 属性，等价于 Object.getOwnPropertyNames 与 Object.getOwnPropertySymbols 之和
* Reflect.preventExtensions(target) -> 让对象不可扩展，等价于 Object.preventExtensions()
* Reflect.get(target, propName, receiver) -> 获取对象属性值，等同于 target[propName], receiver 是函数中 this 的 绑定值
* Reflect.set(target, propName, value, receiver) -> 执行 属性的 setter，返回 true or false, true 表示执行成功
* Reflect.setPrototypeOf(target, proto) -> 等价于执行 Object.setPrototypeOf(target, proto)

get, set 方法之所以有 *receiver* 参数，是因为target 上的 propName 可能是 getter/setter, 那么进行对应的 get/set 时就可能需要绑定 this. 如果不指定 receiver，则默认this 绑定为 target.  
### Reflect & Proxy
ES6 在设计之初，就将 Reflect 和 Proxy 对象绑定在一起了。
```js
const person = {
    name: 'liu'
}

const proxy = new Proxy(person, {
    get(target, propName, receiver) {
        return Reflect.get(target, propName, receiver)
    }
})
```
#### 观察者模式
```js
void observe<T>(target: T, keys, handlers);
```