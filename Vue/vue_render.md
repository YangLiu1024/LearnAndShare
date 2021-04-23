# Vue 渲染函数
## 函数式组件
通过声明 functional: true 来将组件声明为 函数式组件。函数式组件没有状态，没有生命周期方法，没有实例 this 上下文。 实际上，它只是一个接受一些 prop 的函数。
```js
Vue.component('my-component', {
  functional: true,
  // Props 是可选的
  props: {
    // ...
  },
  // 为了弥补缺少的实例
  // 提供第二个参数作为上下文
  render: function (createElement, context) {
    // ...
  }
})
```
在 vue 单文件里，为了使用函数式组件，可以将 template 声明为 functional, eg <template functional></template>