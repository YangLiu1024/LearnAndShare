# Type Script
TS 其实就是为 JS 加入了静态类型检查，在编译阶段，就会检查出类型错误。但是它并没有改变 JS 运行时的特性，它是完全兼容 JS 的。  
TS 增强了 IDE 的功能，提供代码补全，接口提示，跳转到定义，代码重构等能力，适用于大型项目，可以帮助减少运行时错误，增强可维护性。  

# JS 类型
JS 类型分为原始数据类型，number, string, boolean, null, undefined 以及对象类型。 由对象类型，又派生出 Function, String, Boolean, Number 等类型。  
null 和 undefined 是所有类型的子类型，即 null 和 undefined 可以赋值给任意类型。 但是在 strict 模式下，null 和 undefiend 不可以赋值给 concret type

# any 类型
如果是普通类型，是不允许更改类型的，即一个已经声明了具体类型的变量，不可以被赋值为其它类型的值。但是对于 any 类型，就和没有静态类型一样，可以做任何操作。如果一个变量在声明的时候，没有指定其类型，也没有被赋值，那么就会被识别为 any 类型。 
# 类型推导
如果变量在声明的时候，没有声明类型，但是有赋值，那么就会根据值来进行类型推导。
# 联合类型
联合类型表示变量可以具有多种类型中的一种，联合类型用 <code>|</code> 来分隔每个类型。联合类型在不确定类型的时候，只能访问联合类型所有类型的共有属性和方法，比如
```js
function f(v: string|number) {
    v.length//会报错，因为 number 并不具备该属性
    v.toString()//可以通过
}
```
但是联合类型变量在被赋值时，就可以通过类型推导推断出一个类型。
# 对象的类型-- interface/type
## 确定属性
interface 通常用于对类的一部分行为进行抽象，也用于对对象的结构进行描述。
```js
interface Person {
    name: string;
    age: number
}
```
*Person* 类型定义了对象的结构，所有 *Person* 类型的对象都必须具有 name 和 age 属性且不能包含其它属性。  
## 可选属性
有的时候，我们并不确定一个属性是否必须存在，那么就可以使用可选属性语法。可选属性表示该属性是 optional.    
```js
interface Person {
    name: string;
    age: number;
    email?: string;
}
```
## 任意属性
有的时候，我们也不知道属性名是什么，那么就可以使用任意类型
```js
interface Person {
    name: string;
    age: number,
    [key: string]: string|number
}
```
但是需要注意的是，一旦使用了任意类型，其它确定属性和可选属性的类型都必须是任意类型的子类型，比如上例中，因为 age 是 number 类型，name 是 string 类型，那么任意类型 key 就必须是 string|number。  
同时，一个类型定义里只能有一个任意类型。  
## 只读属性
有的时候，我们希望一个属性是只读的，那么可以为属性类型定义加上 *readonly*
```js
interface Person {
    readonly id: number;
    name: string;
    age: number,
    [key: string]: string|number
}
```
如果该 readonly 属性是确定属性，则表示该属性必须在创建对象的时候就初始化该属性. 如果 readonly 属性是可选属性，那么在创建对象时，如果没有初始化，那么之后也不再允许被赋值。  
## interface vs type
1. type 就是 data types 的集合，interface 更像是一种 syntax
2. type 不允许在同一作用域定义相同名字的 type, interface 可以，并且自动merge
3. interface 更倾向于被 继承或者实现，而 type 只是简单的 type 定义
# 数组类型
TS 里数组类型的定义很简单，最方便就是*类型 + 方括号*，比如 number[], 就表示 number 的数组。当然也可以使用数组泛型，比如 ***Array\<number>*** .   
数组里不允许出现其它类型的元素。
# 函数类型
## 函数声明
函数有输入输出，需要对输入输出都做出类型定义
```js
function f(x: number, y: number): number {//定义参数类型和返回值类型
    return x + y;
}
```
TS 要求必须传入和类型定义一样的参数，不能多也不能少。但是在 JS 里，这是被允许的。
```js
function sum(x, y) {
    return x + y;
}
sum(1)// 1 + undefined => NaN
sum(1, '1')//1 + '1' => '11'
sum(1, 2, '3')//3

// actually, JS will wrap all passed arguments in an array-like object
type IArguments = {
    [index: number]: any;
    length: number;
    callee：Function;
}
//上例 sum 中实际传入的所有参数都会包装进 IArguments 类型的对象里
```
## 函数表达式
除了函数声明，我们有时候也使用函数表达式.它的本质是定义一个匿名函数，然后将这个函数赋值给一个变量
```js
const f = function (x, y) {
    return x + y;
}
```
那么在 ts 里，我们就需要对变量和函数都作出类型定义
```js
const f：(x: number, y:number) => number = function (x:number, y:number):number {
    return x + y;
}
```
注意这里 箭头 => 在 TS　里的应用，它表示函数类型定义里的返回值，它的作用和箭头函数不同。  
并且使用 type 和 interface 定义函数还有一点区别
```js
//使用 type 定义函数
type FF = (x: number, y: number) => number
//使用 interface 定义函数
interface FF {
    (x:number, y:number): number
}
```
但是我们一般不使用 interface 定义函数类型
```js
interface F {
    (x:number, y:number): number
}

interface FF {
    sum(x:number, y:number):number
}

let a:FF = {sum: (x:number, y:number) => x + y}
let b:F = a.sum

console.log(b(1, 2))//3
```
## 函数可选参数
函数的参数有时候也可以是可选的，和可选属性类似，用 <code>?</code>修饰即可。  
需要注意的是，可选参数之后不能再有确定参数。  
```js
type FF = (x:number, y?:number, z?:number):number
```
## 函数参数默认值
ES6 支持对函数参数添加默认值，当对应传入的参数为 undefined, 则会使用设置的默认值.  
```js
type FF = (x:number, y:number = 5, z?:number):number
```
## 剩余参数
ES6 解构语法支持使用 ...rest 的方式获取函数接收的剩余参数
```js
type FF = (array: any[], ...rest: any[]) => void
```
## 重载
TS 允许对同一函数进行多次声明，但是最后一次声明必须进行实现。不同次的声明可以用来精确函数声明匹配，TS 会优先从最前面的函数声明开始匹配，所以如果函数定义有包含关系，要优先把精确的定义写在前面。  
比如，一个函数可以处理数字和字符串，如果输入是数字，输出反转数字，如果是字符串，输出反转字符串
```js
function reverse(x: number): number;
function reverse(x: string): string;
function reverse(x: number | string): number | string | void {
    if (typeof x === 'number') {
        return Number(x.toString().split('').reverse().join(''));
    } else if (typeof x === 'string') {
        return x.split('').reverse().join('');
    }
}
```
前两个函数声明会优先尝试匹配，在编辑器里我们可以得到提示
# 类型断言
类型断言用来手动指定一个值的类型，形式为 *值 as 类型*. 它的作用在于让编译器将值的类型认定为我们指定的类型来进行编译，但是需要注意的是，它只能够欺骗编译器，在运行时，如果类型不匹配，还是会报错
```js
function f(x:number|string):void {
    if (typeof x.length === 'number') {
        console.log(x.length)
    }
}
```
上面的写法会报错，即使我们检查了 x.length 的type, 运行时也不会报错，但是编译器还是会认为 x 是 string|number, 那么就只能访问 string 和 number 的共有属性和方法。  
为了解决这个问题，就可以使用类型断言。
```js
function f(x:number|string):void {
    const xx = x as string
    if (typeof xx.length === 'number') {
        console.log(xx.length)
    }
}
```
在使用断言的时候要慎重，即使指定了类型，也需要做进一步检查。总结：
1. 联合类型可以被断言为其中一个类型
2. 父类可以被断言为子类， 先断言，再尝试判断值是否含有子类属性，如果有，则确定为该子类
3. 任何类型都可以被断言为 any，为了在某些情况下改变一个值
4. any 类型可以被断言为任意类型， 将 any 断言为更精确的类型来避免 any 无限制传播
5. 只要 A 类型兼容　B 类型，或者B 类型能够兼容 A 类型，则能互相断言

A兼容B的意思是 B 包含 A 所有的属性。
# 类型断言和类型转换
需要注意的是，类型断言，只是告诉编译器，你把这个变量当作这种类型来编译，但是所有的断言语句编译后都会被删除，它并没有真正改变变量的实际类型。如果需要类型转换，则必须调用类型转换的方法。 
# 类型断言和类型声明
```js
interface Animal {
    name: string;
}
interface Cat {
    name: string;
    run(): void;
}

const animal: Animal = {
    name: 'tom'
};

let tom = animal as Cat;//通过
let tom: Cat = animal;//报错
```
因为 Animal 和 Cat 兼容，那么 animal as Cat 是合法的，但是对于类型声明，因为 tom 被声明为 Cat 类型，但是 animal 是 Animal 类型，当然不能将父类实例赋值给子类变量。  
可见，类型声明是比类型断言更加严格的，我们应该优先使用类型声明。
# 泛型
当我们对函数函数值的类型依赖于外部输入时，就可以使用泛型
```js
function getCache<T>(key: string): T {
    return (window as any).cache[key]
}

getCache<Animal>("animal")// 返回 Animal 类型
getCache<Cat>("tom")// 返回 Cat 类型
```