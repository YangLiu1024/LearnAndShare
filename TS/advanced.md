# Advanced Type Script
TS 进阶内容
# 字符串字面量类型
字符串字面量类型用来约束取值只能是某几个字符串中的一个
```js
type HttpMethods = 'GET' | 'POST' | 'DELETE'
```
# 元组
数组合并了相同类型的对象，而元组(tuple) 合并了不同类型的对象。  
```js
let tom: [string, number];//声明 tom 是一个元组，应该只包含两个元素，第一个元素是字符串，第二个是 number
tom[0] = 'tom'
tom[1] = 10
//当对元组类型变量进行赋值时，必须同时提供所有元组类型中指定的项，否则 ts 会报错
tom = ['tom', 10]
//即使元组类型声明的时候，要求只有两个元素，但是仍然可以添加类型符合的元素
tom.push('male')
tom.push(true)//TS 会报错，指出类型不匹配，但是JS 里仍然会执行成功
```
# 枚举
```js
//index.ts
enum Days {Sun, Mon, Tue, Wed, Thu, Fri, Sat};
//编译出来的 index.js, 枚举名和枚举值可以互相映射，即 Days[0] = "Sun", Days["Sun"] = 0
var Days;
(function (Days) {
    Days[Days["Sun"] = 0] = "Sun";
    Days[Days["Mon"] = 1] = "Mon";
    Days[Days["Tue"] = 2] = "Tue";
    Days[Days["Wed"] = 3] = "Wed";
    Days[Days["Thu"] = 4] = "Thu";
    Days[Days["Fri"] = 5] = "Fri";
    Days[Days["Sat"] = 6] = "Sat";
})(Days || (Days = {}));
//对应的声明文件 index.d.ts
declare enum Days {
    Sun = 0,
    Mon = 1,
    Tue = 2,
    Wed = 3,
    Thu = 4,
    Fri = 5,
    Sat = 6
}
```
## 常数枚举
常数枚举是使用 const enum 定义的枚举类型
```js
//direction.ts
const enum Directions {
    Up,
    Down,
    Left,
    Right
}

let directions = [Directions.Up, Directions.Down, Directions.Left, Directions.Right];
//direction.js
"use strict";
let directions = [0 /* Up */, 1 /* Down */, 2 /* Left */, 3 /* Right */];
```
常数枚举和普通枚举的区别是，它在编译阶段就被删除，并且不能包含计算成员，即成员在初始化时，只能是常量，不能依靠运行时计算。  
## 外部枚举
外部枚举是使用 declare enum 定义的枚举类型，declare 定义的类型只会用于编译时的检查，编译结果中会被删除。
# 类
在传统 JS 里，通过构造函数和原型链来实现类以及类的继承
```js
//JS 其实是基于对象/原型而不是类的语言，任何对象都可以在创建或者运行时，指定自身的属性，包括将另一个对象作为自己的原型，从而继承(共享)该对象的属性。  
//JS 里并没有关于 class 的定义，ES6 引入的 class 也只是已有的原型继承方式的语法糖而已。  
//JS 将函数作为构造函数使用，然后通过 new 构造函数来创建对象。这些对象初始都有相同的原型链。

//JS 里每一个对象，都有一个私有属性 __proto__, 该属性会指向它的原型(也是一个对象, 这个对象的 __proto__ 又会指向它自己的原型对象)，直到对象的 __proto__ = null
//当访问 JS 里对象的属性时，编译器首先查找该对象自己 own 的 properties 里是否包含该属性，如果包含，则返回，如果不包含，则查找对象的 __proto__ 属性，重复这一步骤，直到找到属性或者对象的 __proto__ = null  
//那么对象的 __proto__ 是什么时候初始化的呢？在通过 new 构造函数的时候。  
//JS 里，每一个函数对象，都有 prototype 属性，该属性是一个对象，包含 constructor 和 __proto__ 属性。函数对象本身也有 __proto__, 指向 Function.prototype  
//如果是一般的对象创建，那么该对象会直接继承 Object 类，对象的__proto__ 会指向 Object.prototype, 如果是通过 new 构造函数，该构造函数首先创建一个 plain JS 对象，然后将该对象的 __proto__ 指向构造函数的 prototype 属性, 然后将该对象绑定为该构造函数的this, 继续执行函数体。
//这就可以看出，所有通过相同的构造函数创建的对象，它们的 __proto__ 都会指向构造函数的 prototype 属性，这就实现了继承。
//但一般，不要直接访问对象的私有属性，可以通过 Object.getPrototypeOf(obj) 来获取 obj 的原型对象
//如果通过 Object.create(obj) 创建对象，那么会返回一个新的对象，该对象的 __proto__ 指向了 obj
//箭头函数没有 prototype 属性，只有 __proto__, 所以箭头函数不能作为构造函数使用
//我们平时使用的 instanceof 其实也只是check 对象的原型链中是否包含指定构造函数的 prototype


//Point 是一个普通函数，拥有 prototype 属性
//当它被当作构造函数调用时，即通过 new Point 调用时，会构造一个对象并返回
//当然它也可以作为一个普通函数被调用，比如 Point(1, 2), 这个时候 this 会绑定到 全局对象 window, 在 strict 模式下会报错，也可以通过 Point.call({}, 2, 3) 来调用，传入的对象会作为 this 被调用
function Point(x,y) {
    this.x = x
    this.y = y
}
//在函数的 prototype 属性上添加一个 distance 属性
Point.prototype.distance = function() {
    return Math.sqrt(this.x * this.x + this.y * this.y)
}
//创建一个 p 对象，该对象有属性 x, y, 且 __proto__ 指向 Point.prototype
const p = new Point(1, 2)
//定义一个新的函数
function MyPoint(z) {
    this.z = z
}
//将该函数的 prototype 指向对象 p
MyPoint.prototype = p
//MyPoint.prototype = Object.create(Point.prototype)//并不共享 x, y 属性

//这个时候，mp 的 __proto__ 指向了 MyPoint.prototype, 即 p 上面
//那么 mp 就可以使用 p 的所有属性，相当于 mp 继承了 p
//mp.z 访问自己 own 的属性，mp.x 访问 mp.__proto__ 即 p 的属性，mp.distance 访问 p.__proto__ 即 Point.prorotype 的属性，Point.prototype.__proto__ 指向了 Object.prototype, Object.prototype.__proto__ = null
let mp = new MyPoint(3)

```

