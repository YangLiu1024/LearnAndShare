The *destructuring assignment* syntax is a JavaScript expression that makes it possible to unpack values from arrays, or properties from objects, into distinct variables.

# Array Desctructuring
```js
let arr = [1, 2, 3, 4, 5]

let a, b, rest
//a variable can be assigned its value via destructuring, separate from the variable's declaration
[a , b] = arr//a is 1, b is 2

[a, b, ...rest] = arr //a is 1, b is 2, rest is [3, 4, 5]

//and a variable can be assigned a default, in the case that the value unpacked from the array is undefined
[a, b = 2, c] = [1]// a is 1, b is 2, c is undefined

//swap value
[arr[2], arr[1]] = [arr[1], arr[2]]//the arr will be [1, 3, 2, 4, 5]

//ignore some element
[a, , b] = arr

```
# Object desturcturing
```js
let obj = {x: 1, y : 2, z: 3}

const {x, y, ...rest} = obj// x is 1, y is 2, rest is an object {z: 3}

//assign new variable name(take from the object 'obj' the property named 'x' and assign it to a local variable named 'xx')
const {x: xx, y: yy} = obj//xx is 1, yy is 2

//default value, in case that the value unpacked from the object is undefined 
const {x, y = 5} = {x: 3}

//assign new variable and set default value
const {x: xx, y:yy = 5} = {x: 3}

//set a function parameter's default value
function drawChart({size = 'big', coords = {x: 0, y: 0}, radius = 25}={}) {
  console.log(size, coords, radius);
  // do some chart drawing
}
//could pass zero parameter because has defined default value(empty object {}) for the parameter
//and when do destructuring, has set the default value for each property
drawChart();

drawChart({
  coords: {x: 18, y: 30},
  radius: 30
});

//access nested object property
const user = {
  id: 42,
  displayName: 'jdoe',
  fullName: {
    firstName: 'John',
    lastName: 'Doe'
  }
};
//for nested object, should wrap the destructuring into curly brace
//you can only access 'displayName' and 'name', for the fullName/firstName here, they are not defined variable actually 
const {displayName, fullName: {firstName: name}} = user

//for nested object and array, should obey same format
const metadata = {
  title: 'Scratchpad',
  translations: [
    {
      locale: 'de',
      localization_tags: [],
      title: 'JavaScript-Umgebung'
    }
  ],
  url: '/en-US/docs/Tools/Scratchpad'
};

let {
  title: englishTitle, // rename
  translations: [
    {
       title: localeTitle, // rename
    },
  ],
} = metadata;

console.log(englishTitle); // "Scratchpad"
console.log(localeTitle);  // "JavaScript-Umgebung"

//for of iteration and destructuring
const people = [
  {
    name: 'Mike Smith',
    family: {
      mother: 'Jane Smith',
      father: 'Harry Smith',
      sister: 'Samantha Smith'
    },
    age: 35
  },
  {
    name: 'Tom Jones',
    family: {
      mother: 'Norah Jones',
      father: 'Richard Jones',
      brother: 'Howard Jones'
    },
    age: 25
  }
];

for (const {name: n, family: {father: f}} of people) {
  console.log('Name: ' + n + ', Father: ' + f);
}

//computed object property names and destructuring
let key = 'z';
let {[key]: foo} = {z: 'bar'};

console.log(foo); // "bar"
```
