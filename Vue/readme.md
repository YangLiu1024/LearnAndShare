# Introduction to Vue

## Vue 的特点
  * 轻量级的框架。 Vue.js 能够监听数据变化，从而自动更新 DOM，具有简单灵活的 API，容易上手
  * 双向数据绑定。 声明式渲染是数据双向绑定的主要体现，同样也是 Vue.js 的核心，它允许采用简单的模板语法将数据声明式渲染整合进 DOM
  * 指令。 Vue.js 与页面交互，主要就是通过内置指令完成，指令的作用是当其表达式的值改变时相应的将某些行为应用到 DOM 上
  * 组件化。 组件是 Vue.js 最强大的功能之一，可以扩展 HTML 元素，封装可重用的代码
  * 客户端路由。 Vue-router 是 Vue 官方的路由组件，与 Vue 深度集成。 Vue 单页面应用是基于路由和组件的，路由用于设定访问路径，并将路径和组件映射起来
  * 状态管理。状态管理实际就是一个单向的数据流，state 驱动 view 的 更新，用户对 view 的操作触发 action, action 改变 state, 从而使 view 重新渲染

## Vue 的优势
  * 支持组件开发
  * API 简单易懂
  * 数据的双向绑定
  * vue 使用 vue-router 来实现单页面应用，在路径改变时，不会刷新整个页面
  * 社区活跃，发展迅速

## Vue 单文件
.vue 文件是一个自定义的文件类型，用类HTML 语法描述一个 Vue 组件。每个 vue 文件包含三种类型的顶级语言块 <code>template</code>, <code>script</code> 和 <code>style</code>.

vue-loader 会解析 .vue 文件，提取每个语言块，如有必要，会通过其它 loader 处理，最后组装成一个 CommonJS 模块，module.exports 出一个 Vue 组件对象

<code>template</code>, <code>script</code> 只能有一个, <code>style</code> 可以有多个。

Note: 当在 .js 文件里 import 组件，并在 vue 对象里 <code>template</code> 块使用该组件，那么该 .js 需要在运行时编译，则需要在 webpack 里进行配置，一直使用 vue 完整版
```js
//main.js
import Hello from './hello'

new Vue({
  components: {
    Hello
  },
  template: '<Hello/>'
})
//webpack.config.js

{
  resolve: {
    alias: {
      vue: 'vue/dist/vue.js'
    }
  }
}
```
但同样的，如果直接使用 <i>render</i> 函数，则不需要配置 alias. 因为 Hello 组件的渲染函数已经被 vue-loader 编译好了
```js
//main.js
import Hello from './hello'

new Vue({
  components: {
    Hello
  },
  render: h => h(Hello)
})
```

## Vue 不同版本
* vue.js 完整版，包含编译器和运行时版本
* vue.min.js, 完整版，用于生产环境, 
* vue.runtime.js, 只包含运行时版本, 比 vue.js 小了 30%。 一般在 build 好之后的包里不在需要编译器，所以只使用运行时版本即可
* vue.runtime.min.js 只包含运行时版本，用于生产环境

编译器用于将模板字符串编译为 JS render 函数的代码，运行时用于创建 Vue 实例，渲染并处理虚拟 DOM 等的代码。基本上就是除去编译器之外的一起。

生产环境版本就是开发环境版本压缩后的代码，且删掉只用于开发环境的code. 一般，打包工具，类似于 webpack, 会根据你的环境变量 process.env.NODE_ENV 来判断是否压缩代码





    

