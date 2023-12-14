# Suspense
Suspense 允许 UI 显示回退，直到子组件完成渲染。这里的子组件可以不是直接子组件，只要子组件支持 suspense, 那么在挂起的时候，就会往上查找最近的 Suspense
```js
reutrn (
    // 在 children 渲染好之前，显示 fallback 的内容
    <Suspense fallback={fallback}>
        {children}
    </Suspense>
)
```
当数据在 effect 或者事件回调里获取，Suspense 并不会被触发。Suspense 目前只支持以下方式
1. 使用支持 Suspense 的框架，比如 relay/nextjs, 来获取数据
2. 延迟加载的组件，*** const LazyComponent = lazy(() => import('./component.js')) ***
3. 在使用了 ***use*** hook 的组件里，当 use promise, 且该 promise 状态是 pending 时，则会显示 Suspense


# 一次性显示内容
Suspense 的 children 总是在全部 ready 之后，一次性一起显示。
```js
// 只有当 Biography 和 Albums 都 ready 的时候，才会一起显示，在这之前，都是显示回退内容
export default function ArtistPage({ artist }) {
  return (
    <>
      <h1>{artist.name}</h1>
      <Suspense fallback={<Loading />}>
        <Biography artistId={artist.id} />
        <Panel>
          <Albums artistId={artist.id} />
        </Panel>
      </Suspense>
    </>
  );
}

function Loading() {
  return <h2>🌀 Loading...</h2>;
}
```

# 嵌套使用 Suspense
```js
export default function ArtistPage({ artist }) {
  // 当 Biography 没有加载完成，即使 Albums 已经加载完成， 都会显示 BigSpinner
  // 当 Biography 加载完成，就会显示 Biography 和 AlbumsGlimmer
  // 当 Albums 加载完成，就会替换 AlbumsGlimmer 为 Panel
  return (
    <>
      <h1>{artist.name}</h1>
      <Suspense fallback={<BigSpinner />}>
        <Biography artistId={artist.id} />
        <Suspense fallback={<AlbumsGlimmer />}>
          <Panel>
            <Albums artistId={artist.id} />
          </Panel>
        </Suspense>
      </Suspense>
    </>
  );
}
```
# 在加载时显示陈旧内容
为了在 pending 时不显示 回退内容，也不阻塞 UI 渲染，可以使用 useDefferedValue
```js
// 使用 回退, 当 query 改变时，会显示 loading
export default function App() {
  const [query, setQuery] = useState('');
  return (
    <>
      <label>
        Search albums:
        <input value={query} onChange={e => setQuery(e.target.value)} />
      </label>
      <Suspense fallback={<h2>Loading...</h2>}>
        <SearchResults query={query} />
      </Suspense>
    </>
  );
}
```
使用 useDefferedValue. 那么此时由 deferredQuery 改变引起的重新渲染将会在后台进行，且可以被取消。UI 不会阻塞，会继续显示过时的内容，直到后台渲染结束，被新结果替换。  
另外，useDefferedValue 也会避免 Suspense 显示回退
```js
export default function App() {
  const [query, setQuery] = useState('');
  const deferredQuery = useDeferredValue(query);
  const isStale = query !== deferredQuery;
  return (
    <>
      <label>
        Search albums:
        <input value={query} onChange={e => setQuery(e.target.value)} />
      </label>
      <Suspense fallback={<h2>Loading...</h2>}>
        <div style={{ opacity: isStale ? 0.5 : 1 }}>
          <SearchResults query={deferredQuery} />
        </div>
      </Suspense>
    </>
  );
}
```
# 使用过渡
过渡也会避免 Suspense 显示回退内容，但是 过渡不会等待渲染完全完成，它只会等待足够长的时间，来避免 hide 一些已经渲染的内容。  
比如，在 路由 切换时，可能一些 layout 布局是共用的，那么使用 过渡就可以避免 hide 掉这些共用的内容。