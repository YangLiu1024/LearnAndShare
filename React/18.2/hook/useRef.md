# useRef
useRef 用来存储不影响组件渲染的本地信息。修改 ref.current 不会触发重新渲染。  
通常，我们不应该在渲染期间去修改 ref 的值，这会影响组件的 pure 特性。因为 react 期望组件的主体行为类似于纯函数，如果输入相同的 props/state/context, 应该返回完全相同的 JSX.  
所以 ref 的写入应该放在 effect 或者事件处理函数当中  

# 使用 ref 操作 DOM
当绑定 ref 到 DOM 节点时，ref 的值会在挂载后初始化为 DOM 节点。在组件卸载时，ref.current 会重置为 null