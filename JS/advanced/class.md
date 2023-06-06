`class` 是ES5 支持的一种创建类的语法糖，它其实也是基于 Function 和 prototype 的。但也有一些语法和语义是函数声明不支持的。  
它们的区别在于，function 声明会自动 hoisting, 但是 class 声明并不会。而且 class 里的代码是在 `strict mode` 下执行的
```js
class Rect {
    constructor() {}
}

//unnamed
let Rect = class {
    constructor() {}
}
//Rectangle will be undefined, should use Rect
let Rect = class Rectangle {
    constructor() {}
}
```
# constructor
一个 class 只能最多有一个 *constructor*, constructor 是用来创建和初始化通过该 class 创建的对象的。 
在 constructor 里可以通过 `super` 直接调用父类构造方法。`super` 必须在 `this` 之前被调用.  
js 里 constructor 没有什么限制，和 function 一样，可以有返回值。即通过 return 返回一个对象，而不是返回 this
```js
class Rectangle {
  constructor(height, width) {
    this.height = height;
    this.width = width;
  }
  // Getter
  get area() {
    return this.calcArea();
  }
  // Method
  calcArea() {
    return this.height * this.width;
  }

  *sides() {
      yield this.height
      yield this.width
  }
}

const square = new Rectangle(10, 10);
console.log(square.area);//100

//上面的定义等价于
function Rectangle(height, width) {
    this.height = height
    this.width = width
}

Object.defineProperty(Rectangle.prototype, 'area', {
    enumerable: false,
    configurable: true,
    get() {
        return this.calcArea()
    }
})

Rectangle.prototype.calcArea = function() {
    return this.height * this.width
}

Rectangle.prototype.sides = function*() {
    yield this.height
    yield this.width
}
```
# static
`static` 定义类里的 静态的方法或者属性，静态属性/方法 只能被类本身，而不是类实例调用。静态方法通常作为 util 方法被创建.   
静态方法/属性是作为类本身的方法/属性添加的, 即添加到函数对象本身，而不是 函数对象的 prototype
```js
class Point {
  constructor(x, y) {
    this.x = x;
    this.y = y;
  }

  static displayName = "Point";
  static distance(a, b) {
    const dx = a.x - b.x;
    const dy = a.y - b.y;

    return Math.hypot(dx, dy);
  }
}

//等价于
function Point() {
    //...
}

Point.displayName = 'Point'
Point.distance = function(a, b) {

}
```
# this in methods 
不管是 实例方法，还是静态方法，如果没有指定调用者，那么函数体内的 `this` 将会是 undefined. 因为 class 总是在 `strict` mode 下执行code。  
静态方法或者静态变量里的 this 指向的是类本身，在派生类里还可以使用 `super`, 指向基类的类对象本身。  
实例方法或者实例属性里的 this 指向的是实例本身，在派生类里也可以使用 `super` 指向基类的实例方法或者属性

```js
class Animal {
  speak() {
    return this;
  }
  static eat() {
    return this;
  }
}

let obj = new Animal();
obj.speak() === obj; // true
Animal.prototype.speak() === Animal.prototype//true

let speak = obj.speak;
speak() === undefined; //true, 因为该函数执行时，并没有绑定 this, 在 strict 模式下，this 就是 undefined， 而不是 window

Animal.eat() === Animal //true, 所谓的静态方法，其实就是绑定在函数对象本身的方法
let eat = Animal.eat;
eat(); // undefined
```
如果将上面的code 以 function-based syntax 重写，那么如果没有给方法指定调用者, 在 non-strict mode 下返回的就会是全局对象
# 字段声明
可以在构造器之外预先声明变量, 变量可以有默认值，也可以没有。构造器之外的变量声明优先于构造器被执行。
```js
class Rectangle {
  height = 0; // 在 class 里声明属性，不需要使用 var,let,const
  width;
  constructor(height, width) {
    this.height = height;
    this.width = width;
  }
}
```
# initialization order
基类的字段默认初始化 > 基类的构造函数 > 子类的字段默认初始化 > 子类的构造函数
# static blocks
class 里允许写 static 静态初始化块
```js
class A {
    static a = 1

    static {
        // 静态初始化块里可以写一些逻辑
    }
}
```
# inherit
```js
class A {
    static a = 1
    x;
    y;
    constructor() {
        this.x = 0
        this.y = 0;
    }

    say() {
        console.log('A')
    }

    eat() {

    }
}

class B extends A {
    static b = 2;
    z;
    constructor() {
        super()
        this.z = 0
    }

    say() {
        console.log('B')
    }

    touch() {

    }

}

console.log(B.a)//1

function A() {
    this.x = 0
    this.y = 0;
}
A.a = 1
A.prototype.say = function() {
    console.log('A')
}
A.prototype.eat = function() {

}

//如果是 function-based, 想实现继承，方式有很多种，下面是其中一种
function B() {
    A.call(this) // 继承父类的属性，x, y 是 B 类实例自己 own 的属性
    this.z = 0
}
B.b = 2;
B.prototype.say = function() {
    console.log('B')
}
B.prototype.touch = function() {

}

// 虽然 B 的构造函数里调用了 A, 但是实际上 A 类 和 B 类现在还没有什么联系
// 为了能让 B 的实例能够访问 A 的实例方法，需要进行 link. 
// B 对象的 __proto__ 指向了 B.prototype, B.prototype 只有它自己定义的 say 和 touch 方法
// 为了 link 到 A, B.prototype.__proto__ 就需要指向 A 的 prototype
// 这样， B 的实例就可以访问 A 的实例方法了。
Object.setPrototypeOf(B.prototype, A.prototype)

// 解决了实例字段，对于 静态字段 呢？
console.log(B.a)//undefined, 这是因为 B 是一个普通的函数对象，B.__proto__ 指向的是 Function.prototype, 那么在 B 的原型链上就找不到属性 a
// 因此，还需要把 B 的静态字段和 A 的静态字段 link 起来
Object.setPrototypeOf(B, A)
console.log(B.a)//1

// 可见为了达成 class extends 的效果，基于函数的形式复杂繁琐很多
// 我们可以 double check 一下 class extends 底层实现是否还是基于原型链的
console.log(Object.getPrototypeOf(B.prototype) === A.prototype) // true
console.log(Object.getPrototypeOf(B) === A) //true
```
如果在 静态属性初始化时使用 this 和 super
```js
class A {
    static a = 1
    static aa = this.a//这里的 this 会绑定到 A, the class constructor
    static m() {//静态方法 by default 是 writable, non-enumerable, configurable
        return 3
    }
}

class B extends A {
    static b = super.m()//这里的 super 也会绑定到 A
}
console.log(A.aa, B.b)//1, 3
```
对于非静态属性，也是类似的.  
对于 class 而言, 如果在 constructor 之外初始化变量，比如 a = 3, 相当于在 constructor 内部调用 this.a = 3. 且构造器之外的变量初始化先于 constructor 执行
```js
class A {
    a = 3
    constructor() {
        this.x = 1
        this.y = this.c//this.y 会是 3
    }
    b = this.x// b 会是 undefined， since this.x has not been initialized yet
    c = this.a
    m() {//实例方法 by default 是 writable, non-enumerable, configurable
        return 5
    }
}
const PREFIX = 'prefix'
//如果B 没有显式构造器，则会默认添加一个构造器，该构造器会调用 super()
//如果 B 有显示构造器，则在构造器里必须调用 super(), 且只能在 super() 之后，才能使用 this
class B extends A {

    [`${PREFIX}Field`] = 10// 变量名可以通过表达式来推导
    bb = super.m()//实例属性初始化时，super 绑定到基类的 prototype
}

let a = new A()
let b = new B()
```
# private class features
class 里的属性默认是 public 的， 但是如果将变量用 `#`(hash) 开头，则这些属性将变为 private 属性.  
 ***更准确的说， private field 并不是属性，除了声明它的类本身，其它地方感知不到私有字段的存在， JS 并没有提供遍历私有字段的方法***  
私有字段必须预先声明，且私有字段名以 `#` 开头. 实例变量，实例方法，静态变量，静态方法， getter, setter 都可以是私有的。  
值得注意的是，私有字段只能在类内部被使用，在类外部不能访问私有字段. 且private 必须先被声明，然后才能被使用， 且 private 属性不可被 delete  
类可以提供 public 方法供外部调用，在该方法里访问 私有字段。但调用者必须是类或者继承类的实例。对于静态私有字段，只能通过该类本身调用。继承类也不行。  
```note
私有字段的具体实现不清楚，以我的理解，
对于私有实例字段，只有在通过构造函数产生的对象，才可以通过 public 方法访问私有字段。子类其实持有了私有字段，只是只能在声明该私有字段的类内部才能访问该私有字段。
对于私有静态字段，即使子类继承了基类，但是子类其实并没有持有基类的私有静态成员，所以不能访问
```
私有方法/私有 getter/setter 在 Babel7 里被实现
```js
class A {
    #a = 0//private property declaration, 也可也不指定默认值
    constructor() {
        this.#a = 1
        delete this.#a//syntax error
        this.#b = 2//syntax error, #b must be declared in an enclosing class
    }
    #m() {//private instance method

    }
    f() {
        return this.#m()//这里的this 必须是 A 或者继承类的实例
    }
}

class B extends A {}

const b = new B()
b.f()// 是可以的
```
同样的，私有属性也可以设置 getter/setter
```js
class A {
    #msg

    get #decorateMsg() {
        return `Hello: ${this.#msg}`
    }

    set #decorateMsg(msg) {
        this.#msg = msg
    }

    constructor() {
        this.#decorateMsg = 'world'
        console.log(this.#decorateMsg)
    }

    *#f() {//private generator function
        yield 1
    }

    ff() {
        return this.#f()//the this here must be instance of A
    }
}
new A()//Hello: world
```
对于静态私有属性，只有定义了该属性的 class 能够访问该属性, 包括读与写
```js
class A {
    static #a = 1
    static f() {
        return this.#a
    }
}
A.f()//1, A 能访问自己的私有静态成员

class B extends A {}
console.log(B.f())//type error, B 能访问 f 函数，但是不能访问 this.#a. 因为 B 本身没有私有静态属性 #a 
```
# class in Typescript
首先，类的字段都需要有 type 信息.  
`当违反了 TS 的 rule 后，编译器会报错。但这些错误仅仅是 TS 的编译期错误，仍然可以把 TS code 编译为 JS 代码执行`
```js
class Point {
    x: number
    y: number

    // ts 里 constructor 不允许初始化未声明的变量, 也不能有 return 
    constructor(x = 0, y = 0) {
        this.x = x
        this.y = y
    }
    // TS 里，如果只有 get, 没有 set, 那么该属性就会是 read-only
    // 在 strict mode 下，如果没有 set, 那么是不允许为该属性赋值的，runtime 会抛错
    get length() {
        return Math.sqrt(x*x, y*y)
    }
}

class Point3D extends Point {
    
    constructor() {
        // 对于派生类的构造器，在访问 this 之前，必须先显式调用 super. 如果没有，TS 会报错
        super()
    }
}
```
## implements
TS 里，`class` 可以 `implements` 一个或者多个接口 `interface`， class 需要满足被实现的接口的 type.
```js
interface A {
    x: number
    y?: number
}
// 需要注意的是，implements interface 仅仅只是一个 check, interface 并不会对 class 有任何影响
// 它只是让 TS 去 check class 是否可以被当作一个满足 interface type 的类
class B implements A {
    // B 并没有 y 字段，B 也 满足 A 的接口定义，但不能访问 b.y, 因为 B 并没有 y 字段
    x = 0
}
```
## extends
TS 里 extends 添加了一些规则，比如子类的类型签名在 `override` 时必须能够 cover 子类，否则会报错
```js
 class A {
    greet() {

    }
 }

 class B extends A {
    // 在这里 TS 会报错，因为这里 greet 的参数是 string, 不包含 undefined，所以并没有  cover 子类里的 greet
    greet(name: string) {
        console.log(name.toLowerCase())
    }
 }
 // 在这种场景下，为什么需要报错呢？
 const a: A = new B() // 这句代码是没有问题的
 // 这里会抛异常，因为 a 实际指向了 b 对象，会调用 b.greet(),所以 name 是 undefined，但是调用了 name.toLowerCase(). 
 // 所以 TS 强制要求子类在 override 时，需要 cover 基类的 type 约束
 a.greet() 
```
## member visibility
TS 支持配置 属性和方法 对于外界的可访问性，和 java 类似，通过 public/protected/private 来标志。  
字段的默认访问权限是 public, 即可以被任何外界访问。  
protected 表示只能被类以及类的子类访问。且只能在类的内部访问。      
private 表示只能被类自己内部访问。  
注意这里的限制只是 TS 提供的静态编译期的限制，并不是真的实现了 public/protected/private。 JS 提供的 private field(# 开头命名的 field) 才是真正的 private
## 类型断言 in class
```ts
// 之前有碰到过 union 类型的 类型断言，比如
type Sex = 'Male' | 'Female'
const isMale = (s: Sex) : s is 'Male' => s === 'Male'

declare const s: Sex;
if (isMale(s)) {
    // s 会被当作 Male 类型
} else {
    // s 会被当作 Female 类型
}
```
在 class 里，也可以使用类型断言, 比如下列 `hasValue` 就对 this 的 类型做了断言
```js
class Box<T> {
  value?: T;
 
  hasValue(): this is { value: T } {
    return this.value !== undefined;
  }
}
```
## parameter properties
TS 提供了特殊的语法，将构造函数的参数直接转变为 属性。 方法是使用 权限访问符或者 readonly 修饰 构造函数参数。  
这个语法就是为了简写，只需要在构造函数里声明属性即可，也不需要再写 this.x = x 之类的代码。
```js
class A {
    constructor(
        public x: number,
        protected y: number,
        private z: number,
        readonly length: number
    ) {
        // 函数体内就不需要再对属性就行初始化了
    }
}
```
## abstract
TS 提供了语法，实现了 abstract 类，这个概念和 Java 里的抽象类是一致的。  
抽象类不可以被实例化，子类必须实现基类里的抽象方法。