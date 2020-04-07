# Introduction to JS Object

1. In JavaScript, except primitive value, all variables are objects.
primitive data types are:
* string
* number
* boolean
* null
* undefined
primitive data type has primitive value, primitive value does not contain properties or method, just immutable value
2. objects are assigned with reference, not value.
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
