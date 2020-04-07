Introduction to basic knowledge of JS

1. <script> could be placed in both <head> and <body> section, and its better to place at bottom of <body> to improve display speed
2. place js code in external javascript file, then reference in html, such as 
  - <script sre="demo.js">
  - <script src="https://my.learn.share.com/demo.js">
  - <script src="/src/demo.js">. note that "/src/demo.js" refer to the root dir of current web
3. built-in methods
  - document.getElementById("id").innerHTML = ""
  - window.alert("")
  - console.log("")
  - window.print() print current window
4. a variable declared without value, will have default value <b>undefined</b>. if re-declared the variable, its value will be kept
5. js comparison. note that when compare number and string, always convert string to number. empty text => 0, non-number text convert to NaN. when compare with NaN, always return false. when compare two objects, always return false, because they are different object.
  - <b>==</b> equal value
  - <b>===</b> equal value and equal type
  - <b>!=</b> different value
  - <b>!==</b> different value or different type
6. the difference between null and undefined
  - if a variable declared without value, its has default value <b>undefined</b>
  - null could be asigned to a variable as a kind of special value
  - null == undefined => true, null === undefined => false. because typeof null => object, typeof undefined => 'undefined'
7. js data types
  - string
  - number
  - boolean
  - object note that for array, typeof array still return object
  - function
  - undefined
8. js operator precedence, list in descend order
  - () expression grouping
  - . [] () new  member access operator and function call and creator
  - postfix ++ --
  - prefix ++ -- ! typeof 
  - **  Exponentiation
  - \* / %
  - \+ -
  - shift operator >> << >>>
  - <= >= > < instanceof in
  - == === != !==
  - other...
9. access object property by obj.propname or obj["propname"], access object method by obj.func()
10. this in function refer to the function owner
11. js common events in html: onclick, onmouseover, onmouseout, onkeydown, etc...
12. js array.splice(-1, 1) will delete element from ending
13. string in numeric operation will convert to number auto, for example, "100" - "10" => 90
14. NaN is a number, typeof NaN => "number". but all logic operations with NaN will return false, even NaN == NaN. to check if a value is NaN, call global function isNaN(val)
15. Infinity is a number, typeof Infinity => "number". the value outside of possible range will be Infinity or -Infinity
16. the difference between array and object
    - array use numbered index, object use named index
    - array is a special kind of object
17. Math.PI, Math.round(), Math.ceil(), Math.floor(), Math.pow(x, y), Math.abs(), Match.sqrt(), Math.sin(), Math.cos(), Math.max(),
  Math.min(), Math.random() return [0, 1)
18. Boolean(var) => true when var has value, false when var has not value.
  0, "", -0, null, undefined, NaN, false => their Boolean(v) is false
19. variable scope ccould be declared as local(declared in function) and global(out of function). local variable could only be accessed within function. if a variable is used without declared before, regard as declared as global. And global variable can be accessed by all
scripts and all functions on the web.
20. switch statement use strict match ===, and need to add break for each case
21. js loop
    - for (statement1; statement2; statement3), general for sentence
    - for key in object, loops through the keys of an iterable objects
    - for value of object, loops through the values of an iterable objects
    - while and do/while
22. typeof always return string
23. js regex modifiers
  - i, ignore case
  - g, find all match instead of first
24. regex object methods
  - test, for example, /e/.test("abcdef") => true, because the text contain e
  - exec, return the matched text as object
25. try {} catch (err) {} finally {}. the err thrown by js itself always has properties "name" and "message"
26. user can throw customized error. it could be string, number, boolean, object. and the catch will catch what you throw
27. js will move all "var" variables declaration to the top of current script or function, it means you can use variable before you declare it. this behavior call "hoist". note that it only hoist declaration, not including intialization.
28. strict mode is declared by adding "use strict" to the beginning of script(all code will be executed in strict mode) or function(the code in function will be executed in strict mode)
29. this referred to the owner object when used in object method, refer to the window(undefined in strict mode) when used in function
  ```js
  var person {
    firstname: "John",
    lastname : "White",
    fullname : function() {
      return this.firstname + lastname();
    }
    fullname : () => {return this.firstname + this.lastname}
  }
  person.fullname()//John White for normal function, the this will refer to the person
  person.fullname()//undefined undefined for arrow function, the this refer to the window
  ```
  
30. ES5 support global scope and function scope for variable. for ES6, support block scope.
  - var variable could have global scope and function scope, if declared in block, still have global scope
  - let variable could have global scope and function scope and block scope. when declared in block, it will shadow outside variable which has same name, and the block scope variable could not be aceessed by outside of block
  - redeclaring var variable will override existed declaring, redeclaring let variable will not
  ```js
  var x = 1;
  {
    var x = 2;
  }
  //x = 2 here
  let y = 1;
  {
    let y = 2;
  }
  //y = 1 here, the y in block shadow the y outside
  ```
  - global var and global let are alomost same, function var and function let are almost same
31. the const variable behavior like <b>let</b>, except that the const variable could not be reassigned. it means the variable itself is const, but the value it referred is changable. same concept as "pointer constant", the pointer is constant, but its value is not.
32. const and let does not support hoist. refer to #27
33. - <b>this</b> in general function always refer to the caller of the function, the window, the document, the object, the button, or whatever
    - <b>this</b> in arrow function always refer to the owner(who define the function) of the function
    ```js
    //regular function, the this always refer to the caller
    hello = function() {
       document.getElementById("demo").innerHTML += this;
    }
    //arrow function, the this always refer to the owner
    hello = () => document.getElementById("demo").innerHTML += this;

    window.addEventListener("load", hello); //refer to the window for both regular and arrow function
    window.getElementById("btn").addEventListener("click", hello)//refer to the button for regular function, refer to window for arrow function
    ```
34. regular function definition for object method or class method
   ```js
   var person = {
     hello: function() {return "hello"}
     //equal to
     hello() {return "hello"}
   }
   ```
35. <b>class</b> definition
```js
class Car {
  //the constructor, required, if not declared, compiler will add default empty constructor
  //the constructor will be invoked when new instance auto
  constructor(name) {
    //init the properties
    this._carname = name;
  }
  func1() {}
  func2() {}
  //getter and setter
  get carname() {return this._carname}
  set carname(name) {this._carname = name}
  //static method
  static func3() {//could not use this in static method}
}
//usgae
var mycar = new Car("Ford");
//getter, no need ()
mycar.carname
//setter
mycar.carname = "qq"
//static, should be called by class instead of instance
Car.func3()
```
36. <b>extends</b> definition. 
```js
class Model extends Car {
  constructor(name, mod) {
    super(name)
    this.model = mod;
  }
  
  func4() {}
}
//usage
mod = new Model("volvo", "mustang");
mod.func1();
mod.func4();
Model.func3()
```
37. avoid global variable, avoid <b>new</b>, avoid <b>==</b>, avoid <i>eval</i>
  - global variable could be accessed and overwrite by all scripts of this web page
  - avoid new Boolean(), new String(), new Array(), new Function(), new Object()...
  - <b>==</b> always convert to matching types before do comparision
  ```js
  0 == "" //true
  1 == "1" //true, note that Boolean("asd") => true
  1 == true //true
  
  0 === "" //false
  1 === "1" //false
  1 === true //false
  ```
  - <i>eval</i> execute arbitrary text as code which is not safe
