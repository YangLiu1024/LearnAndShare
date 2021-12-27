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
ES6 提供了 class 语法，支持使用 constructor, super, static, get, set 等关键字  
对于getter/setter
```js
//在通过 Object.defineProperty(obj, prop, config) 定义对象的属性时，可以对该属性进行配置， 包括 value/writable/enumerable/configurable/get/set, get/set 和 value/writable 不可同时存在.value 默认值为 undefined, writable/enumerable/configurable 默认值为 false.    
//在对象初始化时，也可以直接定义属性的 getter/setter, 该属性一般不能与其它属性同名，如果同名，只有最后作出定义的属性有效。getter/setter 默认值为 undefined, enumerable/configurable 默认值为 true.  
//对象的属性都有自己的 descriptor, 可以通过 Object.getOwnPropertyDescriptor(obj, prop)
let a = {y: "1"}
Object.defineProperty(a, "x", {
    value: "1",
    writable: false//x 不可写
    //或者定义 get/set
    get() {
        return this.y//如果一个属性定义 get/set, 那么它一般来说是作为计算属性使用，即依赖于其它的属性
    },

    set(v) {
        this.y = v
    }
})

let o = {get z() {return "1"}}
console.log(Object.getOwnPropertyDescriptor(o, z))//{get(){...}, set:undefined, enumerable: true, configuable: true}
```
在 class 里， get/set 用来定义实例对象的属性
```js
class Animal {
    age = 0//ES7里支持类内直接初始化属性，ES6只能在构造函数体内初始化
    static count = 100
    constructor(name) {
        this.name = name;
    }
    sayHi() {
        return `My name is ${this.name}`;
    }

    get name() {//任何访问 Animal 实例对象的 name 属性，返回都是 Jack
        return 'Jack'
    }

    set name(value) {//任何对 Animal 实例对象的 name属性进行赋值的操作都只会打印
        console.log('setter:', value)
    }
    //static 语法其实就相当于把该方法定义在了 Animal 函数对象本身上面
    //普通函数或者属性，其实就是添加到了Animal 函数对象 prototype 属性上
    static isAnamal(o) {
        return o instanceof Animal
    }
}
//类的继承使用 extends
class Cat extends Animal {
  constructor(name) {
    super(name); // 调用父类的 constructor(name)
    console.log(this.name);
  }
  sayHi() {
    return 'Meow, ' + super.sayHi(); // 调用父类的 sayHi()
  }
}
```
上述的例子都还停留在 JS, 在 TS 里，对 class 的定义有增强，它允许对属性添加 public/private/protected 修饰符，它们的作用和 Java 里的修饰符是一样的。  
```js
//animal.ts
class Animal {
  private name;
  public constructor(name) {
    this.name = name;
  }
}

let a = new Animal('Jack');
console.log(a.name);//TS 报错，因为 a.name是 private, 不应该被外部直接调用
a.name = 'Tom';//TS 报错
```
需要注意的是，这里的错误只是 TS 报出的编译期错误，编译后的结果也会删掉修饰符，在运行时，也并不会出错。  
TS 只是在编译器帮助开发者维护类属性的访问权限控制，就和 Java 一样，只是在编译后，仍然可以运行。  
除了访问权限修饰符， TS 还支持使用 readonly 来修饰属性。  
```js
class Animal {
    public readonly name: string//如果 readonly 和其它修饰符一起使用，需要写在后面
    constructor(name: string) {
        this.name = name
    }
}

let a = new Animal("Tom")
a.name = "Kitty"//报错
```
TS 还支持抽象类，即使用 abstract 来修饰 class 以及 class 内的 抽象方法  
```js
abstract class Animal {
  public name;
  public constructor(name) {
    this.name = name;
  }
  public abstract sayHi();
}

let a = new Animal('Jack');//报错，不应该创建抽象类实例
```
和 Java 里概念一样，抽象类不允许实例化，且继承类必须实现其抽象方法。  
当然，这些限制都是 TS 添加的编译器限制，在运行时，实际上并没有这些限制。

# 类与接口
TS 里的接口和 Java 里的接口非常相似，都是对对象行为的抽象。  
类可以实现一个或多个接口，接口也可以继承另一个接口。 值得注意的是，在 TS 里，接口是可以继承类的。  
```js
class Point {
    x: number;
    y: number;
    constructor(x: number, y: number) {
        this.x = x;
        this.y = y;
    }
}
//上面的代码，在 TS 里，除了创建一个构造函数 Point 外，还会声明一个 Point 类型, 相当于
Function Point(x, y) {...}
type Point = {x: number, y: number}//这个类型会包含类的实例属性和实例方法，其它如构造函数，静态方法，属性是不会被包含的

//在这里，Point 是作为类型而不是类来使用的，这个类型具有x,y 两个属性
//所以本质上，其实还是接口继承于接口
interface Point3d extends Point {
    z: number;
}

let point3d: Point3d = {x: 1, y: 2, z: 3};
```
# 泛型
泛型是指，在定义接口，函数或者类的时候，不预先指定具体类型，而在使用的时候再指定类型的一种特性。  
```js
function createArray<T>(length: number, value: T): Array<T> {
    let result: T[] = [];
    for (let i = 0; i < length; i++) {
        result[i] = value;
    }
    return result;
}

createArray<string>(3, 'x');//这里的 string 也可以不写，因为类型推导可以推导出来

//当然也可以指定多个泛型参数
function swap<T, U>(tuple: [T, U]): [U, T] {
    return [tuple[1], tuple[0]];
}

swap([7, 'seven'])
```
## 泛型约束
使用泛型变量时，因为不知道具体类型，不能随意操作它的属性或方法，我们可以对泛型进行约束让它至少满足要求。
```js
interface Lengthwise {
    length: number;
}

function loggingIdentity<T extends Lengthwise>(arg: T): T {
    console.log(arg.length);
    return arg;
}
```
需要注意的是，TS 里类型的的继承并不是说需要我们必须声明 A extends B, 才能说 A 继承了 B, 只要 A 包含 B 里要求的所有的属性，那么就可以说A 继承了 B.  
多个泛型参数之间也可以互相约束
```js
//要求 T 继承 U, 保证 U 上不会出现 T 没有的属性
function copyFields<T extends U, U>(target: T, source: U): T {
    for (let id in source) {
        (target as U)[id] = source[id];
    }
    return target;
}

let x = { a: 1, b: 2, c: 3, d: 4 };

copyFields(x, { b: 10, d: 20 });
```
上面的示例说明了怎么在函数定义里使用泛型，那么在函数类型声明里，该怎么使用泛型呢
```js
type CreateArray = <T>(length: number, value:T) =>  Array<T>

let ca: CreateArray = function<T>(len: number, v: T): Array<T> {
    let res: T[]= []
    for (let i = 0; i < len; i++) {
        res[i] = v;
    }
    return res
}
```
当然也还可以将泛型应用到类的定义里
```js
//我们还可以为泛型参数指定默认值，当没有显示指定，也不能通过推导判断时，使用默认参数
class MyArray<T = string> {
    values: T[] = [];
    get length() {
        return this.values.length
    }

    set length(len: number) {
        
    }
    add(v: T) {
        this.values.push(v)
    }
}
```