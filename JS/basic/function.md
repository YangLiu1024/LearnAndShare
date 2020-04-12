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
//for regular function, <b>this</b> refer to the global obect, such as window
//for object method, <b>this</b> refer to the caller
const func = (a,b) => a * b;
func(1,2)
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
