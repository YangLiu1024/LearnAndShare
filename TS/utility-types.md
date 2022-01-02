# Utility Types
前面也提到过，TS 内置了许多工具类型，比如 Uppercase<T>, Capitialize<T> 等。

# Partial<T>
返回 T 里所有的属性，只是每个属性都是 optional
```js
type Partial<T> {
    [Key in keyof T]+?:T[Key]
}
//常见的使用场景
interface Todo {
  title: string;
  description: string;
}
 
function updateTodo(todo: Todo, fieldsToUpdate: Partial<Todo>) {
  return { ...todo, ...fieldsToUpdate };
}
 
const todo1 = {
  title: "organize desk",
  description: "clear clutter",
};
 
const todo2 = updateTodo(todo1, {
  description: "throw out trash",
});
```

# Required<T>
和 Partial<T> 相反，返回类型的所有属性都是 required

# Readonly<T>
返回类型的所有属性都变成 readonly
```js
function freeze<Type>(obj: Type): Readonly<Type>;
```
# Record<Keys, Type>
创建一个对象类型，该类型的 key 的类型是 Keys, value 的类型是 Type
```js
type Record<K extends string | number | symbol, T> = { [P in K]: T; }

interface CatInfo {
  age: number;
  breed: string;
}
 
type CatName = "miffy" | "boris" | "mordred";
 
const cats: Record<CatName, CatInfo> = {
  miffy: { age: 10, breed: "Persian" },
  boris: { age: 5, breed: "Maine Coon" },
  mordred: { age: 16, breed: "British Shorthair" },
};
```
# Pick<T, Keys>
从 T 的属性里根据 Keys 挑选一些属性出来构建一个新的类型
```js
type Pick<T, K extends keyof T> = { [P in K]: T[P]; }

interface Todo {
  title: string;
  description: string;
  completed: boolean;
}
 
type TodoPreview = Pick<Todo, "title" | "completed">;
 
const todo: TodoPreview = {
  title: "Clean room",
  completed: false,
};
```
# Omit<T, Keys>
和 Pick 相反，Omit 将 keys 里的属性给去除掉
```js
type Omit<T, K extends string | number | symbol> = { [P in Exclude<keyof T, K>]: T[P]; }
```
# Exclude<T, ExcludedUnion>
在 T 里去除掉 ExcludedUnion 里包含的选项。 对于 Exclude, T 和 U 都应该是 联合类型
```js
//在条件类型判断里，如果 T 是联合类型，那么该判断会依次作用于 联合类型的所有选项，再将最后的结果合并
type Exclude<T, U> = T extends U ? never : T
```
# Extract<T, Union>
提取 T 里所有能够 assign 给 Union 的属性, 和 Exclude 相反
```js
type Extract<T, U> = T extends U ? T : never
```
# ReturnType<T>
返回函数的返回值类型
```js
type ReturnType<T extends (...args: any) => any> = T extends (...args: any) => infer R ? R : any
```
