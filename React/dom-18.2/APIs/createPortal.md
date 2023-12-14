# createPortal
```js
// 支持将 children  渲染到其它位置，而不是当前 DOM 流
// children 是任意 ReactNode, domNode 是必须已经存在的 DOM 节点
createPortal(children, domNode, key?)

// for example, createPortal 将会把 children 渲染到 domNode， 而不是当前 SomeComponent 旁边
<div>
  <SomeComponent />
  {createPortal(children, domNode, key?)}
</div>
```
需要注意的是，portal 只是改变了 children 的 DOM 节点物理位置，在其它任何方面，都和普通的组件行为是一样的。 
即 children 会表现得就像是在当前父组件里渲染似的，children 可以使用当前父组件提供的上下文，发出的 事件也会被 父组件而不是 DOM 节点物理位置的 父节点捕获。  
```js
import { createPortal } from 'react-dom';

export default function MyComponent() {
    // p 会被渲染到 body 里，但是即使在物理位置上 p 属于 body, 但是 p 仍然可以使用 MyComponent 提供的上下文，p 里发生的事件也会传递给 MyComponent 而不是 body
  return (
    <div style={{ border: '2px solid black' }}>
      <p>This child is placed in the parent div.</p>
      {createPortal(
        <p>This child is placed in the document body.</p>,
        document.body
      )}
    </div>
  );
}
```
最后渲染出的结果就会是
```html
<body>
  <div id="root">
    ...
      <div style="border: 2px solid black">
        <p>This child is placed inside the parent div.</p>
      </div>
    ...
  </div>
  <p>This child is placed in the document body.</p>
</body>
```
# Modal Dialog
通过 createPortal, 可以快速简单的创建 modal 或者 tooltip 之类的悬浮控件
