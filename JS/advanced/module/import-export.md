# Import & Export 用法
```js
export const Year = 2023;

export const Month = 12;


import * as date from './date'
date.Year
date.Month

import {Year, Month} from './date'

// 还可以对导入的变量重命名
import {Year as CurrentYear, Month} from './date'


// export 也可以重命名
const Year = 2023;
const Month = 12;

export {Year as CurrentYear, Month}

// 默认导出
const Day = 31;
export default Day;

// 默认导入, 不需要花括号
import Day from './date'
import * as date from './date'
// 获取 default 导出
date.default

import {default as day} from './date'

// 还有的时候，我们希望在 某一个层级统一定义往外暴露的变量，比如 auth/index.js
// auth 下面很可能有很多模块，但是某一些模块我们希望它仅仅在 auth 内部使用，那么我们就可以在 auth/index.js 里重新导出
export {User} from './user.js'
export {login, logout} from './login.js'

// 对于重新导出默认导出，对它的处理有些特殊，我们必须显式重新导出
export Day from './date' // 这条语句并不会生效
export {default as Day} from './date' // 必须这样显式声明
export * from './date' // 这条语句也会忽略默认导出，如果希望导出所有内容，还需要显式导出默认导出
```
# 动态导入
常规的 import/export 是不支持动态导入导出的，但可以使用 *import(module)* 来动态的导入
```js
const module = getModule()
// import(module) 表达式会返回一个 promise, 其值是包含 module 所有导出的对象
import(module).then(obj => {})
```

在 react 里，*lazy* API 就支持动态导入
```js
const LazyComponent = lazy(() => import('./component'))
```