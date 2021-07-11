the *rest* parameter syntax allows a function to accpet an indefinite number of arguments as an *Array*
```js
function sum(...args) {
    return args.reduce((prev, curr) => prev + curr);
}

sum(1, 2, 3)// 6
sum(1, 2, 3, 4)// 10
```

Note that only the last parameter of a function definition can be prefixed with `...`, and the rest parameter will be an *Array*. 

for the `arguments` object within function, its *not* an *Array*.

If the *rest* parameter does not receive any parameter, it will be an empty array.