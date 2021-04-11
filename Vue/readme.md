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
    
## Vue 实例
```js
new Vue({
  el: "#root", //挂载的DOM元素
  data: {//组件使用的数据，对于组件，data 必须是函数
  },
  props: {},//定义需要父组件传递给该组件的一些参数
  template: `<div>{{msg}}</div>`, //用于渲染的模板，
  components:{},//定义该组件使用的子组件，定义之后，则可以在模板里直接使用
  methods: {}, //定义该组件的一些方法
  computed: {},//定义该组件的一些计算属性，
  watch: {},//可以watch data/props 中的变量，当改变时，触发回调
  
  beforeCreate() {},//hook, 在初始化阶段，init events 和 lifecycle 之后调用
  created() {}, //hook，在初始化阶段，完成 数据注入和 支持响应性后调用。该钩子结束后，check 是否指定 el 属性，如果有，则开始进入compile 阶段，如果没有，则等执行 vm.$mount 时，进行compile
                //在 compile 阶段， 如果有模板，则将模板编译到 render 函数，如果没有，则将 el 的 outerHTML 作为模板进行编译
  beforeMount() {}, //hook, 在init vm.$el 之前调用。 当 vm.$el 初始化结束，并且将 el 的内容替换为 vm.$el 后，调用mounted
  mounted() {}, //hook, 在实例挂载到 DOM 元素之后调用
  beforeUpdated() {},//hook, 在组件挂载后，销毁前，会一直接收用户输入，当数据改变时，就调用 beforeUpdate
  updated() {}, //当数据改变导致的重新渲染结束，则调用 updated 钩子
  beforeDestory() {},//当组件需要销毁，比如路由路径改变，需要渲染其它组件时，调用 beforeDestory
  destoryed() {}//当组件自己的 watcher, 子组件，事件监听等都被销毁后，调用 destroyed
  
  directives:{},//定义该组件使用的自定义指令
  filters: {}, //定义该组件使用的过滤器
  mixins:[],//定义该组件使用的一系列混入对象
})
```
