# isValidElement
***isValidElement(value)*** check value 是否是合法的 react element， 可以参考 ReactNode 以及 ReactElement 的定义
```ts
    type ReactNode =
        | ReactElement
        | string
        | number
        | Iterable<ReactNode>
        | ReactPortal
        | boolean
        | null
        | undefined

```
而 ReactElement 就是 JSX tags 和 通过 createElement 返回的对象