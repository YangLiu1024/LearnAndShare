# useDefferedValue
`useDefferedValue` 是一种延迟渲染的机制
```js
// 初始渲染，defferedValue 会和 value 值一样
// 在后续 defferedValue 接收到新的值时，它首先会保持当前渲染(以之前的值渲染的结果)，然后 schedule 一个 background re-render with new value.
// 这个 background re-render 是可以打断的，即如果在 background re-render 期间，defferedValue 又接收到了一个新的 value, 则会打断当前的 background re-render, 然后以最新的值重新开始 background re-render
// 看起来 useDefferedValue 和防抖操作类似，区别在于 useDefferedValue 没有固定的等待时间
const defferedValue = useDefferedValue(value)// value 必须是 primitive 类型，或者定义在函数外部的对象，否则每次渲染，都会创建新的对象，导致冗余的 background-render
```