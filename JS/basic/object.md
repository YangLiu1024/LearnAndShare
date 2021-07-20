# Introduction to JS Object

1. In JavaScript, except primitive value, all variables are objects.
primitive data types are:
* string
* number
* boolean
* undefined
* bigint
primitive data type has primitive value, primitive value does not contain properties or method, just immutable value
1. objects are assigned with reference, not value.
```js
var person = {}
var x = person;//x and person and same object, will not copy and create new object, they have same address
```
when compare two objects, if (o1 == o2) or if (o1 === o2), return true only when o1 and o2 are same object
3. object is a collection of unordered properties, property can be changed(if writable)/added(if extensible)/deleted(if configuable)
4. <b>delete</b> could delete object property and value. it has no effect on variables or function 
5. when access property by object.property, its equal to object["property"]
```js
var person = {firstname: "john", lastname: "white"}

for (let key in person) {
  //must access by person[key] instead of person.key
  person[key]
}
``` 
6. object getter/setter could give simpler sytax than function
7. object constructor function
```js
//here, Person is a regular function yet
function Person(firstname, lastname) {
  this.firstname = firstname;
  this.lastname = lastname;
  //in this way, the object created could have default value for language property
  this.language = "English"
}
//when called with new, its treated as object constructor method
//the this in implementation will refer to the object it construt
var person = new Person("john", "white");
```

fimilar object constructor: new String(), new Boolean(), new Array(), new Number(), new Date(), new Function() etc.

8. to define a class, could use <b>class</b>
```js
class Person {
  constructor(firstname, lastname) {
    this.firstname = firstname;
    this.lastname = lastname;
  }
  
  func1() {}
  func2() {}
  static func3() {}
}
//usage
var person = new Person("john", "white");
```
9. all js objects inherit properties and methods from a prototype. Date inherit from Date.prototype, Array inherit from Array.prototype,
The Object.prototype is on the top of the prototype inheritance chain.
10. to add properties or method to all existed given type objects, use <b>prototype</b> to change object consturctor.
```js
function Person(firstname, lastname) {
  this.firstname = firstname;
  this.lastname = lastname;
}

var person = new Person("john", "white");
//person.language will be undefined now

Person.prototype.language = "English"//add new property to all existed Person object
//person.language will be "English" now
```
11. enumerate the properties of an object
 1. for...in loop. this loop will traverse all enumerable properties of an object and its prototype chain
 2. Object.keys(o), return an array with all the own(not in the prototype chain) enumerable properties names for an object
 3. Object.getOwnPropertyNames(o), return an array containing all own properties names(enumerable or not) of an object
