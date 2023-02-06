# 引用
引用主要是区别于基础数据类型，描述的是具有属性和方法的对象。  
引用类型变量的赋值传递的是内存地址。  
引用类型变量的比较，是比较内存地址。
## 原型链
JS 里的对象都有 __proto__ 属性，该属性会指向创建该对象的构造函数的 prototype 属性, 这个就是原型链。
```js
// Person 函数就可以作为构造函数被调用， 即 new Person()
// new 操作符会首先创建一个 plain object, 即 {}, 然后将该对象的 __proto__ 指向构造函数的 prototype
// 然后将该对象作为 this 继续执行初始化语句，如果最后没有显式 return 语句，则返回 this
function Person() {
    this.name = '';
    this.age = 0;
}
// JS 里的属性访问依赖于对象的原型链，即如果在对象的 own 属性集里没有找到想要的 key, 则继续遍历对象的 __proto__ 属性，__proto__ 属性也是一个对象
// 然后在该 __proto__ 对象上继续查找，直到找到 key, 或者对象的 __proto__ 为 null 为止
// 以上述 Person 为例, const p = new Person(), 当执行 p.sex 时，因为 p 不包含该属性，则查找 p.__proto__
//p.__proto__ 指向了 Person.prototype(prototype 是函数对象的属性)
//所以如果给 Person.prototype 对象上添加属性，相当于所有 new Person 对象都可以访问到该属性，类似于其它语言里的继承。
//上例里 Person.prototype 并没有添加什么属性，只是一个拥有 constructor 属性的对象，所以依然找不到 p.sex
//那么继续查找 Person.prototype.__proto__, 因为 Person.prototype 是一个普通对象，它的 __proto__ 则指向了 Object 函数的 prototype, 继续往上查找，则返回 null
```
## new 操作符
```js
// Cat 是一个函数，可以像任何其它函数一样正常使用
// Cat() -> 因为没有对象调用 Cat(), this 会自动绑定在 window 上面
// Cat.apply({}, [args]) -> 通过任意对象调用函数 Cat, 对象会绑定为 this
// Cat.call({}, ...) -> 通过任意参数以及参数调用函数 Cat, 对象会绑定为 this
// Cat.bind({}) -> 返回一个新的函数，该函数将 this 绑定在 对象上
function Cat(name) {
    this.name = name
}

const cat = new Cat()
// new 操作符的作用其实有三个
// 上述代码等价于
// const cat = {} -> 创建空对象
// cat.__proto__ = Cat.prototype -> 继承原型链
// Cat.call(cat) -> 执行函数体，并且 this 绑定到空对象
```
## Object 对象实例方法
### hasOwnProperty(name)
该方法检测对象是否 拥有指定属性，该方法不会检查原型链上的属性，仅对象本身。
### propertyIsEnumable(name)
检测属性是否为实例属性并且可枚举
### isPrototypeOf(obj)
检测该对象是否为指定对象的 __proto__ 
## Object 静态方法
### Object.create(prototype, propertyDescriptor)
创建一个以 prototype 对原型的对象，可以为 null, 不能是 undefined.  
propertyDescriptor 是对象的属性定义，可以配置每个属性的 value, writable, enumable, configurable, value 默认是 undefined, 其它配置项默认都是 false
### Object.defineProperties(obj, propertyDescriptor)
定义对象的属性
### Object.getOwnPropertyNames()
返回对象拥有的所有属性名，包括不可枚举的属性
### Object.keys(obj)
返回对象可枚举的实例属性，不包括原型链属性

## Array
### 判断对象是否为数组
```js
Array.isArray(obj)
```
## Date
