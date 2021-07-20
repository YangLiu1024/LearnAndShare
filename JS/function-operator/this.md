JS general function 中的 `this` 是在运行时动态绑定 caller 的，如果没有绑定 this, 则函数中的this 默认执行全局对象 window

arrow function 本身并不提供 this binding, arrow function 里的 this 绑定的是其上下文中的 this

```js
//global context
console.log(this === window)//true

a = 37;
console.log(this.a, window.a)//37, 37

this.b = 'MD'
console.log(window.b)//MD

//function context
//在函数中， this 根据 caller 而绑定。 note that 在 strict mode 下，如果this 没有被绑定，会是 undefined，在 non strict 下，会是 global object window
//为了给 function 绑定 this, 可以通过 function.apply 或者 function.call 来invoke， 或者 function.bind() 来显示绑定
function f() {
    return this
}

f() === window//在 non strict mode 下 为true, 在 strict 模式下为 false
let obj = {x: 1, f: f}
f.call(obj) === obj//true
obj.f() === obj//true

const ff = f.bind(obj)
ff() === obj//true
let obj2 = {x: 2}
ff.call(obj2) === obj2//false, should be obj too

const fff = ff.bind(obj2)
fff() === obj2//false, since the function call only bind once, so fff still bind with obj

//arrow function
//对于箭头函数，它的 this 永远指向创建该箭头函数的code 的上下文 this
let af = (() => this)//this 指向global object window

function ff() {
	return af()//在函数内调用箭头函数
}

console.log(ff() === window)//true
let o = {a: 1}
console.log(ff.call(o))//即使给ff 绑定this, 但是箭头函数仍然返回global object

let o3 = {func: af}
o3.func() === window//true, 即使把箭头函数赋予对象，但是箭头函数的返回值仍然是 window

const bf = af.bind(o)
bf() === window//true, 给箭头函数bind,仍然会返回 window


function fc() {
	let f = (() => this)//箭头函数在函数内定义，这里的 this 则指向函数自己的 this. 而函数自己的this则依赖于函数被调用的方式
    return f()
}
console.log(fc() === window)//true
let o2 = {a: 1}
console.log(fc.call(o2) === o2)//true
```

对象里的 get/set property 函数里的 this, 自动绑定到调用该 property 的对象上

在构造函数里，this 指向正在创建的对象

在 DOM event handler function 里，this 指向发生事件的 element

```js
function Person() {
  // The Person() constructor defines `this` as itself.
  this.age = 0;
  let sex = 'male'
  //函数体内的 this 永远和它的调用者绑定在一起
  //setTimeout 没有显式调用者，所以它是被全局对象 window 调用的
  //growUp 是一个inner function, inner function 可以访问外部变量
  //在 1s 之后，setTimeout 就会直接执行 growUp, 所以 growUp 也是被 window 调用的
  setTimeout(function growUp() {
    console.log(this === window)//true
    console.log(this.age, set)//undefined, male
    this.age++;
    console.log(this.age)//NaN
  }, 1000);
}

var p = new Person();
```
为了解决上面的问题，一个办法是使用一个 outer 变量持有 this, 这样inner function 就可以访问这个变量，从而访问 outer 的this
```js
function Person() {
    this.age = 0;

    let that = this;
    setTimeout(function growUp() {
        that.age++
    }, 1000)
}
new Person();
```
或者将inner 函数binding 到 outer 的 this
```js
function Person() {
    this.age = 0;
    function growUp() {
        this.age++
    }
    setTimeout(growUp.bind(this), 1000)
}
new Person();
```
或者使用箭头函数. 箭头函数的this 永远指向声明该箭头函数的上下文中的this
```js
function Person() {
    this.age = 0;
    setTimeout(() => this.age++, 1000)
}
new Person();
```