The *Generator* object cannot be instantiated directly, it can only be returned from a *Generator Function*

A *Generator Function* declaration(*function* keyword followed by an asterisk)

# Generator Methods
* next() => return a value yielded by the `yield` expression
* return(value) => return the given value and finish the generator
* throw() => throw an error to a generator, also finish the generator, unless caught from within that generator

# Example
```js
function* infinite() {
    let index = 0;

    while (true) {
        yield index++;
    }
}

const generator = infinite(); // "Generator { }"

console.log(generator.next().value); // 0
console.log(generator.next().value); // 1
console.log(generator.next().value); // 2
//if execute generator function again, the value list will start scratch
console.log(infinite().next().value); // 0
```

执行 *Generator Function* 并不会执行它的 body code directly，而是返回一个可迭代对象。当调用该迭代对象的 `next()` 方法时，才会执行该 *Generator Function* body code, 直到碰到一条 `yield` 表达式，然后将该表达式的值作为 `next()` 的返回值的 `value` property, 并且该返回值还包含一个 `done` boolean property 来标志该生产函数是否已经产出它最后一个值。生产函数里的 `return` 语句，会结束该生产函数。也就是说，当执行 `next()` 时，碰到 `return` 语句，则该 `next()` 的返回值将是 `{value:${value}, done:true}`.其中的value 则是 `return` 语句的返回值。 当一个生产函数已经 finish，则之后的 `next()` 调用不会执行任何 body code, 而是直接返回 `{value:undefined, done:true}`


# yield
the `yield` keyword is used to pause and resume a *Generator Function*
```js
//expression define the value to return from the generator function, if omitted, return undefined
//retrieve the optional value passed to the generator's next() method to resume its execution
[rv] = yield [expression]
```
`yield` 只能在包含它的生产函数里被直接调用，不能被 内嵌函数或者 callback 函数里被调用

一旦生产函数 pause在一个 yield 语句上，整个生产函数就会 pause 直到下一次的 `next()` 被调用。 当`next` 被调用时，生产函数就会从上一次 pause 的地方(yield 之后的代码)继续开始执行。并且可以给 `next()` 方法传递一个参数，该参数会作为 `yield` 表达式的返回值
```js
function* counter(value) {
 let step;

 while (true) {
   step = yield ++value;

   if (step) {
     value += step;
   }
 }
}

const generatorFunc = counter(0);
console.log(generatorFunc.next().value);   // 1
console.log(generatorFunc.next().value);   // 2
console.log(generatorFunc.next().value);   // 3
console.log(generatorFunc.next(10).value); // 14
console.log(generatorFunc.next().value);   // 15
console.log(generatorFunc.next(10).value); // 26
```

# yield*
the `yield*` expression is used to delegate to another *Generator Function* or iterable object
```js
function* func1() {
  yield 1;
  yield 2;
  return 4;
}

function* func2() {
  yield 3
  //yield* func1() 会依次迭代 func1 中 yield 的值
  //yield* func1() 表达式本身的值是 func1 结束时返回的值
  yield yield* func1();
  return 5;
}

const g = func2();

console.log(g.next());//3, false
console.log(g.next());//1, false
console.log(g.next());//2, false
console.log(g.next());//4, false
console.log(g.next());//5, true
```