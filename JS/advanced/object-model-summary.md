# JS 原型链 和 class
JS 里除了基础类型和一些不常用的，其它的都是 Object, 以及 Object 的衍生类型。  
基础类型包括 number, string, boolean, null, undefined, 不常用的包括 Bigint, Symbol.  
null 是一种特殊的 object, typeof null 会返回 ‘object’. null 表示空对象，不具备任何属性或方法。  
undefined 表示未定义，typeof undefined 会返回 'undefined'.

# 原型链
对象的原型链是什么呢？就是当在对象上查找属性的时候，JS 会沿着对象的原型链一直往上查找，直到终点. 这里也牵扯出了构造函数的 *prototype* 属性。  
每一个对象在创建的时候，都会将该对象的 *__proto__* 属性，指向创建它的构造函数的 *prototype* 上。常见的 plain 对象的创建，其实就是将对象的 __proto__ 属性直接指向了 Object.prototype
```js
// a 是一个 plain 对象，a.__proto__ 指向了 Object.prototype
// Object.prototype 也是一个对象，该对象上定义了许多方法。比如 
const a = {x: 1, y: 2}
```
我们需要先理清楚一些概念, 比如 Object, Function.  
在 JS 里，Object 是构造函数，那么它首先是一个函数，Object instanceof Function 会返回 true.  
同时，Function 也只是 Object 的衍生类型，那么 Object 其实也是一个函数对象，Function instanceof Object 会返回 true, Object instanceof Object 也会返回 true.  
但 typeof Object 会返回 'function'. 这是 typeof 关键字本身的实现。  
每一个函数对象都会有 prototype 属性， Object 有，Function 也有
```js
// Object.prototype
{
    toString(),
    valueOf(),
    hasOwnProperty(),
    isPrototypeOf(),
    __proto__: null
}
// Function.prototype 是 native code
{
    apply(),
    call(),
    bind()
}
```
Object 和 Function 除了是构造函数外，本身也是对象，也可以有自己的属性。比如，一些常用的 方法，其实都是定义在构造函数上的
```js
// Object
Object.assign()
Object.keys()
Object.freeze()
Object.getOwnPropertyNames()
// 除了这些之外，Object 还可以访问 定义在 Function prototype 里的方法，比如 Object.apply()，这是为什么呢?
// 这是因为 Object 在查找 apply 的时候，发现自己没有这个属性，那么就尝试在原型链上继续查找。又因为 Object 是函数对象，那么 Object.__proto__ 就指向了 Function.prototype 
// Function.prototype 里定义了 apply，call,bind，所以 Object 可以访问到这些属性
```
那么，对于所有对象，不管是函数对象，还是非函数对象，在往上查找的终点，都是 Object.prototype. 而 Object.prototype.__proto__ 是 null, 查找结束。这个就是 JS 原型链

# Before class
在支持 class 语法之前，JS 里是通过函数的形式来构建原型链的
```js
function Person(first, last) {
    this.age = 0;
    this.sex = 'male';
    this.firstName = first;
    this.lastName = last;
}

Person.prototype.getAge = function() {
    return this.age;
}

Person.prototype.getSex = function() {
    return this.sex;
}

Object.defineProperty(Person.prototype, 'fullName', {
    get() {
        return this.firstName + ' ' + this.lastName
    }
})

const p1 = new Person('yang', 'liu')
```
当需要对 Person 进行继承的时候，会有一些细节问题需要处理
```js
// 学生也是一种 Person, 那么该怎么让 Student 继承 Person 的属性和方法呢？
function Student() {
    this.grade = 6;
}
// JS 里其实有多种方式，每种有一些差别。常用的，其实是希望子类直接 own 父类中的属性，仅仅继承父类的方
function Student() {
    // 这样，子类继承了父类的属性
    Person.apply(this);
    this.grage = 6;
}
// 让 Student 的 prototype = {__proto__: Person.prototype}, 这样创建一个空对象作为 Student 和 Person 之间的连接
Student.prototype = Object.create(Person.prototype)
// 但上述方法也会带来新的问题，因为更改了 Student 的 默认 prototype, 使得 s.constructor 不再指向 Student 了，所以需要进一步修改
Student.prototype.constructor = Student

const s = new Student()
```

# class
通过开发者自己来控制原型链其实是很低效的行为， ES5 推出了 class 语法。class 本身最后其实也会是一个函数，只是支持了一些新的语法特性
```js
class Person {
    // 支持属性的默认初始化
    age = 0;

    sex = 'male'
    constructor(first, last) {
        this.firstName = first;
        this.lastName = last;
    }

    get fullName() {
        return this.firstName + ' ' + this.lastName;
    }

    getAge() {
        return this.age;
    }

    // 这里的方法，如果这样写，那么就会进入到 Person.prototype 里，是所有对象共享的方法
    // 这个函数就是一个普通的函数，它的 this 永远指向调用者
    // 那么为了让 class 里的方法，永远指向创建的对象，需要通过箭头函数来完成
    getSex() {
        return this.sex;
    }

    // 通过箭头函数，将实现体内的 this 绑定在了创建的对象上
    // 这样也会带来另一个小问题，那就是每个对象都会有一个自己的 getSex 函数
    getSex = () => {
        return this.sex;
    }
}
```
Person 最后也是一个函数
```js
Person.constructor === Function
Person.prototype.constructor === Person
```

在实现继承时
```js
// Student 继承了 Person 的属性
// Student.prototype 是 {constructor: Student}
// Student.prototype.__proto__ 是 Person.prototype
class Student extends Person {
    constructor() {
        // 必须显式调用 super
        super();
        this.grade = 6;
    }

    getAge() {
        // 调用基类的方法实现
        const age = super.getAge()
    }
}
```
基于 class 的写法会更简单，清晰，而且，还不仅仅如此。  
class 里可以直接使用 static 标志属性或者方法为 static, 即该属性和方法是绑定在 类本身的。class 里还支持真正的私有属性。

# super 实现细节
refer to *super.md*
# Difference between function and class
有人说 class 只是 function 语法的语法糖，其实并不是。 class 可以说是 function 语法的超集，它提供了一些额外的功能
1. class 定义的构造器函数 内部属性[[isClassConstructor]] 会为 true, 这导致该构造函数只能通过 new 来调用，但是普通的 function 构造器是可以直接调用的
2. class 里定义的 方法，默认是不可枚举的，但是 function 语法，是通过 Person.prototype.getAge 来实现的，默认是可枚举的，通过 for...in... 是可以访问的
3. class 内部必须使用 *use strict*