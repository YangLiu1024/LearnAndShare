# Fragment

Fragment 的最大的作用就是把元素进行分组，而不需要额外引入 容器 element
```js
function Post() {
  return (
    <>
      <PostTitle />
      <PostBody />
    </>
  );
}
```
使用 Fragment 而不是 <> 的唯一场景，是当需要给 Fragment 指定 key 时
```js
function Blog() {
  return posts.map(post =>
    <Fragment key={post.id}>
      <PostTitle title={post.title} />
      <PostBody body={post.body} />
    </Fragment>
  );
}
```