# Code Split
在 code build 之后，进行打包生成 bundle 时，打包工具通常会对 code 进行 split, 将不需要初始加载的 code 分离出来，以加快 initial loading.  
同样，我们也可以通过 coding 的方式来引入 code split.

## dynamic import
```js
import math from './math'

console.log(math.sum(1, 2))

// dynamic import
import('./math').then(math => console.log(math.sum(1, 2)))
```
当打包工具检测到动态 import, 就会自动进行 code split

## React.lazy
React.lazy 可以让你像使用普通组件一样使用动态组件，且包含该组件的 bundle 只会在组件第一次渲染时才加载
```js
//before
import CompoA from './CompoA';

// after
const CompoA = React.lazy(() => import('./CompoA'))
```
React.lazy 接收一个函数，该函数必须调用 动态 import, 且返回一个 promise, 该 pomise resolve 一个 module, 该module default export 是一个组件。  
在渲染动态组件时，需要 wrap 在 *Suspense* 组件里面
```js
import React, { Suspense } from 'react';

const OtherComponent = React.lazy(() => import('./OtherComponent'));
const AnotherComponent = React.lazy(() => import('./AnotherComponent'));

function MyComponent() {
  return (
    <div>
      <Suspense fallback={<div>Loading...</div>}>
        <section>
          <OtherComponent />
          <AnotherComponent />
        </section>
      </Suspense>
    </div>
  );
}
```
*Suspense* 的 fallback 用来显示当动态组件正在加载时的页面
## Route-based code split
基于路由的 code split, 比如 react-router
```js
import React, { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

const Home = lazy(() => import('./routes/Home'));
const About = lazy(() => import('./routes/About'));

const App = () => (
  <Router>
    <Suspense fallback={<div>Loading...</div>}>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
      </Routes>
    </Suspense>
  </Router>
);
```