Introduction to number

1. toExponential([optional precision]), returns a string, with a number rounded and written using exponential notation. for example, 
var x = 1.546; x.toExponential(1) => 1.5e+0, x.toExponential(2) => 1.55e+0
2. toFixed([optional precision]), returns a string, with the number written with a specified number of decimals. if precision absent, 
treat as 0. for example, var x = 9.645; x.toFixed(0) => 10, x.toFixed() => 10, x.toFixed(1) => 9.6
3. toPrecision([optional precision]), returns a string, with a number written with a specified length, if absent, return original number
for example, var x = 9.645; x.toPrecision() => 9.645, x.toPrecision(1) => 10, x.toPrecision(6) => 9.64500

Global number functions
1. isNaN(v), check if v is NaN or not
2. Number(v), convert v to number, if not numeric, return NaN. 
   Number(true) => 1, Number(false) => 0, Number("  10  ") => 10, Number("10,20") => NaN
3. parseInt(v), return integer part of v
4. isFinite(v)

Number properties
1. Number.MAX_VALUE
2. Number.MIN_VALUE
3. Number.POSITIVE_INFINITY
4. Number.NEGATIVE_INFINITY
5. Number.NaN

note that number properties can not be used on variables. x.MAX_VALUE will return undefined 
