# Class
TS 对 JS keyword class 也是完全支持的，并且加入了一些其它的特性，比如 访问权限修饰符，readonly, optional(?).
# JS 实现继承
JS 里，如果通过原型链来实现继承
```js
function A() {
    this.a = 1
}

A.prototype.sayHi = function(name) {//原型链上的成员
    console.log(`hello ${name}`)
}

A.count = 1000;//相当于 static 成员

function B() {
    A.call(this)//将 A 的构造函数通过 B 的实例 this 调用，从而将 A 里注册的实例属性注册到 B 实例里
    this.b = 2;
}
//到现在为止，B 和 A 还没啥关系，为了让 B 继承 A
//创建了一个对象作为 B 的原型，该对象的原型指向 A.prototype
//这样，所有 B 的实例，都可以访问 A 的 原型上定义的成员了
B.prototype = Object.create(A.prototype)
//但上面的代码还有一点问题， B.prototype 里的constructor 属性被去掉了，B.prototype.constructor 会访问到 A.prototype.constructor
//所以需要将 B 的构造函数重置为 B
Object.defineProperty(B.prototype, "constructor", {
    value: B,
    writable: true,
    enumable: false
})
//对于静态成员，上述代码还是不够的，B.count 仍然会返回 undefined
B.__proto__ = A//Object.setPrototypeOf(B, A)
```
上述通过原型链实现继承的方法，通过 ES5 class 来实现的话
```js
class A {
    a = 1
    static count = 1000
    sayHi(name) {
        
    }
}

class B extends A {
    b = 2
}
```

# implements
A implements B 只是约束 A 可以当作 B 来使用， B 本身并不会对 A 有什么影响。  
interface 只是用来规定一个 shape, 满足该 shape 的对象都可以看作 implements 了该 interface, 既是该对象的 type 并没有显示声明过 implements 该 interface.   
# extends
继承则和 java 里的概念基本一致，继承类可以有自己的属性和方法，也会拥有基类的所有属性和方法。   
## shadow
当出现继承类属性 shadow 基类属性时，在 JS 里，属性或者方法的查找是基于名字匹配的，TS 对 shadow 有一定的约束，它要求继承类必须 assignable to 基类
## 初始化顺序
1. 基类的类内初始值
2. 基类的构造函数执行
3. 子类的类内初始值
4. 子类的构造函数

# Member visibility
TS 里， class 成员的默认访问权限是 public, 即该成员能够在任何地方被访问。protected 成员只能被类本身内部以及它的继承类内部访问， private 成员则只能在类本身内被访问。  
需要注意的是，访问权限其实只在编译阶段起作用，编译后，在运行时，其实并没有访问权限，任何属性都是可以被访问的。

# static member
TS 里 static 成员仍然可以使用访问权限修饰符
# abstract
抽象类的概念和 Java 一样