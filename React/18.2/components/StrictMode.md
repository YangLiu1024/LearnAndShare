# StrictMode
严格模式下，在开发模式中，react 会进行以下行为来帮助发现潜在的问题
1. 组件在每一次渲染时会调用一些函数体两次，以及时发现 impure rendering。
2. 组件会在挂载时，执行 两次 effect, 具体 flow 会是 effect => cleanup => effect。在状态改变引起的 effect 时，只会执行 cleanup => effect
3. 检查是否使用了 deprecated API

# 调用函数两次
对于 #1, 组件会调用以下函数两次
1. 组件函数体(仅顶层函数体，不包括事件处理函数中的代码)
2. 传递给 useState, 状态更新函数， useMemo 的函数

如果组件是纯函数，那么两次执行应该会得到完全相同的结果. 但如果渲染函数改变了传递的参数，则会导致一些错误
```js
// 第一次渲染时，看起来不会有问题，但是一旦组件再次渲染，那么错误就会很明显了
export default function StoryTray({ stories }) {
  const items = stories;
  items.push({ id: 'create', label: 'Create Story' });
  return (
    <ul>
      {items.map(story => (
        <li key={story.id}>
          {story.label}
        </li>
      ))}
    </ul>
  );
}
```
可以给组件添加一个状态，来快速发现问题
```js
export default function StoryTray({ stories }) {
  const [isHover, setIsHover] = useState(false);
  const items = stories;
  items.push({ id: 'create', label: 'Create Story' });
  return (
    <ul
      onPointerEnter={() => setIsHover(true)}
      onPointerLeave={() => setIsHover(false)}
      style={{
        backgroundColor: isHover ? '#ddd' : '#fff'
      }}
    >
      {items.map(story => (
        <li key={story.id}>
          {story.label}
        </li>
      ))}
    </ul>
  );
}
```
# 执行 effect 两次
在严格模式，开发模式中，react 会为 每个 effect 运行一个额外的 cleanup 和 effect 周期
