Introduction to basic knowledge of JS

1. <script> could be placed in both <head> and <body> section, and its better to place at bottom of <body> to improve display speed
2. always coding with strict mode
3.  <b>class</b> definition
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
  static func3() {}
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
4.  <b>extends</b> definition. 
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
5.  Object methods
  - Object.defineProperty(object, property, descriptor) or Object.defineProperties(object, descriptors)
  ```js
  var person = {
    firstname: "John",
    lastname: "White"
  };
  Object.defineProperty(person, "language", {
    //property descriptor
    value: "ENG",
    writable: true,//if could change value
    enumerable: false,//if could be enumerable, such as Object.keys(object)
    //if configuable false,
    //1. could not delete this property
    //2. enumerable could not be changed any more
    //3. writable could change from true to false, could not change from false to true
    configuable: true
  })
  ```
  - Object.getOwnPropertyDescriptor(object, property), return the descriptor
  - Object. getOwnPropertyNames(object), return all owned properties as array
  - Object.keys(object), return all enumerable properties as array
  - Object.preventExtensions(object), prevent adding properties to object
  ```js
  var person = {
    firstname: "john"
  };
  Object.preventExtensions(person);
  person.lastname = "white"//now allowed, this line will cause error
  ```
  - Object. isExtensible(object), check object is extensible or not
  - Object.seal(object)
    * prevent adding property to object
    * all existing properties become non-configuable
  - Object.isSealed(object), check if object is sealed
  - Object.freeze(object), prevent any change to object
  - Object.isFrozen(object)
  - Object.create(object), create a new object and using the parameter as __proto__, eg, let o2 = Object.create(o1), then o2.__proto__ = o1

5. Object 的 property descriptor 都只是描述属性本身的一些配置，比如 enumerable, configurable, writable/value, get/set. 但是有的时候，需要对 对象 本身的行为做出限制。  
   这些限制就包括 
   * Object.preventExtensions(), 阻止对象添加新的 属性
   * Object.seal(), 在 preventExtensions() 的基础上，再把所有属性的 configurable 都改为 false
   * Object.freeze(), 阻止任何操作，包括添加，删除，修改属性
6.  nullish operator
    the operator `??` work similar with `||`, but it only return second expression when the first one is `null` or `undefined`.
    note that `0`, `''` could be regard as valid value in most case, but `0 || 1` will return 1, `0 ?? 1` will return 0
  
