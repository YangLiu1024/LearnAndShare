# hydrateRoot
如果 在接管 domNode 之前，该 domNode 的内容已经是由 react server 渲染出来的，那么为了不丢掉这些内容，就需要使用 hydrateRoot
```js
import { hydrateRoot } from 'react-dom/client';

const domNode = document.getElementById('root');
const root = hydrateRoot(domNode, reactNode);
```
react 会把初始 html 内容同步为 reactNode 里的状态，相当于把一个 html 快照，反序列化为一个 react node. 不过这需要确保服务器端渲染结果和 react node 初始渲染结果匹配。