# Module
随着应用程序越来越大，我们将代码分为多个文件，即所谓的 “模块” 来帮助我们组织代码。随着社区发展，有多种模块系统
* AMD => 最古老的模块系统之一，最初由 require.js 实现
* CommonJS => 为 Node.js 服务器搭建的模块系统
* UMD => 建议作为通用的模块系统，可以兼容 AMD 和 CommonJS
最早  JS 里是没有语言级的模块语法，现在上述模块系统都渐渐成为历史的一部分，但我们仍然可以在旧脚本里看到它们。  
现在语言级的模块系统已经成为 ES6 标准，已经得到了所有主流浏览器和 Node.js 的支持

# 什么是模块
一个文件就是一个模块，一个脚本就是一个模块。  
模块只会在 HTTP 环境下工作，如果你通过 file:// 协议在本地打开一个 html 文件，那么其中的 module script 并不会 work. 你需要使用 类似于 *static-server* 的手段使用本地 web 服务器来 serve 一个 html 文件。    
模块可以使用 *import*, *export* 导入导出，实现在一个脚本调用其它脚本的内容。  
* export => 标记了当前模块，有哪些变量和函数可以被外界调用
* import => 允许从其它模块引入变量和函数

在浏览器里使用 模块脚本时，需要指定脚本 type 为 *module*, 否则 import/export 不会工作
```html
<!doctype html>
<script type="module">
  import {sayHi} from './say.js';

  document.body.innerHTML = sayHi('John');
</script>
```
## 模块核心功能
* 模块始终在 *use strict* 模式下运行，且顶层的 this 是 *undefined*
* 每个模块都有自己独立的作用域，其它模块只能通过 import 才能访问模块通过 export 导出的变量和函数
* 模块代码仅在第一次导入时被解析，即模块代码只会在第一次真正导入的时候，才会被执行。后续的导入不会再次解析。但如果有的数据在导入的过程中被修改了，那么这个修改也可以被后续的导入看见。

## 浏览器特定功能
* 模块脚本的加载总是延迟的，并且不会阻塞 HTML 的处理，它需要等到所有常规脚本，和 html 内容加载完之后，才会运行
* 保持脚本的相对顺序 => 在文档中，排在前面的脚本先执行
* 外部脚本 => 通过 <script type="module" src="***"></script> 来加载的脚本就是外部脚本, 当外部脚本来自于其它 origin, 需要
* 内联脚本 => 在 脚本代码里 import 的脚本就是内联脚本， 比如 <script type="module">import {sum} from './util.js'</script>
* 异步脚本 => 对于非模块脚本，async 特性只适用于外部脚本。<script async src="***"></script>. 这时异步脚本在外部脚本加载完后会立即运行，不会等待其它脚本或者 HTML. 当作用于内联脚本时，异步脚本也会在依赖项加载完成后立即执行

# 构建工具
在实际工程中，很少会以原始形式使用模块，通常会使用一些打包工具，比如 webpack, 将模块打包在一起，然后部署在生产服务器。  
构建工具做一下这些事：
1. 从一个打算放在 html 中 <script type="module"></script> 的主模块开始
2. 递归分析它的依赖：它的导入，以及它导入的导入等
3. 使用所有模块构建一个文件，或者多个文件(这是可调的，通常为 bundle.js), 并用打包函数替代原生的 import 调用，以使其正常工作。还支持像 HTML/CSS 等特殊模块
4. 在处理过程中，可能会应用一些转化和优化
   * 删除无法访问的代码
   * 删除未使用的导出
   * 删除特定于开发环境中使用的语句，比如 console
   * 可以使用 Babel 将前言的 JS 语法转换为具有类似功能的旧的 JS 语法，以适用于旧的浏览器
   * 压缩生成的文件

打包之后生成的文件将不再包含任何 import/export 等，也不再需要 type="module", 我们可以将其放入常规的 *script*
```js
<!-- 假设我们从诸如 Webpack 这类的打包工具中获得了 "bundle.js" 脚本 -->
<script src="bundle.js"></script>
```