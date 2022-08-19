# 数据类型
JS 里面也有内置的数据类型。number, string, boolean, undefined, null, object.  
## number 
number 就包含了整数和浮点数，以及特殊值 NaN.
## string
string 就是字符串，空字符串会稍显特殊一些 
## boolean
boolean 就只有 true 和 false 的字面值，任何值都可以转换为 boolean.  
非空字符串转换为 true, 空字符串转换为 false  
0 和 NaN 转换为 false, 其它的都转换为 true 
## null & undefined 
* null 是特殊的 object, 表示空指针对象。typeof null 会返回 'object'. 一般将 null 用作变量的初始值。  
* undefined 表示未定义的值，typeof undefined 会返回 'undefined'. 如果变量仅仅被声明，但是没有被赋值，那么它的值就是 undefined, 访问对象不存在的 key, 也会返回 undefined.  
* undefined 其实派生自 null, null == undefined 会返回 true.  
* 在将 null/undefined 转换为字符串时，null 会转为 'null', undefined 会转为 'undefined'  
* 在转换为 number 时，null 会转为 0, undefined 转为 NaN
* null 是 JS 里的关键字，undefined 是 挂载在 window 上的全局变量
* 在将 null 或 undefined 转换为对象时，都会抛出 TypeError, 比如 Object.getPrototypeOf(null)

# Number
## 进制
* 八进制数必须以 0 开始，且每位数字都在 0-7 内，如果满足，则当作八进制。
* 十六进制必须以 0x 开始，且其他位必须在 0-9，a-f 之间，如果超出，则抛出 syntax error
## 转换
* boolean 类型，true 转为 1， false 转为 0
* null 转为 0
* undefined 转为 NaN
* 空字符串转为 0，非空则根据内容来转换，如果内容不合法，则转为 NaN
* 在将对象转为数字时，会稍微复杂一些

## 转换方法
### Number(any)
将参数转换为十进制数, 如果不能转换，则返回　NaN
### parseInt(string, radix) -> 将参数按照指定基数，解析出 number, 并取整数部分　　
* 如果参数不是字符串，则会先转换为字符串。如果是数字，则会先转换到十进制，再转换为字符串
* 根据指定基数，从开开始截取参数中的合法部分，遇到非法字符，则丢弃后续部分，将合法部分转换为 Number
* 如果传入的参数是科学计数法表示的 Number, 则会先转换为十进制数，再转换为字符串，再根据基数进行解析
* 如果传入的参数是浮点数，则直接取整返回

一个数组 map 以及 parseInt 结合使用的问题，
```js
const a = ['1', '2', '3', '4']
const n = a.map(parseInt) 
// 返回值会是 [1, NaN, NaN, NaN]，看起来比较不可思议
// 其实根本原因在于 a.map(parseInt) 的写法，会将 map 每一次迭代的所有参数，都传给了 parseInt, 相当于调用了 (value, index, arr), arr 会被 parseInt 丢弃
parseInt('1', 0);// 任何整数以0 为基数取整时，返回本身
parseInt('2', 1);// 基数的取值范围为 2-36， 1 不合法，返回 NaN
parseInt('3', 2);// 2 进制只能有 0、1，3 不合法，返回 NaN
parseInt('4', 3);// 3 进制只能有 0、1、2， 4不合法，返回 NaN
```
### parseFloat()
parseFloat 没有进制的限制，简单的解析字符串，只允许出现科学计数法的 e.

### Number vs parseInt vs parseFloat
Number(v) 作用于传入参数的整体，如果能将整体转换为数字则转换，如果不能，则返回 NaN.  
parseInt 有进制的概念，且试图从字符串中解析出合法部分，然后转化为 number.  
parseFloat 没有进制的概念，支持科学计数法，从字符串中解析出合法部分，然后转化为 number.  

## NaN
NaN - not a number, 在期待一个数字，但是数字不合法的情景下使用。比如 0/0, 将返回 NaN, 程序可以继续运行。  
任何涉及到 NaN 的操作都会返回 NaN, 且 NaN 于任何值都不相等，即使于它自己相比。
```js
NaN == NaN // false
``` 
判断 number 是否为 NaN 有两个方法，ES5 提供了 isNaN 方法， ES6 提供了 Number.isNaN 方法。  
两者的区别在于，isNaN 会先尝试将参数转化为数字，能够能够转换，则返回 false, 如果不能转换，则返回 true, isNaN('a') 会返回 true.  
因为 isNaN 的实现带有误导性，所以 ES6 引入了新的方法，Number.isNaN 只会在参数确实为 NaN 时返回 true.  

# String
创建 字符串有三种方式，一种是直接声明字符串字面量，比如 const a = 'hello'.  还有就是使用 String() 方法，第三种是使用 new String().  
前两者都是返回基本字符串，第三种返回字符串对象。  
一般来讲，字符串字面量没有字符串对象的方法，但是为什么可以执行 'hello'.indexOf() 呢？ 是因为 JS 自动做了转换，将字面量转换为对象。  
# typeof
typeof undefined 会返回 'undefined', typeof null 会返回 'object', function 会返回 'function'.  
null 只需要特殊，是因为 JS 中，数据类型都会使用 3 bit 来表示，000 表示 object, 而 null 在大多数平台下面的值都是 0x00, 所以 typeof null 会判断为 object.  
# toString() & valueOf()
每个对象都有 toString 和 valueOf 方法，这两个方法常用在在类型转换。toString 表示的是对象的表现，valueOf 表示的是对象的原始值。  
数组 valueOf 返回数组本身，函数 valueOf 返回函数本身。
```js
[] == 0// true, 首先进行类型转换，尝试将对象转换为数字，那么首先调用对象的 valueOf 方法，数组会返回数组本身，但是数组并不能转换为数字，所以尝试 toString 方法。空数组 toString 会返回空字符串，空字符串转换为 0
[1] == 1//true, 与上例类似
[2] == 2// true， 与上例类似
```
