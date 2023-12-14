# lazy
在组件外部调用，以申明延迟加载的 react 组件
```js
// lazy 的参数是一个函数，该函数返回一个 promise like，其最终的值应该是一个react 组件。 react 只会在第一次尝试渲染该组件时，调用该函数
// 该函数返回的 promise 以及 promise resolve 的值都将被缓存
const MarkdownPreview = lazy(() => import('./MarkdownPreview.js'));
```
lazy 通常和 Suspense 以及 ErrorBoundray 联合使用。
```js
<Suspense fallback={<Loading />}>
  <h2>Preview</h2>
  <MarkdownPreview />
 </Suspense>
```
lazy 的作用在于，有的组件可能只会在某一些事件后才可能显示，那么在初始渲染时，则不需要 加载这些组件的代码，从而提高初始渲染速度。
```js
const MarkdownPreview = lazy(() => delayForDemo(import('./MarkdownPreview.js')));
// MarkdownPreview 的加载会在 showPreview 为 true 时才开始。在加载时，会显示 loading, 在加载结束后，才会显示内容
// 在加载完成一次后，后续表现就是正常组件了
export default function MarkdownEditor() {
  const [showPreview, setShowPreview] = useState(false);
  const [markdown, setMarkdown] = useState('Hello, **world**!');
  return (
    <>
      <textarea value={markdown} onChange={e => setMarkdown(e.target.value)} />
      <label>
        <input type="checkbox" checked={showPreview} onChange={e => setShowPreview(e.target.checked)} />
        Show preview
      </label>
      <hr />
      {showPreview && (
        <Suspense fallback={<Loading />}>
          <h2>Preview</h2>
          <MarkdownPreview markdown={markdown} />
        </Suspense>
      )}
    </>
  );
}
```