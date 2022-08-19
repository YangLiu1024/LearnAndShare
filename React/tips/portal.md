# Portal
React 提供了一种将 ReactNode 渲染到指定 dom 节点的方法 *React.createPortal(node, container)*.  
第一个参数是 React 任意可渲染的节点  
第二个参数是 dom 节点，用来挂载 node  
通过这种方式，可以将节点插入到不同的位置，一个典型的用例就是 dialog, tooltip 之类。  

Portal 神奇的地方在于，即使这个 组件是 portal, 会被插入到其它 DOM 节点，但是它仍然表现的像是一个正常的 Reach Child, 比如 context, event bubbling.  
```html
<div id="app"></div>
<div id="portal"></div>
```
比如 app 里定义了 context, 然后 app 里有一个 React Child 通过 React.createPortal 最终挂载到了 portal DOM 节点里，该 React Child 仍然可以访问 app context. 就像它没有被 portal 一样。