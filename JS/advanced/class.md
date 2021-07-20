`class` 是ES5 支持的一种创建类的语法糖，它其实也是基于 Function 和 prototype 的。

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

在 constructor 里可以通过 `super` 直接调用父类构造方法
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
`static` 定义类里的 静态的方法或者属性，静态属性/方法 只能被类本身，而不是类实例调用。静态方法通常作为 util 方法被创建

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
当 class 中的 methods is called without a value for this, the this in methods will be undefined.

因为 class 总是在 `strict` mode 下执行code
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
如果将上面的code 以 function-based syntax 重写，那么即使没有给方法绑定 this, 返回的就会是全局对象

# public instance fields

# inherit
```js
class A {
    static a = 1
}

class B extends A {
    static b = 2;
}

console.log(B.a)//1

//如果是 function-based, 想实现继承，需要将一个函数的 prototype 绑定到另一个类的对象上
function A() {}
A.a = 1

function B() {}
B.b = 2;
B.prototype = Object.create(A.prototype)
console.log(B.a)//undefined, 这是因为 B 是一个普通的函数对象，B.__proto__ 指向的是 Function.prototype, 那么在 B 的原型链上就找不到属性 a

//class B extends A 除了将 B.prototype = Object.create(A.prototype)， 还执行了 B.__proto__ = A, 这样在查找 B.a 时，就会找到 A, A.a 是存在的
B.__proto__ = A
console.log(B.a)//1
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

private 属性只能在 class body 内被访问， 且private 必须先被声明，然后才能被使用， 且 private 属性不可被 delete

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
        return this.#m()//这里的this 必须绑定 A 的实例，即使是 A 的继承类的实例也不行
    }
}
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

