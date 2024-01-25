# super 
super 的实现并不是简单的在 this 原型链上去查找
在 class 定义里，可以使用 super 来调用父类的实现。语义上，当使用 *super.method()* 时，JS 引擎需要调用当前对象 this 的原型链上的 method 方法，它是怎么做到的呢？  
这个看起来挺简单，仿佛可以直接使用 *this.__proto__.method* 来实现，但这个很容易就 break 掉
```js
// 这个例子可以运行成功
let animal = {
  name: "Animal",
  eat() {
    alert(`${this.name} eats.`);
  }
};

let rabbit = {
  __proto__: animal,
  name: "Rabbit",
  eat() {
    // 这就是 super.eat() 可以大概工作的方式
    // 这里 call 必须使用 this, 否则函数将使用 this.__proto__ 作为 this 被调用
    this.__proto__.eat.call(this); // (*)
  }
};

rabbit.eat(); // Rabbit eats.
```
当我们在原型链上再添加一个对象，代码就会出错了
```js
let animal = {
  name: "Animal",
  eat() {
    alert(`${this.name} eats.`);
  }
};

let rabbit = {
  __proto__: animal,
  eat() {
    this.__proto__.eat.call(this); // (*)
  }
};

let longEar = {
  __proto__: rabbit,
  eat() {
    this.__proto__.eat.call(this); // (**)
  }
};

// 当调用 longEar.eat 时，this 指向的是 longEar, 进入 eat 函数，找到 rabbit 的 eat 函数，再次通过 longEar 调用
// 在 rabbit eat 函数里，因为 this 指向的是 longEar, longEar.__proto__ 又回到了 rabbit, 那么就会再次使用 longEar 调用 rabbit 里面的 eat 方法
// 继而无限循环
longEar.eat(); // Error: Maximum call stack size exceeded
```
那么 super 是通过什么方式来避免这个问题的呢？需要注意的是，上述方案的问题就在于在往原型链上查找方法的时候，this 会始终指向调用者。这个特性本身也是 JS 所期望的，方法里的 this 永远和 调用者绑定(箭头函数另说)  
为了解决这个问题，JS 引擎为成员函数(对象的成员函数或者类的成员函数，注意，是成员函数，而不是属性)添加了一个特殊的内部属性 [[HomeObject]]. 该属性会绑定到对象上，并且不能改变。  
并且该属性只会被 super 所使用。当在方法里调用 *super.method()* 时，首先找到当前方法所对应的 [[HomeObject]]，注意这里找的是方法，和调用当前方法的对象无关。  
然后去查找 [[HomeObject]].__proto__, 在这个对象里找到 method 方法，然后通过当前对象调用它。
```js
let animal = {
  name: "Animal",
  eat() {         // animal.eat.[[HomeObject]] == animal
    alert(`${this.name} eats.`);
  }
};

let rabbit = {
  __proto__: animal,
  name: "Rabbit",
  eat() {         // rabbit.eat.[[HomeObject]] == rabbit
    super.eat();
  }
};

let longEar = {
  __proto__: rabbit,
  name: "Long Ear",
  eat() {         // longEar.eat.[[HomeObject]] == longEar
    super.eat();
  }
};

// 正确执行
longEar.eat();  // Long Ear eats.
```
***可见，方法的 [[HomeObject]] 是在定义的时候就绑定好了的，和方法的调用对象无关。***[[HomeObject]] 的存在违背了函数是动态的设计，即函数本身和对象无关的原则。  
还有一点需要注意的是，super 只能在方法里被调用，如果函数是作为属性被定义，则不可以使用 super
```js
let rabbit = {
  __proto__: animal,
  eat: function() {
    // 语法错误，super 不能在这里被调用
    super.eat();
  }
};
```
super 在查找方法的时候，并不考虑对象，只考虑方法本身所绑定的 [[HomeObject]]
```js
let animal = {
  sayHi() {
    alert(`I'm an animal`);
  }
};

// rabbit 继承自 animal
let rabbit = {
  __proto__: animal,
  // tree.sayHi() 会把 tree 作为 this 来调用 rabbit.sayHi
  // 这里使用了 super, 那么 JS 引擎会去 rabbit.sayHi.[[HomeObject]] == rabbit 里去查找 sayHi, 继而查找到 animal
  // 可见，这里的 super 并没有考虑 this 的原型链，而是通过 方法绑定的 [[HomeObject]] 来查找的
  sayHi() {
    super.sayHi();
  }
};

let plant = {
  sayHi() {
    alert("I'm a plant");
  }
};

// tree 继承自 plant
let tree = {
  __proto__: plant,
  sayHi: rabbit.sayHi // (*)
};

tree.sayHi();  // I'm an animal (?!?)
```