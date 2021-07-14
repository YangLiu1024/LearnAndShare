JS 是一种基于原型/对象的语言，而不是基于类。它只有对象，原型对象也可以作为一个模板，新对象可以从中获得原始的属性

任何对象都可以指定其自身的属性，既可以是创建时，也可以是运行时。而且，任何一个对象都可以作为另一个对象的原型，从而允许后者共享前者的属性

JS 中并没有专门的类的定义(ES6 引入的 Class 定义其实也只是已有的原型继承方式的语法糖而已，并没有引入新的面向对象的继承模型)，你通过定义构造函数的方式来创建一系列有着特定初始值和方法的对象。任何 JS 函数都可以被用作构造函数，也可也使用 `new` 操作符来创建一个新的对象

JS 中的所有对象均为实例，不区分类和实例。并且 JS 通过构造器函数来定义创建一组对象，通过 `new` 操作符创建对象。

在定义继承类时，JS 通过指定一个对象作为原型并且与构造函数一起构建对象的层级结构，它遵循原型链继承属性。

JS 构造器函数或原型指定实例的初始属性集。允许动态的向单个对象或整个对象集中添加或移除属性。

# prototype and __proto__
每个 JS 对象都有一个 private property named `prototype` which holds a link to another object, and the `prototype` object has a *prototype* of its own, and so on util an object is reached with null as its prototype. By definition, null has no prototype, and act as the final link in this prototype chain.

JS 里基本上所有对象都是 `Object` 的实例，所以*Object* sits on the top of a prototype chain.

当 JS 执行 `new` 操作符时，它会先创建一个blank, plain JS 对象，并将这个普通对象的 *__proto__* 指向构造函数的 *prototype*, 将这个普通对象设置为执行构造函数的 this的 值, 然后执行构造函数。该普通对象的 *__proto__* 决定其用于检索属性的原型链。当构造函数执行完成后，所有的属性都被设置完毕，如果构造函数本身没有返回一个对象，则返回this，通过赋值语句将它的引用赋值给变量。

Note: 
* `prototype` 是 *Function* 对象的一个属性，描述了由该构造函数创建的对象的原型信息。在函数声明的时候被创建。
  该对象包含 `constructor` 和 `__proto__`， *constructor* 是一个函数对象，shadow 了 Object.prototype.constructor，该对象
  `__proto__` 则指向 `Function.prototype`
  如果我们给构造函数的 prototype 添加了属性，那么所有由该构造函数创建的对象，都将拥有该属性。因为所有由该构造函数创建的对象，都拥有指向该构造函数 prototype 的 __proto__ 属性，当 构造函数的 prototype改变，对象的原型链也改变了。
* `__proto__` 是任意对象的 internal property, 指向创建该对象的构造函数的 *prototype*，可以通过 Object.getPrototypeOf(obj) 返回

```js
//在声明 函数 f 时，f.prototype 就会被创建，默认会包含一个 constructor 属性和一个 __proto__ 属性
//constructor 即该构造函数本身的定义，__proto__则会指向 Function.prototype, 因为f 是一个 Function 对象
function f() {
	this.a = 1;//构造函数里的this 是指向新建对象的，和 函数 f 本身并没有什么关系
    this.b = 2;//只是会把 f.prototype 赋值给新建对象的 __proto__属性
}
//ff 和 f 一样
function ff() {
	this.c = 3;
}
//这儿创建了一个 f 的对象，并把该对象赋值给了 ff 的 prototype, 相当于 ff 继承了 f, 因为 ff 的对象会拥有 f 的 prototype 里拥有的属性
//注意此时，ff 的 prototype 被赋值给了一个对象 pt，该对象由 f 构造函数创建，{a：1，b:2, __proto__:f.prototype}
//该对象的 __proto__ 指向了创建该对象的构造函数 f 的 prototype, f.prototype 则由 f 的 constructor 和 __proto__ 构成
//f.prototype.__proto__ 则指向了 Object.prototype, 因为 f.prototype 只是一个普通的默认创建的对象， 而 Object.prototype.__proto 则指向了 null
//同样的，f 和 ff 本身也是一个 Function  对象
let pt = new f
ff.prototype = pt;
let o = new ff
console.log(o.__proto__ === ff.prototype)//true
console.log(ff.prototype === pt)//true, pt 为 {a：1，b:2, __proto__:f.prototype}， 则在查找 o.a 时能找到 属性 a
console.log(pt.__proto__ === f.prototype)//true, 如果查找 o.d, 在pt own的 属性上找不到，则会继续查找 pt.__proto__, 即 f.prototype
console.log(f.prototype.__proto__ === Object.prototype)//true, 因为 f 的 prototype 只是一个普通的对象，只有 constructor 和 __proto__, 所以继续往下查找
console.log(Object.prototype.__proto__ === null)//true, 直到查找对象的__proto__返回为 null 停止
//所以对象 o 的原型链则是 o({c:3}) -> o.__proto__ === ff.prototype({a:1,b:2}) -> o.__proto__.__proto__=== f.prototype({}) -> o.__proto__.__proto__.__proto__===Object.prototype -> null

//同时，函数 f,ff 本身也是一个 Function 对象，它们的 __proto__ 则指向了 Function.prototype
console.log(f.__proto__ === Function.prototype)//true
console.log(ff.__proto__ === Function.prototype)//true
console.log(Function.prototype.__proto__ === Object.prototype)//true, Function.prototype 本身只是一个普通的对象，则其__proto__ 指向了 Object.prototype

//所以函数对象本身同时具备 prototype 和 __proto__ 属性
```
Note:
* 箭头函数在声明时，并不会创建 `prototype`, 所以箭头函数并没有 *prototype*, 而只有*__proto__* 指向 *Function.prototype*
  又因为没有 prototype, 所以并没有 *constructor* 函数，不能用于执行  `new`

JS  里check 一个对象是否为某个类型(obj instanceof Class), 其实只是去查找 obj 的__proto__ chain 里是否包含指定 Class 的 prototype
```js
function instanceOf(Func){//Func 需要是一个函数
  var obj = this;
  while(obj !== null){
    if(Object.getPrototypeOf(obj) === Func.prototype)
      return true;
    obj = Object.getPrototypeOf(obj);
  }
  return false;
}

instanceOf.call(obj, Class)
```
# inheriting property and methods
子类可以 shadow 父类的属性，只需要在子类声明具有相同名字的属性
```js
function f() {
    this.a = 1;
    this.b = 2;
}

function ff() {
    this. b = 3;
}

ff.prototype = new f

let o = new ff
console.log(o.b)//3,  因为 o 本身具有 b 属性，所以 ff.prototype 里面的 b 是不可到达的，这样就造成了 property shadow
console.log(o.__proto__.b)//2, 必须显示访问 o.__proto__才能访问到被 shadow 的属性
```
同样的，子类也可以覆盖/继承父类的 methods
```js
let o = {
  a: 2,
  m: function() {
    return this.a + 1;
  }
};

console.log(o.m())//3, 此时 函数 m 绑定的this 是 o

let p = Object.create(o)//p 继承于 o, 即 p.__proto__ === o

console.log(p.m())//3, 即使 this 绑在了 p 上面，p.a 仍然会找到 p.__proto__.a 即 o.a 上面

p.a = 4;
console.log(p.m())//5, 因为 p.a 给对象 p 添加了属性 a, 这个 a 会 shadow p.__proto__.a

p.m = function() {
    return this.a + 5;
}

console.log(p.m())//9
```

# Different way to create objects
## syntax constructs
```js
//通过 {key:value} 直接创建对象 o
//这样创建的对象直接继承自  Object.prototype
//注意 o 自己 own 的 property 其实只有 a, 其它能够访问的 property，其实来自于 o.__proto__, 即 Object.prototype, 比如 hasOwnProperty, isPrototypeOf, valueOf, toString 等等
let o = {a : 1}// {} equals to Object.create(Object.prototype)

//通过 [ elements] 来创建数组对象
//b.__proto__ === Array.prototype, b own 的 属性只有 元素的索引 '0' 和 '1', 注意对象的 key 如果不是 string, 也都会自动转换为 string
//b.key equals to b['key'], 且只有当 key 是合法的变量名 b.key 才能正确 work, 比如当 key 是数字时，就不能使用 b.1, b.'1' 之类的
//b[key] 支持 key 是变量，或者key 是字符串, 即使 key 包含一些特殊字符
//所以 b[key]相对于 b.key 更强大一些，只是 b.key 写起来更方便，也更可读
let b = ['a', 'b']//相当于 {'0': 'a', '1': 'b', length: 2, __proto__: Array.prototype}
//Array.prototype 里定义了所有的 Array 的方法，such as： indexOf, filter, reduce, shift 之类， 然后被 b 继承

//函数在声明的时候，就会创建对应的函数对象，以及该函数对象的 prototype 和 __proto__
//这里 f 的 prototype 是一个普通的对象，包含 constrcutor 和 __proto__ 属性，并没有被赋值给其它任何对象，即没有继承任何类
//constructor 是构造函数定义本身，因为 f 是函数对象，f.__proto__ 指向 Function.prototype, f.prototype.__proto__ 指向 Object.prototype
function f() {
  //如果函数被当作构造函数执行，即 new f()，则首先创建一个普通对象，将该对象的 __proto__ 指向构造函数 f.prototype, 然后将该普通对象作为 this， 执行构造函数
  //如果构造函数没有 return 语句，或者return 的 不是一个对象，则会将 this 作为返回值
  //如果函数返回的是一个对象，则会返回该对象
  return 2
}

let o = new f()//o 是一个普通对象，并且没有 own 任何property, o.__proto__ === f.prototype, f.prototype.__proto__ === Object.prototype
//f.__proto__ === Function.prototype, Function.prototype.__proto__ === Object.prototype
//Function.prototype 定义了 call,apply, bind 之类的方法，所以 f 也继承了这些方法
```
## Constructor
当函数 happens to be called with `new` operator, 这个函数就是被当作构造函数来使用
```js
function Graph() {
  this.vertices = [];
  this.edges = [];
}

Graph.prototype.addVertex = function(v) {
  this.vertices.push(v);
}

var g = new Graph();//g.__proto__ === Graph.prototype, g own properties vertices & edges
```
## Object.create
`Object.create(obj)` 会创建一个新的空对象，并且该对象会以传入的对象作为 __proto__
```js
var a = {a: 1};
// a ---> Object.prototype ---> null

var b = Object.create(a);
// b ---> a ---> Object.prototype ---> null
console.log(b.a); // 1 (inherited)

var c = Object.create(b);
// c ---> b ---> a ---> Object.prototype ---> null

var d = Object.create(null);
// d ---> null
//这里的 d 就是一个完全不包含任何属性的 {}，连 __proto__ 都没有
console.log(d.hasOwnProperty);//undefined, 因为 d 并没有继承自 Object.prototype, 所以 d 并不包含 property hasOwnProperty
```
# Classical inheritance with Object.create
JS  里面怎么定义一个继承链
```js
//define a function named Shape, its prototype has property constructor and __proto__
//每一个通过 new Shape() 创建的对象，将拥有 自己的 property x and y, 然后 __proto__ 指向 Shape.prototype
//当 Shape.prototype 改变的时候，所有由 new Shape() 创建的对象都会受到影响
function Shape() {
  this.x = 0;
  this.y = 0
}
//为 Shape.prototype 添加一个方法，所有由 new Shape() 创建的对象将继承该方法
Shape.prototype.move = function(x, y) {
  this.x += x;
  this.y += y;
}
//定义了一个新的函数 Rectangle
//到现在为止，Rectangle 和 Shape 还没有什么关联， Rectangle.prototype 只是一个普通的拥有 constructor 和 __proto__ 的对象
function Rectangle() {

}

//1. 指定一个 Shape 对象作为 prototype
// new Shape() 会创建一个新的对象 {x: 0, y: 0, __proto__: Shape.prototype}
// 这样，所有通过 new Rectangle() 创建的对象的原型链都会指向这一个对象，这个对象的 属性 x 和 y 被所有 new Rectangle() 生成的对象共享
//注意 新生成的 Rectangle 对象并不 own x 和 y property
Rectange.prototype = new Shape()
//2. 通过 Object.create
// Object.create(Shape.prototype) 也会生成一个对象，这个对象是一个空对象，且以 Shape.prototype 作为__proto__. 即{__proto__: Shape.prototype}
//这样通过 new Rectangle() 生成的对象的 __proto__ 都将指向这个空对象，这个空对象又指向了 Shape.prototype
Rectange.prototype = Object.create(Shape.prototype)
//1 和 2 的区别就在于 2. 并不会执行 Shape 的构造函数

//可以看出，在 JS 里，类型之间的继承是只基于原型的，和构造函数本身的实现，其实也没有太大关系。
//比如在上面 2. 的条件下，new Rectangle() 只是一个空对象，并没有 own x, y, 其__proto__指向的对象也没有 x,y, 但是仍然可以说 new Rectangle() instanceof Shape
//所以为了更符合对于类的继承的认知， 在 2. 之后，可以将 Rectangle 的构造函数改为
function Rectangle() {
  Shape.call(this)//in this way, 生成的 Rectangle 对象则会 own x,y 属性
}

//上面的代码还是有一点点问题, 因为每一个对象都会有一个 constructor 属性，该属性应该指向创建该对象的构造函数
//在上例中， new Rectangle().constructor 会指向 Shape.prototype.constructor, 这是由于 Rectange.prototype = Object.create(Shape.prototype)
//我们为 Rectange.prototype 重新赋了值， 并且新的引用的对象本身并不含有 constructor 属性， 继而最后只能在 Shape.prototype里找到 constructor
//为了避免这个问题
Rectangle.prototype.constructor = Rectangle//这样 new Rectangle().construtor === Rectangle
```
`Object.create(obj, options)` 还可以有第二个参数 options, 用来对生成的对象的属性进行配置.

by default, options 里添加的属性 ARE NOT writable(该属性是否可写), enumerable(是否可遍历), configurable(为 false 时， enumerable 不可改，writable 只能从 true 改为 false, 且该属性不可删除)
```js
o = Object.create(Object.prototype, {
  // foo is a regular 'value property'
  foo: {
    writable: true,
    configurable: true,
    value: 'hello'
  },
  // bar is a getter-and-setter (accessor) property
  bar: {
    configurable: false,
    get: function() { return 10; },
    set: function(value) {
      console.log('Setting `o.bar` to', value);
    }
  }
});
```
# Classical inheritance with class
ES5 定义了一些新的操作符用来构建继承链，such as `class`, `constructor`, `extends`, `super`, `static`
```js
class Polygon {
  constructor(height, width) {
    this.height = height;
    this.width = width;
  }
}

class Square extends Polygon {
  constructor(sideLength) {
    super(sideLength, sideLength);
  }
  get area() {
    return this.height * this.width;
  }
  set sideLength(newLength) {
    this.height = newLength;
    this.width = newLength;
  }
}

var square = new Square(2);
```