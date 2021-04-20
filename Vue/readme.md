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

### Vue 构造器
```js
new Vue({
  //对于 data,props,methods,computed 等Object, vue 对其属性进行了代理，当使用 vm.key 时，等价于 vm.$data.key
  el: "#root", //指定该实例挂载的DOM元素
  data: {},//组件使用的数据，对于组件，data 必须是函数
  props: {},//定义需要父组件传递给该组件的一些参数
  template: `<div>{{msg}}</div>`, //用于渲染的模板，
  components:{},//定义该组件使用的子组件，定义之后，则可以在模板里直接使用
  methods: {}, //定义该组件的一些方法
  computed: {},//定义该组件的一些计算属性，当依赖改变，会自动更新
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
### Vue 实例属性
* vm.$data: Object 当前实例的data 对象
* vm.$props: Object 当前实例接收到的 props 对象
* vm.$el: Element 当前实例使用的 根 DOM 元素
* vm.$options: Object 当前实例自定义的属性
* vm.$parent: Vue instance 当前实例的父实例，如果当前实例有的话
* vm.$root: Vue instance 当前实例的根 vue 实例，如果当前实例没有父实例，则root 会是其自己
* vm.$children: Array<Vue instance> 当前实例的直接子实例数组
* vm.$slots： [name: String]: ？Array<VNode> 用来访问被插槽分发的内容, 比如 vm.$slots.default 将返回所有分发到 default slot 的内容。如果自定义 render 函数，该属性必被使用。
* vm.$scopedSlots: [name: string]: props => Array<VNode>|undefined 用来访问作用域插槽，对每一个插槽，返回一个函数
* vm.$refs: Object 持有注册过 ref 属性的所有 DOM元素 和 组件实例
* vm.$attrs: [key: string]:string 包含了父作用域中不作为 prop 被识别的 属性绑定，（class/style 除外）
* vm.$listeners: [key:string]:Function|Array<Function> 包含了父作用域中的不含 .native 修饰的 v-on 事件监听器

### Vue 实例方法
```js
//a.b.c 是一个键路径, 当 c 内部的某些属性值改变时，也会触发回调
vm.$watch('a.b.c', function(newV, oldV) {
 //do something
}， {deep: true})
//监听一个函数的返回值，这儿相当于监听一个匿名计算属性
var unwatch = vm.$watch(function() {return this.a + this.b}, function(newV, oldV){//do something})

unwatch()//解除监听
```
* <code>vm.$watch(expOrFn, callback, [options]): Function<code>. options 是一个对象，包含两个 boolean 类型的参数，deep 和 immediate。
deep 表示是否监听对象内部值的变化，immediate 表示当值变化，立即以当前值触发回调。该方法返回一个函数 unwatch，当想解除监听时，调用该函数即可
* <code>vm.$set(target, propertyName/index, value)</code>, target 是一个对象或者数组，vm.$set 是全局函数 Vue.set 的别名。 用处是给 响应式对象添加响应式属性
* <code>vm.$delete(target, propertyName/index)</code>, vm.$delete 是 Vue.delete 的别名。如果对象是响应式的，则保证删除能够触发视图更新
* <code>vm.$on(event, callback)</code>, 监听当前实例上的自定义事件，事件可以由 vm.$emit 触发
* <code>vm.$once(event, callback)</code>, 监听当前实例上的自定义事件，但只监听一次，触发后，移除该监听器
* <code>vm.$off(event, callback)</code>, 移除自定义事件监听器。如果不提供参数，则移除该实例所有自定义事件的所有监听器，如果只指定事件，则删除该事件的所有监听器。
* <code>vm.$emit(event, [..args])</code>, 触发当前实例上的事件，参数可以是多个，并且都会被回调函数捕获
* <code>vm.$mount()</code>, 用于挂载 DOM 元素
* <code>vm.$forceUpdate</code>, 用于迫使 vue 实例重新渲染，注意该方法仅影响该实例本身，以及插入插槽内容的子组件
* <code>vm.$nextTick(callback)</code>, 将回调延迟到 DOM 下次渲染更新完成之后。比如在代码里更改某数据后，调用该方法，使得在 DOM 更新之后再执行回调
* <code>vm.$destroy</code>, 销毁实例


### Vue 内置指令
 * v-text 用来更新 element 的 textContent. <span v-text="msg"></span> 等价于 <span>{{msg}}</span>
 * v-html, 更新元素的 innerHTML
 * v-show,绑定 boolean 表达式，控制元素 是否display:none
 * v-if, 绑定 boolean 表达式，控制组件是否销毁或者重建。 当 v-if 与 v-for 在同一个元素上使用，v-for 优先级更高。这意味着 v-if 会依次作用于所有 item
 * v-else, 前兄弟节点需为 v-if 或者 v-else-if，不需要表达式
 * v-else-if, 前兄弟节点需为 v-if 或者 v-else-if
 * v-for, 遍历数组/对象/string/integer/iterable， v-for = "(item, index) in items" 或者 v-for="(val, key, index) in object"
 * v-on, 绑定事件监听器, 缩写为 @。事件修饰符： .stop/.prevent/.self/.native/.keyCode/.once. 当监听原生事件时，响应方法以事件为唯一参数，比如 <button @click="onClick"></button>, onClick 方法可以接收一个事件参数。 当使用内联语句时，可以直接访问该事件属性。 <button @click="onClick(1, $event)"></button>. v-on 还可以使用对象语法， <button v-on="{click: doThis, mouseup: doThat}"></button>。使用对象语法，意味着事件监听器可以进行运算，传递等等操作。 v-on 还可以监听动态事件， <button v-on:[event]="doThis"></button>
 * v-bind, 绑定属性或者子组件 prop，缩写为 :. 还可以将父组件接收的 props 传递给子组件 <child-component v-bind="$props"></child-component>
 * v-model, 在表单控件上创建双向绑定。一般来说，控件在改变 value 时，会触发 input 事件，并将新的值发出，父组件响应该事件，并更新自己的值，达到双向更新.
 * v-slot, 指定具名插槽，默认值是 default, 比如 v-slot:header, 或需要接收 prop 的插槽，v-slot 只能用于 template 元素， 缩写为 #. 有的时候，在外部自定义 slot 的内容，需要使用到组件本身的数据，为了打破作用域访问限制，这个时候就可以通过作用域插槽将组件参数传递出去，供外部调用。怎么传递呢？通过 v-bind 将数据作为属性绑定给 slot，eg, <p><slot v-bind:user="user"></slot></p>, 这样就把组件的数据 user 作为 user 属性，绑定在了 default slot 上。在父级作用域，通过 <user><template v-slot:default="slotProps"></template></user> 来接收 user 组件 default slot 的 props, slotProps 是一个对象，名字任意，是绑定在 default slot 上所有属性的集合。在我们的例子里，就可以通过 slotProps.user 来访问 user 组件提供的 user 数据。作用域插槽的最大用处就在于可以在父作用域通过子组件的数据自定义子组件内容。
 * v-pre， 用来跳过指定元素及其子元素的编译过程 
 * v-one， 元素只渲染一次
