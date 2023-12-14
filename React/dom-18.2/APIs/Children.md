# Children
有的时候，我们需要去操作接收到的 children 内容。在 react 里，children 的类型是不透明的，我们不能假设它的类型就一定是数组或者其它类型。  
除此之外，API 还会做一些额外的工作，比如 key 的传递。  
因此， 对于 children 的操作，我们只能依赖于提供的 API

# Children.count(children)
统计接收到的 child 个数
```js
import { Children } from 'react';

function RowList({ children }) {
  return (
    <>
      <h1>Total rows: {Children.count(children)}</h1>
      ...
    </>
  );
}
```
空节点，null, undefined, string, number, react element 都会当作一个 node 被统计。数组不会被当作一个 node, 它的 children 会被计数。  
*count* 不会真正渲染节点

# Children.forEach(children, fn)
为每一个 children node 执行一些函数
```js
import { Children } from 'react';

function SeparatorList({ children }) {
  const result = [];
  Children.forEach(children, (child, index) => {
    result.push(child);
    result.push(<hr key={index} />);
  });
```

# Children.map(children, fn)
转换 children
```js
import { Children } from 'react';

function RowList({ children }) {
  return (
    <div className="RowList">
      {Children.map(children, child =>
        <div className="Row">
          {child}
        </div>
      )}
    </div>
  );
}
```
如果 child 本身有指定 key, 那么它的 key 也会自动 assign 到 map 之后的结果上

# Children.only(children)
确保组件接收到一个 valid 的 child node, 如果接收到数组，或者 invalid node, 则抛出异常
```js
function Box({ children }) {
  const element = Children.only(children);

}
```

# Children.toArray(children)
将 children 转换为数组，且 empty node, null, undefined, boolean 在返回值里会被自动丢掉
