# Keywords
* extends
* keyof
* in
* typeof
* infer

# Type
* any
* never
* unknown

# Operation
* ...(spread)
* ...(rest)


//...(spread)=> T extends any[], 展开 类型数组 T, ...T
// ... infer rest => T extends any[], 解构 类型数组 T, T extends [infer First, ...infer Rest]
//
type LengthOfTuple<T extends readonly any[]> = T['length'];
//Pick 是从对象 type 里挑选属性
type MyPick<T, K extends keyof T> = {
  [Key in keyof T as Key extends K ? Key : never]: T[Key]
}

type MyReadonly<T> = {
  readonly [K in keyof T]: T[K]
}

type TupeToObject<T extends readonly PropertyKey[]> = {
  [K in T[number]]: K
}

type FirstOfArray<T extends any[]> = T extends [] ? never : T[0];

//Exclude 是从 union 里面过滤掉 option
//利用 union 类型对于 extends 的 分布再结合特性
type MyExclude<T, E> = T extends E ? never : T;

type UnPromise<T> = T extends Promise<infer P> ? UnPromise<P> : T;

type IF<C extends boolean, T, F> = C extends true ? T : F;

type Concat<T1 extends readonly any[], T2 extends readonly any[]> = [...T1, ...T2]

type Equal<A, B> = A extends B ? B extends A ? true : false : false;

type Include<A extends readonly any[], E> = A extends [] ? false : A extends [infer First, ...infer Rest] ? Equal<First, E> extends true ? true : Include<Rest, E> : never

type Push<A extends any[], E> = [...A, E]

type Unshift<A extends any[], E> = [E, ...A]

type ParamsOf<T extends (...args: any) => any = T extends (...args: infer P) => any ? P : never


# 2022.11.24
// TS 里面，extends 的特性。 
// A extends B 为真，表示 能将 A type 赋值给 B type, 比如 一个变量 b：B 被声明为 B 变量，如果 A extends B， 那么就可以将一个 A type 的变量赋值给 b 变量
// 先说一些简单的情况，比如一个 A 类型是 {x: number}, 一个 B 类型是 {x: number, y: number}, 我们能够直接说 B 一定 extends A, 因为如果是 B 类型，那么就一定是 A 类型
// 但是对于 union 类型，比如 A 类型 是 'a' | 'b', B 类型 是 'b', 看起来 A 选项更多，那么是否 A extends B 呢？其实并不是，因为 union 类型是多选一的类型，对于 A 类型，如果选择 'a' 类型，那么此时就不可以赋值给 B 类型，所以最后其实是 B extends A
// 还有更违背直觉的情况，当 extends 作用于泛型时，处理会不一样。当 extends 前面的类型是泛型时，且该类型是一个 union 类型时，会使用分配律计算最终的结果，即将 union 类型的联合项拆开，分别代入条件类型，最后将每个单项的结果再联合起来，得到最终结果

// never
// never 是所有类型的子类型，即 never extends 任何类型，都是 true, 包括 never,any,unknown
// 当 never 被用作泛型时，其实是被当作空的 联合类型，所以因为没有联合项可以分配，所以泛型的表达式根本就没有执行，返回的类型也会是 never

// 那么怎么避免条件判断中的分配律？
// 这个时候就可以将 泛型参数用 [] 括起来，即可阻断条件判断类型的分配
// P<T> = T extends 'x' ? true : false, P<'x' | 'y'> = boolean, P<never> = never
// 在使用 [] 括起来后，泛型参数将会被当作一个整体，不再进行分配。 P<T> = [T] extends ['x'] ? true : false, P<'x' | 'y'> = false, P<never> = true
type MyPick<T, Key extends keyof T> = {
  [K in keyof T as K extends Key ? K : never]: T[K]
}

type Merge<T> = {
  [K in keyof T]: T[K]
}

type MyReadonly<T, Key extends keyof T = keyof T> = Merge<{
  readonly [K in keyof T as K extends Key ? K : never]: T[K]
} & {
  [K in keyof T as K extends Key ? never : K]: T[K]
}>

type DeepReadonly<T> = {
  readonly [K in keyof T]: keyof T[K] extends never ? T[K] : DeepReadonly<T[K]>
}

type TupleToObject<T extends readonly PropertyKey[]> = {
  [K in T[number]]: K
}

type FirstArr<T extends readonly any[]> = T extends [] ? never : T[0]
type LengthofTuple<T extends readonly any[]> = T['length']

type MyExclude<T, U> = T extends U ? never : T;
type UnPromised<T> = T extends Promise<infer R> ? R : T;
type If<C extends boolean, T, F> = C extends true ? T : F;
type ToArray<T> = T extends any[] ? T : [T];
type Concat<L, R> = [...ToArray<L>, ...ToArray<R>]
type Equal<A, B> = A extends B ? B extends A ? true : false : false
// WRONG, T[number] 确实拿到了数组所有元素的类型 union, 但是 E extends T[number] 并不代表 E 和 union 类型中元素相等，只能说明 E extends 某一个元素
type Include<T extends readonly any[], E> = E extends T[number] ? true : false;
// 所以需要把 数组里的所有元素都拿出来和 E 做比较
type Include2<T extends readonly any[], E> = T extends [] ? false : T extends [infer First, ...infer Rest] ? Equal<First, E> extends true ? true : Include2<Rest, E> : never

type Push<T extends any[], E> = [...T, E]
type Unshift<T extends any[], E> = [E, ...T]
type ParameterOf<T extends (...args: any[]) => any> = T extends (...args: infer P) => any ? P : never 

type MyReturnType<T extends (...args: any[]) => any> = T extends (...args: any[]) => infer P ? P : never 

type MyOmit<T, Keys extends keyof T> = {
  [Key in keyof T as Key extends Keys ? never : Key]: T[Key]
}

type MyOmit2<T, Keys extends PropertyKey> = {
  [Key in keyof T as Key extends Keys ? Key : never]: T[Key]
}

type TupleToUnion<T extends readonly any[]> = T[number];
type GetValueUnion<T> = T[keyof T];

// 对于这种需要前置信息的类型推导，一般将前置信息通过泛型传入
type Chainable<T = {}> = {
  get(): T;
  // 同样的，为了获得参数的类型，将其类型通过泛型传入
  // 这里方法泛型将参数 key 做了基础的约束，即应该是字符串，之后又对实参做了进一步的check，在方法调用时，实参的值的类型会被推导，然后用来 检查是否 extends keyof T
  // 之后为了让参数 key 成为类型的 property key, 需要使用 []
  option<K extends string, V>(key: K extends keyof T ? never : K, value: V):Chainable<Merge<Omit<T, K> & {[Key in K]: V}>>
}

type UnionToObject<U extends PropertyKey, V> = {
  [K in U]: V
}

type LastArr<T extends any[]> = T['length'] extends 1 ? T[0] : T['length'] extends 0 ? never : T extends [infer First, ...infer Rest] ? LastArr<Rest> : never
// JS 里 rest 是只能放在最后一个的，但是 TS 里允许放在任意位置
type Last<T extends any[]> = T extends [any, ...any, infer L] ? L : never
type Pop<T extends any[]> = T extends [...infer P, any] ? P : never

type UnPromiseArr<T extends readonly any[]> = T extends [infer F, ...infer Rest] ? [UnPromised<F>, ...UnPromiseArr<Rest>] : [];
type UnPromiseArr2<T extends readonly unknown[]> = {
  [K in keyof T]: T[K] extends Promise<infer P> ? P : T[K]
}
type PromiseAll<T extends readonly any[]> = Promise<UnPromiseArr2<T>>

declare function promiseAll<T extends readonly unknown[]>(...ps: T): PromiseAll<T>

promiseAll(1, Promise.resolve(2), {}, Promise.resolve({a: 1})).then(r => {})

// 注意这里不能写成 T['type'] extends P ? T : never, 即使对 T 加上 extends {type: unknown}
// 猜测原因是 T['type'] 虽然也是 union，但是已经不再作为泛型处理，不会 apply 分配律
type LookupWrong<T extends {type: unknown}, P> = T['type'] extends P ? T : never
type Lookup<T, P> = T extends {type: P} ? T : never

// 模板字符串也可以直接用在 extends 表达式里
type TrimLeft<T extends string> = T extends `${' ' | '\n' | '\t'}${infer R}` ? TrimLeft<R> : T
type TrimRight<T extends string> = T extends `${infer R}${' ' | '\n' | '\t'}` ? TrimRight<R> : T

# 2022.12.20
type Trim<T extends string> = T extends `${' ' | '\t' | '\n'}${infer R}` ? Trim<R> : T extends `${infer R}${' ' | '\n' | '\t'}` ? Trim<R> : T
type Trim2<T extends string> = T extends `${Space}${infer R}` | `${infer R}${Space}` ? Trim2<R> : T

type MyCapitalize<T extends string> = T extends `${infer F}${infer R}` ? `${Uppercase<F>}${R}` : never

type Replace<S extends string, F extends string, T extends string> = S extends `${infer B}${F}${infer A}` ? `${B}${T}${A}` : S
type ReplaceAll<S extends string, F extends string, T extends string> = S extends `${infer Before}${F}${infer After}` ? ReplaceAll<`${Before}${T}${After}`, F, T> : S

type AppendArgument<Func extends (...args: any[]) => any, P> = Func extends (...args: infer Params) => infer R ? (...args: [...Params, P]) => R : never;
type Double<T> = T extends infer I ? [I, I]: never;
type DD1 = Double<'1' | '2' | never>
type Permutation<Options, Item = Options> = [Options] extends [never] ? [] : Item extends infer I ? [I, ...Permutation<Exclude<Options, I>>] : never;
// 第一次进入，泛型本身不为 never, 对每一个迭代项进行判断，产生 ['A', Permuation<'B' | never>], ['B', Permutation<'A' |never>] 的全排列, 而 [never, Permuation<'A' | 'B'>] 因为是 never, 并不会执行判断
// 进入下一次迭代， 对于 ‘B’ | never, 返回 ['B', Permuation<never>], 又因为 Permutation<never> 返回空数组，所以生成 ['A' | 'B']
type P11 = Permutation<'A' | 'B'>

type ConvertStrToArr<T extends string> = T extends '' ? [] : T extends `${infer F}${infer R}` ? [F, ...ConvertStrToArr<R>] : never;
type ConvertStrToArr2<T extends string, Arr extends string[] = []> = T extends `${infer F}${infer R}` ? [...Arr, F, ...ConvertStrToArr2<R>] : Arr;

type StrLength<T extends string> = ConvertStrToArr<T>['length'];
type StrLength2<T extends string, A extends string[] = []> = T extends `${infer F}${infer R}` ? StrLength2<R, [...A, F]> : A['length']

type Flatten<T extends any[], R extends any[] = []> = T extends [infer First, ...infer Rest] ? First extends any[] ? [...R, ...Flatten<First>, ...Flatten<Rest>] : [...R, First, ...Flatten<Rest>] : R
type Flatten2<T extends any[]> = T extends [infer F, ...infer R] ? F extends any[] ? Flatten2<[...F, ...R]>: [F, ...Flatten2<R>]: T

type AppendField<O extends object, F extends PropertyKey, V> = Merge<O & {[Key in F]: V}>

type Absolute<T extends string | number> = `${T}` extends `-${infer N}` ? `${N}` : `${T}`
type StringToUnion<T extends string> = ConvertStrToArr2<T>[number]

type Merge2<T extends object, P extends object> = Merge<P & {[Key in keyof T as Key extends keyof P ? never : Key]: T[Key]}>
type Merge3<T extends object, P extends object> = {
  [K in keyof T | keyof P]: K extends keyof P ? P[K] : K extends keyof T ? T[K] : never
}

type KebabChar<F extends string, Res extends string> = F extends Uppercase<F>
   ? F extends Lowercase<F> ? `${Res}${F}` : Res extends '' ? Lowercase<F> : `${Res}-${Lowercase<F>}` : `${Res}${F}`
type KebabCase<S extends string, Res extends string = ''> = S extends `${infer F}${infer R}` 
  ? KebabCase<R, KebabChar<F, Res>>: Res;

type KebabCase2<S extends string> = S extends `${infer A}${infer B}` 
  ? B extends Uncapitalize<B> 
    ? `${Lowercase<A>}${KebabCase2<B>}` 
    : `${Lowercase<A>}-${KebabCase2<B>}`
  : S;

//T1 & T2 表示 该类型即是 T1 又是 T2，那么该类型就是 T1 和 T2 的并集, 当 T1 T2 具有相同的 field 时，若该 field type 不一样，那么 T1 & T2 里该 field 的 类型将会是 never
//T1 | T2 表示该类型要么是 T1, 要么是 T2，如果这时候再作用于 keyof, 那么 返回的将会是两个类型的 交集
type Diff<T1 extends object, T2 extends object> = Omit<T1 & T2, keyof (T1 | T2)>
type Same<T1 extends object, T2 extends object> = {
  [K in keyof (T1 | T2)]: T1[K] | T2[K]
}

// 为了表示空对象，不能直接使用 {}, 需要用 Record<PropertyKey, never> 代替
// TS 里怎么判断一个类型是空对象？

type Falsy = 0 | '' | undefined | null | [] | Record<PropertyKey, never>;
type AnyOf2<T extends readonly any[]> = T extends [infer F, ...infer R] ? F extends Falsy ? AnyOf<R> : true : false;
type AnyOf<T extends readonly any[]> = T[number] extends Falsy ? false : true //T[number] extends Falsy 表示数组的元素全是 Falsy, 这里 T 虽然是类型，但是使用了 T[number], 就不会再使用分配律

//type IsEmpty<T extends Record<PropertyKey, unknown>> = 
type IsNever<T> = [T] extends [never] ? true : false;
// 怎么判断是否为 Union type 呢？union 的特殊在于分配律
type IsUnion<
T,
R=T> =
    [T] extends [never] // 首先排除 never, 否则会返回 never 类型
    ? false
    :T extends T // 如果是 union 类型，则会 apply 分配律，union 中的每一项继续执行之后的条件表达式，最后把结果再 union 起来
      ? [R] extends [T] // R 是原始的类型，这里的 T 已经是分配之后的每一项
        ? false // 如果 R 和 T 相同，表示 T 并不是 union 类型
        :true
      :never

  # 2023.1.13
  type ReplaceKeys<Nodes, Keys, Types> = 
Nodes extends unknown // 显式应用分配律
? {[K in keyof Nodes]: K extends Keys ? K extends keyof Types ? Types[K] : never : Nodes[K]}
: never

type ReplaceKeys2<U, T, Y> = {
  [P in keyof U]: P extends T // 分配律不仅仅只在 T extends *, 这里仍然应用了分配律
    ? P extends keyof Y
      ? Y[P]
      : never
    : U[P]
}

type RemoveIndexSignature<T> = {
  [K in keyof T as string extends K ? never : number extends K ? never : symbol extends K ? never : K]: T[K]
}
type Digit = '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'
type ParseNumber<T extends string, Num extends string = ''> = T extends `${infer F}${infer R}` ? F extends Digit ? ParseNumber<R, `${Num}${F}`> : '' : Num;
type ParseUnit<T extends string> = T extends `${infer F}%` ? [ParseNumber<F>, '%'] : [ParseNumber<T>, ''];
type PercentageParser<T extends string> = T extends `+${infer R}` ? ['+', ...ParseUnit<R>] : T extends `-${infer R}` ? ['-', ...ParseUnit<R>] : ['', ...ParseUnit<T>]

// 字符串模板 
// 获取第一个字符 `${infer F}${infer R}`
// 在开始位置匹配 `${T}${infer R}`
// 在结尾位置匹配 `${infer F}${T}`
// 在任意位置匹配 `${infer A}${T}${infer B}`
type DropChar<T extends string, C extends string, Res extends string = ''> = T extends `${infer F}${infer R}` ? F extends C ? DropChar<R, C, Res> : DropChar<R, C, `${Res}${F}`> : Res
type DropChar2<T extends string, C extends string> = T extends `${infer A}${C}${infer B}` ? `${A}${DropChar<B, C>}` : T

type MinusMap = {
  '0' : '9',
  '1': '0',
  '2': '1',
  '3': '2',
  '4': '3',
  '5': '4',
  '6': '5',
  '7': '6',
  '8': '7',
  '9': '8'
}
// 20 - 1
type ParseDigit<T extends string> = T extends `${infer Digit extends number}` ? Digit : never; // parse string to number
type ReverseString<T extends string> = T extends `${infer F}${infer Rest}` ? `${ReverseString<Rest>}${F}` : ''
type Minus<T extends string> = T extends `${infer Rest}0` ? `${Minus<Rest>}9` : ReverseString<T> extends `${infer F extends keyof MinusMap}${infer Rest}` ? `${ReverseString<Rest>}${MinusMap[F]}` : T
type MinusOne<T extends number> = ParseDigit<Minus<`${T}`>>

type tm = MinusOne<1230>

type GetHead<T extends string> = T extends `${infer F}${Digit}` ? F : never   

type PickByType<T, U> = {
  [K in keyof T as T[K] extends U ? K : never]: T[K]
}

type OmitByType<T, U> = {
  [K in keyof T as T[K] extends U ? never : K]: T[K]
}

type StartsWith<T extends string, U extends string> = T extends `${U}${infer R}` ? true : false;
type EndWith<T extends string, U extends string> = T extends `${infer R}${U}` ? true : false;
type Contain<T extends string, U extends string> = T extends `${infer A}${U}${infer B}` ? true : false;

type PartialByKeys<T, U extends keyof T = keyof T> = Merge<{
  [K in keyof T as K extends U ? K : never]?: T[K]
} & {
  [K in keyof T as K extends U ? never : K]: T[K]
}>

type RequiredByKeys<T, U extends keyof T = keyof T> = Merge<{
  [K in keyof T as K extends U ? K : never]-?: T[K]
} & {
  [K in keyof T as K extends U ? never : K]: T[K]
}>

type Mutable<T> = {
  -readonly [K in keyof T]: T[K]
}

type ObjectEntries<T, K extends keyof T = keyof T> = K extends K ? [K, T[K]] : never

// T 已经声明为 PropertyKey[], 为什么 T extends [...infer F, infer L] 还需要声明 T extends [...infer F extends PropertyKey[], infer L extends PropertyKey]？？？
// 在解构数组的时候，不能 infer 出数组元素的类型吗？
type TupleToNestedObject<T extends readonly PropertyKey[], U, R = U> = T extends [...infer Rest extends PropertyKey[], infer Last extends PropertyKey] ? TupleToNestedObject<Rest, U, {[K in Last]: R}> : R

type ArrayReverse<T extends readonly any[]> = T extends [infer First, ...infer Rest] ? [...ArrayReverse<Rest>, First] : []

type FlipArguments<T extends (...args: any[]) => any> = T extends (...argvs: infer Args) => infer R ? (...args: ArrayReverse<Args>) => R : never
type FlattenOnce<T extends any[]> = T extends [infer F, ...infer Rest] ? F extends any[] ? [...F, ...FlattenOnce<Rest>] : [F, ...FlattenOnce<Rest>] : T;
type FlattenAll<T extends any[]> = T extends [infer F, ...infer Rest] ? F extends any[] ? FlattenAll<[...F, ...Rest]> : FlattenAll<[F, ...Rest]> : T;
type FlattenDepth<T extends any[], Depth extends number = 1> = Depth extends 0 ? T : FlattenDepth<FlattenOnce<T>, MinusOne<Depth>>;
