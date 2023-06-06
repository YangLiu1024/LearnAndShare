# Introduction to s function

1. Function declaration
```js
//regular function
function func(a, b) {
  return a * b;
}

func(1, 2);

//expression function
//the function here is anonymous function, then store in variable, and invoke by variable
var func = function(a, b) {return a * b;}
func(1, 2);

//arrow function
//note that the <b>this</b> in arrow function implementation refer to the owner of this arrow function
// 普通函数体内的 this 总是指向 caller, 如果没有调用者， 在 strict mode 下，this 就会是 undefined
// 箭头函数没有调用者 this 这个概念， 它函数体内的 this 总是指向定义该箭头函数的上下文里的 this
const func = (a,b) => a * b;
func(1,2)
```
```js
// test this in arrow function
use strict
function f() {
  const t = () => console.log(this)
  return t;
}
f()() // f 没有调用者，所以 f 函数体内的 this 就是 undefined，t 函数体内的 this 就指向 f 函数体的 this, 即 undefined

f.apply({x: 1})() // {x: 1}, f 的调用者就是 {x: 1}, 所以 t 函数捕获的 this 就是 {x: 1}
f.apply({x: 1}).apply({x: 2}) // {x: 1}, 即使 t 函数经由 {x: 2} 调用，但是 箭头函数实际上是没有 this 的，它函数体内的 this 总是指向定义箭头函数上下文中 的 this, 即 {x: 1}
```
2. self invoke function. the anonymous function has to be surrounded by (), then invoke itself by following ()
```js
(function(){console.log("hello")})()
```
3. object constructor. when function is called after <b>new</b>, the function is object constructor.
the <b>this</b> in implementation refer to the object it create
```js
function Person(firstname, lastname) {
  this.firstname = firstname;
  this.lastname = lastname;
  this.language = "English";
}
var person = new Person("John", "White")
```
4. function has properties and method, its kind of object indeed. but <b>typeof</b> function will return "function"
5. function parameter(declared in function definition) could be different with real arguments(the recevied parameters when invoke)
  * js parameter does not have type declaration
  * js does not do type check when invoke
  * js does not check the number of received parameters
```js
function func(a,b) {
  arguments.length//check the arguments length pass to func
}
func(1), the b will === undefined
```
6. function default value, support since ES5
```js
function func(a=1, b=2) {
  return a * b;
}
func();
```
7. <b>arguments</b> object, contains an array of the arguments used when the function was called

## bind-call-apply
### bind
bind 就是把一个函数 bind 到一个指定的 对象和指定的参数上，返回值则是绑定之后的函数。  
该函数，不管是被谁调用，其调用者都会是绑定的调用者，而不是实际的调用者。参数也会是绑定的参数，传入的参数会赋值给没有被绑定的参数上。  
其原理就是 arguments 里面已经填充了被绑定的参数
```js
function sum(a, b) {
  // arguments 会是 [2, 3, 4, 5, 6]
  console.log(this, arguments)
  return a + b;
}

const bsum = sum.bind({x:1}, 2, 3)
// 函数绑定了 this 在 {x: 1} 上，参数也绑定了 2 和 3
console.log(bsum.apply({x: 2}, [4, 5, 6])) // {x:1} 5
```
### call
有的函数，函数本身并不是对象的一个属性，那怎么把该函数作用于该对象呢？就需要使用 call 方法.  
call 方法的第一个参数，就会作为 this 被引用。后续的参数会被当作参数传入函数。需要注意的是，参数的形式是变长参数。
```js
function sum(a, b) {
  // arguments 会是 [2, 3, 4, 5, 6]
  console.log(this, arguments)
  return a + b;
}

console.log(sum.call({x: 2}, 2, 3, 4, 5)) // {x:2} 5
```
### apply
apply 和 call 基本上一样，区别在于参数的形式，apply 的参数是以数组的形式传递的
```js
function sum(a, b) {
  // arguments 会是 [2, 3, 4, 5, 6]
  console.log(this, arguments)
  return a + b;
}

console.log(sum.call({x: 2}, [2, 3, 4, 5]))
```