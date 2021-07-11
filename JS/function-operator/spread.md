*Spread* syntax(`...`) allows an iterable such as an array expression or string to be expanded in places where zero or more arguments (for function calls) or elements (for array literals) are expected, 

or an object expression to be expanded in places where zero or more key-value pairs (for object literals) are expected.

# Spread for Array
*Spread* syntax looks exactly like *Rest* syntax, in a way, rest syntax is the opposite of spread syntax.

Spread syntax "expands" an array into its elements, while rest syntax collects multiple elements and "condenses" them into a single elemeent.

```js
function sum(x, y, z) {
    return x + y + z
}

const data1 = [1, 2, 3]
const data2 = [4]
const datas = [...data1, ...data2, 5]//expand the array data1/data2 to elements, then generate a new array
//datas is an array, and will be expanded here. it has 5 elements, but the sum function only require three parameters, so the first three elements will be used
console.log(sum(...datas))
```

With *Spread* syntax, 
```js
let parts = [1, 2]
let lyrics = [3, ...parts, 4, 5]//its easy to use existed array as one part of new array

let parts2 = [...parts]//parts2 will be an new array based on parts
```

And note that *Spread* syntax effectively goes one level deep while copying an array. Therefore, it maybe unsuitable for copying multidimensional arrays
```js
let a = [[1], [2], [3]]
let b = [...a]

b.shift().shift()//b will be [[2], [3]]

a// a will be [[], [2], [3]], the first element is affected as well
```

# Spread for Object
*Spread* for object will copy own enumerable properties onto a new object.
```js
function merge(...objs) {//rest syntax
	return {...objs}//spread array into object
}
function merge2(...objs) {
	return objs.reduce((prev, curr) => ({...prev, ...curr}))//merge object into one object using spread
}
let o1 = {x: 1, y: 2}
let o2 = {y: 3, z: 5}

let o3 = {...o1} //copy object
console.log(merge(o1, o2))//Object { 0: Object { x: 1, y: 2 }, 1: Object { y: 3, z: 5 } }
console.log(merge2(o1, o2))//Object { x: 1, y: 3, z: 5 }
```
