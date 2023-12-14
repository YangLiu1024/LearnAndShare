# createRoot
根据 dom 节点创建一个 react root, 并接管该 dom 节点里面所有的 dom 显示和更新
```js
import { createRoot } from 'react-dom/client';

const domNode = document.getElementById('root');
// 接管 domNode 里面的 DOM 管理
const root = createRoot(domNode);

// 在 root 节点里渲染 react 组件
// 在第一次调用时，react 会先清除 root 里面所有已有的 HTML 内容，然后替换为渲染 的 react node
// 如果对同一个 root 有多个 render 调用，那么后续的调用就等同于状态更新，react 会根据传入的 react node 自动更新
root.render(<App/>)

// 如果一个由 react 管理的 root 不再使用，则应该将其显示销毁
// 销毁后，不能再使用该 root.render, 但可以通过 domNode 再创建一个新的 root, 再使用该新的 root.render
root.unmount()
```
但有的时候，初始页面的加载可能比较花时间，如果 初始 html 是空白的，那么在加载完所有 JS 代码之前，页面都会保持空白。这样也会降低用户体验。  
为了解决这个问题，可以使用 服务端渲染，即服务端预先渲染一个初始的 html 出来，那么初始页面就不会是空白的。