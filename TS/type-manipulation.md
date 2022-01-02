# Type Manipulation
在 TS 里，类型本身也是一个对象，也是可以操作的。

# keyof
keyof 用来获取一个对象类型的 key 的 union type. key 本身可以是 string | number
```js
type Point = {
    x: number;
    y: number;
    4：string
}

type KP = keypf Point;//type KP = "x" | "y" | 4

//如果类型声明里，使用了 index signature, keyof 会返回 key 的 type
type Arrayish = { [n: number]: unknown };
type A = keyof Arrayish;// type A = number

type Mapish = {[k: string]: unknown}
type KM = keyof Mapish// type KM = string | number
//这里之所以有 number, 是因为 JS 里对象的key 其实最后都会转换为 string, 比如 p[0], 等价于 p["0"]
```

# typeof
JS 本身已经内置了 typeof 关键字，可以返回表达式的值的类型，比如 string|number|boolean|object|function|undefined 等。  
但 JS 里的 typeof 是在运行时执行，但在 TS 里，typeof 用来返回表达式值的类型，在编译时使用。
```js
function f() {
  return { x: 10, y: 3 };
}
//这里的 typeof 就会返回 f 的类型声明，() => {x:number;y:number}
type P = ReturnType<typeof f>;// type P = {x: number; y: number}
```

# Indexed Access Types
我们可以使用索引访问类型，需要注意的是，索引本身需要是 type
```js
type Person = { age: number; name: string; alive: boolean };
type Age = Person["age"];//type Age = number
type T1 = Person["age" | "name"]//type T1 = number | string
type T2 = Person[keyof Person]//type T2 = number | string | boolean

type AliveOrName = "name" | "alive"
type T3 = Person[AliveOrName]// type T3 = string| boolean

//对于数组，如果想获取数组元素的类型
const MyArray = [
  { name: "Alice", age: 15 },
  { name: "Bob", age: 23 },
  { name: "Eve", age: 38 },
];
//这里的 number 也可以替换为任意数字
type P = typeof MyArray[number]//type P = {name: string; age: number}, 如果数组元素类型不一致，会返回所有元素类型的 union

//有的时候，我们想获取一个对象特定key 的 value 的 type,
type NameOf<T> = T["name"]//这个时候 TS 会报错，因为 T 可能没有属性 name
//这个时候就需要对　T 做出约束
type NameOf<T extends {name: unknown}> = T["name"]
//但有的时候，我们不想对 T 做出约束，我们就想让 NameOf 可以作用于任意类型，为了解决编译错误
//可以把约束挪到外面，然后在约束不满足时，返回 never 类型。
type NameOf<T> = T extends {name: unknown} ? T["name"] : never
```
# Conditional Types
condition type 和 JS 里的条件表达式类似，只是 condition type 是作用于 类型 而已。
```js
interface A {}
interface B extends A {}
type T = B extends A ? number : string
```
上述的案例很简单，体现不了条件类型的用处。下例展示了在函数重载的情况下，条件类型的使用
```js
//如果不使用条件类型，函数重载我们需要声明很多次
function f(id: number): A
function f(id: string): B
function f(id: number|string): A | B

//使用条件类型
type Res<T extends string|number> = T extends number ? A : B
function f<T extends string|number>(id: T): Res<T>//现在就只需要声明一个函数即可

f(1)//会自动匹配到 f(id:number) => A
f("2")//会自动匹配到 f(id:string) => B
f(Math.random() ? "hello" : 10)// f(id: string|number) => A|B
```
现在我们想传入一个参数，如果是数组，则返回数组元素类型，如果不是，则原样返回
```js
//这里我们使用 T[number] 来获取数组的元素的类型
type Flatten<T> = T extends any[] ? T[number] : T
//除了这个方法，我们还可以使用 infer 关键字. infer　主要用于在类型推断时，如果满足条件，则将推导出的类型赋值给 infer 后跟的类型名
type Flatten<T> = T extends Array<infer Item> ? Item : T
```
当条件类型作用于泛型，且泛型本身是联合类型时，则条件类型将会变为分布式的
```js
type ToArray<T> = T extends any ? T[] : never
//当 T 是联合类型时，比如 string|number, 会相当于 ToArray<string> | ToArray<number>
ToArray<string|number>//会返回 string[] | number[] 而不是 (string|number)[]
//如果不想要这种默认行为，可以在条件表达式里，将 extends 左右两边的类型都使用中括号包起来
type ToArray<T> = [T] extends [any] ? T[] : never
ToArray<string|number>//这时就会返回 (string|number)[]
```
# Mapped Types
我们通常也可以从一个类型映射到另一个类型，通常使用 keyof Type 来得到类型 Type 的 key
```js
type OptionsFlags<Type> = {
  [Property in keyof Type]: boolean;
};

type PersonFlags = OptionsFlags<{name:string;age:number}>//返回 {name: boolean; age:boolean}
```
TS 还支持 map modifier, 可以 +, 也可以 -
```js
type CreateMutable<Type> = {
  -readonly [Property in keyof Type]: Type[Property];//去掉 Type　里面所有属性的 readonly 的修饰
};

type Concrete<Type> = {
  [Property in keyof Type]-?: Type[Property];//去掉所有属性的 optional 修饰
};
```
还支持 key 的重命名
```js
type Getters<Type> = {
    //Capitalize<string & Property> 要求 Property 本身是字符串，如果是 number, number & string 会返回 never, 那么该 Property 会被过滤掉
    // type T = {
    //   1: number;
    //   a: string;
    // }
    // Getters<T> 里，Property 1 则会被过滤掉，返回类型里只有 a, 因为 1 本身的类型是 number
    [Property in keyof Type as `get${Capitalize<string & Property>}`]: () => Type[Property]
};
 
interface Person {
    name: string;
    age: number;
    location: string;
}
 
type LazyPerson = Getters<Person>;//返回 {getName: () => string; getAge: () => number; getLocation: () => string}
```
也支持过滤掉一些key
```js
type RemoveKindField<Type> = {
    [Property in keyof Type as Exclude<Property, "kind">]: Type[Property]
};
 
interface Circle {
    kind: "circle";
    radius: number;
}
 
type KindlessCircle = RemoveKindField<Circle>;//返回 {radius: number}
```
# Template Literal Types
Template Literal Types 是建立在 string 字面量类型上面的。  
```js
type World = "world"
type Greeting = `hello ${World}` // type Greeting = "hello world"
```
当模板字面量类型作用于 string 字面量联合类型时，模板字面量类型的值为所有可能值的 union
```js
type People = "Tom" | "Jerry" | "David"
type Greeting = `hello ${People}`// 返回 "hello Tom" | "hello Jerry" | "hello David"
```
比如我们现在有一个 plain object, 需要为它添加所有 field 的 onChange listener
```js
enum SEX = {
  MALE,
  FEMALE,
  OTHER
}
const p = {
  name: "yangliu",
  age: 20,
  sex: SEX.MALE
}
type P = typeof p;
//现在想生成一个类型，该类型具有 p 的所有属性，并且为每个属性添加一个 change listener, 该 listener 的参数为 一个回调，该回调的参数是属性的值
type Person = {
  [Prop in keyof P as `on${Capitalize<string & Prop>}Changed`]: (v: P[Prop]) => void
} & P
// type Person = {
//     onNameChanged: (v: string) => void;
//     onAgeChanged: (v: number) => void;
//     onSexChanged: (v: SEX) => void;
// } & {
//     name: string;
//     age: number;
//     sex: SEX;
// }

```
或者只为对象添加一个属性 on, 它的值是一个注册回调的函数，该函数接受两个参数，第一个参数是事件名字，第二个参数是对应的回调函数，该回调函数的参数为监听的属性的值
```js
type Regist<T> = {
  //这样写只能限制事件名的类型，但是对于回调函数，它的参数的类型没法限制，只能使用 any
  on:(event: `${string & keyof T}Changed`, callback:(v: any) => void) => void
}
//为了做出更精确的限定，需要对 callback 的参数也做出限制，那么该怎么做呢？
//为了让事件名和回调都能从泛型里获取信息，那么就需要将泛型信息往上提，即提到函数 on 的层次，因为函数本身也是支持泛型的
type Regist<T> = {
  on<Key extends string & keyof T>(eventName: `${Key}Changed`, callback:(v:T[Key]) => void):void
}
```
# Intrinsic String Manipulation Types
TS 内置提供了一些用来操作字符串类型的类型， 比如 Uppercase<T>, Lowercase<T>, Capitialize<T>, Uncapitialize<T>  
这些类型的实现细节
```js
function applyStringMapping(symbol: Symbol, str: string) {
    switch (intrinsicTypeKinds.get(symbol.escapedName as string)) {
        case IntrinsicTypeKind.Uppercase: return str.toUpperCase();
        case IntrinsicTypeKind.Lowercase: return str.toLowerCase();
        case IntrinsicTypeKind.Capitalize: return str.charAt(0).toUpperCase() + str.slice(1);
        case IntrinsicTypeKind.Uncapitalize: return str.charAt(0).toLowerCase() + str.slice(1);
    }
    return str;
}
```
# infer
infer 用来在进行类型推导时，如果类型推导成功，则将推导出来的类型，赋给 infer 后跟的类型名。
```js
//这里的 infer Return 表示将推导出来的返回值类型赋给 Return, 然后在 true branch 可以使用该类型
type ReturnType<T> = T extends (...args: any[]) => infer Return ? Return : T
//将推导出的函数参数类型赋值给 P
type ParamType<T> = T extends (...args: infer P) => any ? P : T
//构造函数的类型通常为 new (...args: any[]) => any
//获取构造函数的参数类型
type ParamOfConstructor<T extends new (...args: any[]) => any> = T extends new (...args: infer P)=> any ? P : never
//获取构造函数的返回值类型
type InstanceType<T extends new (...args: any[]) => any> = T extends new (...args: any[])=> infer P ? P : never

class TestClass {
    constructor(public name: string, public age: number) {}
}
type Params = ParamOfConstructor<typeof TestClass>// 返回 tuple [string, number]
type Instance = InstanceType<typeof TestClass>// 返回 TestClass, 在声明一个类时，也会创建一个对应的类型
```
