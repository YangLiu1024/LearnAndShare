# Questions for Interview

## Basic knowledge about Vue
1. Vue 完整版和运行时版本有什么区别？ 开发环境版本和生产环境版本有什么区别？  
   完整版包含了编译器和运行时，编译器用于编译 <i>template</i> 模板为 render 函数，运行时用于创建 vue 实例，渲染 DOM树等。  
   生产环境相对于开发版本，就是删掉了只用于开发环境的 code, 并进行一定的压缩。

2. 构造Vue 实例的常用选项有什么？用处是什么？有什么需要注意的地方？
* <i>el</i>, 用于指定挂载的DOM 元素，一般为 CSS 选择器， 渲染出来的结果， vm.$el, 将替换指定元素的内容  
* <i>data</i>, 实例的数据对象， 当用于组件时，必须为函数  
* <i>props</i>, 组件可以接收的参数，由子组件定义，父组件传入。 props 的定义可以包含 type/required/default/validator  
* <i>template</i>, 定义组件模板，只能有一个根元素。(vue3 可以有多个根元素，但必须user自己指定父组件传入的 attrs 该由哪个根元素获取)  
* <i>computed</i>, 定义计算属性，可以只定义getter, 即将计算属性作为函数直接定义，也可以定义计算属性的 getter 和 setter, 在计算属性内定义 *get*, *set* 函数
* <i>methods</i>, 定义组件方法  
* <i>render</i>, 定义 render 函数，如果该选项存在，则会忽略 <i>template</i> 选项  
* <i>watch</i>, 是一个对象，key 是需要监听的 js 表达式，value 是回调function， 一般用于监听 data 或者 props 的变化， 在实例初始化时，对该对象键值对，依次调用 vm.$watch.  并且回调函数不能使用箭头函数。watch 的使用场景为当需要在数据变化时执行异步或者开销较大的操作时。  
* <i>components</i>, 定义该组件引用的其它组件  
* <i>beforeCreate</i>, <i>created</i>, <i>beforeMount</i>, <i>mounted</i>, <i>beforeUpdate</i>, <i>updated</i>, <i>beforeDestory</i>, <i>destoryed</i>, 生命周期 hook.   
  beforeCreate 在完成初始化生命周期和事件配置后执行，此时数据还没有初始化，完成数据初始化以及响应化，执行 created. 这时候还没开始挂载。如果发现有 el选项，进入 compile 阶段, 如果有 template, 则编译 template, 如果没有，则编译 el 的 outerHTML. 如果没有 el， 则等待 invoke vm.$mount(), 触发 $mount() 后，进入编译阶段。 编译完成后，调用 beforeMount, 这个时候 vm.$el 还没有被更新，仍不可用。在 vm.$el 创建完成之后，并替换了 el 指定的元素内容之后，调用 mounted. 之后则进入等待用户输入的阶段，当用户更改了视图，数据改变，则调用 beforeUpdate。 当视图更新完成后，调用 updated, 并循环该流程。当组件要被销毁前，调用 beforeDestory, 销毁阶段，清除所有的数据观测，事件配置和所有的子组件。销毁完成，调用 destroyed.  

 注意当 mounted 和 updated 调用时，并不能保证该组件的所有子组件都已经被mounted 或者 updated.  

3. Vue 实例的常用属性有什么？有什么需要注意的地方？  
* <i>vm.\$data</i>, vue 实例的数据对象, vm.$data.key 等价于 vm.key. 当直接添加/删除 键值对时，并不是响应式的，需要使用 Vue.set 和 Vue.delete   
* <i>vm.$el</i>, vue 实例编译出来的 DOM 根元素  
* <i>vm.$parent</i>, 该 vue 实例的 父实例  
* <i>vm.$root</i>, 该 vue 实例组件树上的根 vue 实例  
* <i>vm.$children</i>, 该 vue 实例的直接子组件数组，该属性并不保证顺序，也不是响应式的  
* <i>vm.$props</i>, vue 实例实际接收到的参数，可以通过 v-bind="\$props" 传递给子组件  
* <i>vm.\$attrs</i>, 包含了父作用域里不作为 prop 被识别且获取的 attribute 绑定(class 和 style 除外)，可以在内部 通过 v-bind="$attrs" 传递给子组件
* <i>vm.$options</i>, 记录的是实例在被构造时，传入的额外的选项。 
* <i>vm.$slots</i>, 该 vue 实例被父组件分发的 slot 节点， 该属性是一个对象，key 是 slot name, value 是父组件分发的 vnode array.   
    对于具名 slot, 如果父组件对该 slot 分发了多次内容，则只使用最后一次分发内容  
    对于 default slot, 如果父组件通过 v-slot 指定了default， 则使用最后一个指定了 default 的分发内容，如果没有通过 v-slot 指定 default, 则所有未指定其它具名slot 的内容都会被分发到 default slot 里  
* <i>vm.$refs</i>, 存储所有在组件里 声明了 ref 的元素，包括原生 element 和自定义组件

4. Vue 实例常用的方法有什么？ 有什么需要注意的地方？
* <i>vm.$on</i>, 监听当前实例上的自定义事件，该事件通过 该实例的 vm.\$emit 触发，第一个参数是事件名，或者事件名数组，第二个参数是回调函数   
* <i>vm.$emit</i>, 触发当前实例上的事件，该事件可以被父组件监听， 第一个参数是事件名，后面的附加参数都会传递给回调函数。  
* <i>vm.$off</i>, 解除当前实例上绑定的监听器。如果没有提供参数，则解除所有的事件监听器。如果提供了事件名，则移除该事件的所有监听器。如果提供了事件名和回调，则只移除该回调  
* <i>vm.\$once</i>, 类似于 vm.$on, 但只监听一次，触发后则自动移除  
* <i>vm.$watch</i>,  第一个参数是一个 js 表达式，当表达式的值改变，触发回调。该方法返回一个 unwatch, 调用该 unwatch, 解除监听  
5. Vue 里常用的指令有什么？ 有什么需要注意的地方？  
* <i>v-bind</i>, 用于绑定组件参数或者原生元素属性到表达式，参数为元素属性或者组件定义的prop, 值为 js 表达式。当不指定参数时，值为一个对象。对象的键值对为参数名和对应的绑定的 js 表达式。相当于一次性将所有键值对绑定到当前元素上。 当v-bind 作用于 style 和 class 时，还可以接收对象和数组。v-bind 也支持动态参数，v-bind:[name]="expression". 缩写为 <code>:</code>  
* <i>v-on</i>, 用于注册事件监听器，参数为事件名，值为回调函数。用在原生元素上时，只能监听原生 DOM 事件， 且回调函数以事件为唯一参数。用在自定义组件上时，可以监听子组件的自定义事件。还可以指定事件修饰符， 比如 stop/prevent/native 等。 当不使用参数时，可以指定一个对象，该对象键值对为事件名和对应的回调函数(这里就不支持任何修饰器了), 相当于一次性将对象键值对绑定到当前元素上。 v-on 也支持动态参数， v-on:[event]="handler". 缩写为 <code>@</code>。   
* <i>v-slot</i>, 用于指定想分发内容的插槽，必须搭配 template 一起使用。参数为 slot name, 默认值是 default, 当指定 value， 该 value 表示由 slot 本身提供的数据(作用域插槽)， 比如 <code>\<template v-slot:header="slotProps"\></code>. v-slot 也支持动态参数， v-slot:[name]. 缩写为 <code>#</code>
* <i>v-model</i>, 用于在表单控件或者自定义组件上创建双向绑定。<code>\<comp v-model="foo"\/></code>. v-model 默认绑定组件或者元素的 <i>value</i> prop, 在子组件想更新 <i>value</i> 的值时，需要显示调用 <code>this.$emit('input', newValue)</code>. 父组件会监听该事件，并且将传递过来newValue 用来更新 foo, 从而更改了 子组件里的 value.  需要注意的是，可以更改 v-model 总是绑定 value prop 和 input event 的行为，在子组件里使用 model 选项，指定 prop 和 event, prop 是 v-model 想绑定的属性名， event 是v-model 想监听的事件名。相同的功能也可以通过 v-bind 来实现。 <code>v-bind:<i>prop</i>.sync="foo"</code>, 这里的 prop 可以是任意的 prop, 在子组件，通过触发 'update:<i>prop</i>' 事件来更新属性  
* <i>v-for</i>, 用于迭代对象，数组，或者整数。<code>\<div v-for="(item, index) in items"\></code>. 需要为每个迭代元素绑定 key 属性  
* <i>v-if</i>, <i>v-else</i>, <i>v-else-if</i>, 根据条件结果决定是否渲染元素。 v-if 与 v-for 同时使用与同一元素时，v-for 优先级更高  
* <i>v-show</i>, 根据条件结果切换元素的 display 属性  
## Class & Style
字符串的拼接麻烦且易错，所以 Vue 对class 和 style 绑定做了增强， 除了可以传入字符串之外，还可以传给 class 和 style 对象以及数组。 
1. 对象语法
   比如 <code>\<div :class="{active: isActive}"\></code>, 当 <i>isActive</i> 为真，该元素将具有 'active' class. 同时 v-bind:class 可以与普通的 class 赋值共存。比如 <code>\<div class="static" :class="{active: isActive, error: hasError}"></div></code>.  
   上述代码也可以写作
   ```js
   template: `<div :class="classObject"></div>`
   
   data: {
      classObject: {
        active: true,
        'text-danger': false
      }
   }
   ```
   把 classObject 写在 data 里，不太适合，所以通常将 class 绑定在一个计算属性对象上。
   ```js
   data() {
       return {
           active: true,
           error: null
       }
   },
   computed: {
       classObject() {
           return {
               active: this.active && !this.error,
               'text-danger': this.error && this.error.type === 'fatal'
           }
       }
   }
   ```
2. 数组语法 
当绑定数组时，<code>\<div :class="[activeClass, errorClass]"\></code>, 'activeClass' 的值将作为 class 被添加。同样的，在数组里，也可以使用对象语法。
```js
template: `<div :class="[activeClass, errorClass]"></div>`,
data() {
    return {
        activeClass: 'active',
        errorClass: 'text-danger'
    }
}
//在数组里使用对象语法
template: `<div :class="[{active: isActive}, errorClass]"></div>`,
```  
总结，当使用对象语法，对象 key 就是 class name, value 为 真值表达式，当表达式为真，则该key 会作为 class 被添加。  
当使用数组语法，数组元素的值，将作为 class 被添加。
3. 内联样式
v-bind:style 的对象语法十分直观，看起来像 CSS ，但实际上就是一个 JS 对象。 CSS 属性名可以用驼峰形式，或者 kebab-case(记得用引号括起来)
```js
template: `<div :style="{color: activeColor, fontSize: fontSize + 'em'}"></div>`,
data() {
    activeColor: 'blue',
    fontSize: 20
}
//数组语法也是类似的, 元素需要是 style 对象
template: `<div :style="[baseStyleObject, otherStyleObject]"></div>`
```
## 自定义组件 v-model
by default, v-model 会默认绑定表单元素的 *value* 属性, 当表单元素 value 改变时，表单元素会发出事件 *input*, 然后将最新的 value 发送出去。  
上层元素在接收到 *input* 事件时，就会用最新的 value 更新自己绑定的域， 表单元素自己渲染的 value 也就跟着更新了。 注意，在双向绑定的时候，表单元素并不会自己更新 value 值，而是将 value 值传递给上层元素，由上层元素修改自己绑定的域。  
对于自定义组件，也可以使用 v-model. 只需要在组件内指定 *model* 选项，该选项包含 *prop* 和 *event* 属性，*prop* 指定被外界 v-model 绑定的 prop 名，event 指定当参数改变时，需要触发的事件名。
```js
const counter = {
    template: `<button @click="add">{{count}}</button>`,
    model: {
        prop: 'count',
        event: 'add'
    },
    props:{
        count: {
            type: Number,
            default: 0
        }
    },
    methods: {
        add() {
            this.$emit('add', this.count + 1)
        }
    }
}

new Vue({
    template: `<counter v-model="count"></counter>`,
    data() {
        return {
            count: 1
        }
    },
    components: {
        counter
    }
})
```
## Component
1. 组件注册
对于全局组件(Vue.component)或者局部组件(一个 vue 实例对象)，组件名为驼峰时，或非 template 字符串里使用该组件时，必须使用 kebab-case。  
对于局部注册，引用时，必须在 components 里指明。  
组件里定义的 props,如果在非模板字符串里使用，必须使用 kebab-case  
```html
<div>
    <!-- 因为html 本身始终使用小写字母，所以这里对于 CC 组件必须使用 c-c -->
    <!-- 因为组件参数本身是驼峰形式，在 html 里也必须使用 msg-info 形式-->
    <!-- 如果使用 msgInfo, 那么该属性并不会被识别为 prop, 可以在 vm 实例里 通过 this.$attrs.msginfo(注意不是 msgInfo) 来访问-->
    <c-c msg-info="my message"/>
</div>
```
```js
const CaCa = {
  template: `<button @click="ff">Click</button>`,
  methods: {
  	ff() {
      console.log('caca emit')
      this.$emit('caca')
    }
  }
}

const BaBa = {
	template: `<CaCa/>`,
    components: {
  	CaCa
  }
}

const CC = {
    template: `<div>
        <BaBa @caca="ff"/>
        {{msgInfo}}
    </div>`,
    components: {
  	    BaBa
    },
    props: ['msgInfo']
    methods: {
  	    ff() {
    	    console.log('cccc accept')//CC 不能接收到 CaCa 组件发出的消息，消息传递只能在父子组件之间
        }
    }
}
new Vue({
	el: "#root",
    components: {
        CC//当然也可以在注入组件变量的时候，指定它的名字，比如 cc: CC，那么在模板或者 html 里就可以直接使用 cc
    }
})
```
2. 组件通信
* 父组件 -> 子组件， 通过 props 通信，数据传输是单向的，子组件不能更改 props
* 子组件 -> 父组件， 子组件通过 vm.$emit('name', arguments) 发出事件和参数，父组件通过 v-on:name="handler" 来响应事件
* 其它类型的通信， 可以使用一个 公共的 vm 实例作为 event bus，每个组件通过该 event bus $on, $emit, $off 事件。
3. 组件参数
组件参数的类型可以是任意 JS 类型， String, Number, Boolean, Array, Function, Object 等。还可以是自定义类型，通过 instanceof 来进行检查确认。    
在传递参数给子组件时，要注意区分动态参数和静态参数。
```js
template: `<blog title="hello vue.js"></blog>`,// 将静态字符串 hello vue.js 传递给 title 参数
template: `<blog title="post.title"></blog>`,//将静态字符串 post.title 传给 title 参数
template: `<blog :title="post.title"></blog>`,//将 JS 表达式 post.title 的值传递给 title 参数
```
我们可以在定义参数的时候，对该参数作出一些限制。
```js
Vue.component('my-component', {
  props: {
    // 基础的类型检查 (`null` 和 `undefined` 会通过任何类型验证)
    propA: Number,
    // 多个可能的类型
    propB: [String, Number],
    // 必填的字符串
    propC: {
      type: String,
      required: true
    },
    // 带有默认值的数字
    propD: {
      type: Number,
      default: 100
    },
    // 带有默认值的对象
    propE: {
      type: Object,
      // 对象或数组默认值必须从一个工厂函数获取
      default: function () {
        return { message: 'hello' }
      }
    },
    // 自定义验证函数
    propF: {
      validator: function (value) {
        // 这个值必须匹配下列字符串中的一个
        return ['success', 'warning', 'danger'].indexOf(value) !== -1
      }
    }
  }
})
```
如果传入的参数验证失败，开发环境下，控制台将产生 warning。  

有的时候，组件会传入一些不能被识别为 prop 的 attribute,  那么这些 attribute 将会默认添加到组件根元素上。  
如果根元素上本身已经有这些属性的定义，那么传入的属性值将替换根元素上定义的属性值。但是 class 和 style 例外， vue 会将它们合并而不是替换。  
如果想禁用 attribute 继承，那么可以在组件选项中设置 *inheritAttrs: false*. 一般，如果禁用的话，还会通过 vm.$attrs 来决定将这些额外的属性赋予哪个元素
   
## v-slot
Vue 允许通过父组件，将一些内容插入到子组件里。这就要求子组件在自己的模板定义里添加 *slot* 元素。子组件里 slot 内的内容是后备内容，当父组件没有提供插入的内容时，子组件就会使用 slot 里的后备内容。  
同样，也允许组件同时含有多个 slot 插槽，那么就需要给这些插槽提供名字。没有名字的 slot 默认使用 default 作为名字。
```xml
<!-- layout 组件-->
<div class="container">
  <header>
    <slot name="header"></slot>
  </header>
  <main>
    <slot></slot>
  </main>
  <footer>
    <slot name="footer"></slot>
  </footer>
</div>
```
在向具名插槽提供内容时，我们需要结合使用 *template* 元素以及 *v-slot* 命令。任何没有包裹在带有 v-slot 的 template 内容都将被视为默认插槽的内容。  
只有当子组件只提供了默认插槽，那么才可以不使用 template 元素，而是使用子组件标签作为模板。
```html
<layout>
  <template v-slot:header>
    <h1>Here might be a page title</h1>
  </template>

  <p>A paragraph for the main content.</p>
  <p>And another one.</p>

  <template v-slot:footer>
    <p>Here's some contact info{{msg}}</p>
  </template>
</layout>
```
需要注意，父组件里所有的内容都是在父级作用域中编译的，子模版的所有内容都是在子作用域里编译的。比如，上例中的 footer slot, msg 就是父组件里的数据。  
但是有的时候，我们可能不想要 slot 的默认内容，想自己渲染，但是又需要访问子组件里的数据，这怎么办呢？这时候就可以使用作用域插槽。  
在子组件定义 插槽时，就可以将自己的数据绑定在插槽上。 user 是子组件自己 own 的数据，现在将它绑定到 *default* 插槽上。
```html
<!-- user 组件-->
<span>
    <slot :user="user">
        {{user.lastName}}
    </slot>
</span>
```
绑定在 插槽上的属性称为 插槽prop, 现在在父级作用域里，我们可以使用带值的 v-slot 来获取绑定在对应 slot 上的数据。
```html
<user>
    <template v-slot:default="props"><!-- props 是任意取的名字，是一个对象，包含所有绑定到插槽的数据-->
        {{props.user.firstName}}
    </template>
</user>
```
作用域插槽的内部工作原理是将插槽内容包裹在一个拥有单个参数的函数里
```js
function(props) {
    //插槽内容
}
```
所以我们可以直接对 props 进行解构，或者对属性重命名，甚至设置默认值，都是允许的
```html
<user v-slot="{user}"><!-- 重命名， v-slot="{user: person}" 设置默认值 v-slot="{user = {firstName: 'yang', lastName: 'liu'}}"-->
    {{user.firstName}}
</user>
```
v-slot 的缩写为 *#*, 如果使用缩写，则后面必须跟上 slot 名字，即使是 default 插槽，也要写为 *#default*

## 过滤器
Vue 允许定义一些常见的文本格式化函数为*过滤器*，过滤器可以用在两个地方，双花括号插值和 v-bind 表达式。并且过滤器应该被添加到 JS 表达式的尾部，由管道符号指示，且可以级联。  
```html
<!-- 在双花括号里 -->
{{message | capitalize}}

<!-- 在 v-bind 里 -->
<div v-bind:msg="message | capitalize"></div>
```
可以在 Vue 实例构造选项里，直接添加 filters 选项定义局部过滤器。
```js
filters: {
    capitalize: function(value) {
        if (!value) return ''
        value = value.toString()
        return value.charAt(0).toUpperCase() + value.slice(1)
    }
}
```
也可以在构建 Vue 实例之前直接创建全局过滤器
```js
Vue.filter('capitalize', function(value) {
    //...
})
```
并且，过滤器是可以级联的，下一个过滤器的参数是上一个过滤器的结果。且过滤器的本质是一个函数，支持传入多个参数。
```js
//message 作为参数传给 filterA
//filter A 的结果作为第一个参数传给 filterB, 普通字符串'hello' 作为第二个参数， js 表达式 user 作为第三个参数
{{message | filterA | filterB('hello', user)}}
```
## render 函数
一般来讲，我们只需要指定 *template* 选项，由编译器帮我们编译出 render 函数就够了，这在大多数场景下是适用的。  
但是有的时候，模板并不是静态的，我们需要根据条件动态的渲染组件，而不是在模板里把所有情况都列举出来。  
```js
//这里的 createElement 更准确的名字应该是 createNodeDescription,
//它的返回值是 VNode, VNode 能够告诉 vue 该怎么创建真正的 DOM 节点
render: createElement => {
    return createElement('h' + this.level, this.$slots.default)
},
props: {
    level: {
        type: Number,
        required: true
    }
}
```
这样不管传入的 level 是多少，都能正确的渲染出来，并且把插槽内容正确的分发到 default 插槽，也避免了在模板里列举出所有的情况。  
### createElement
createElement 有三个参数，第一个参数是需要创建的元素或者组件，第二个参数是可选对象，配置各种参数，第三个参数是节点的子节点内容。
```js
//return VNode
createElement(
    'div',// string, component对象，或者resolve 上面任何一种的 async function
    // {Object}
    {

    },
    // {String | Array}
    [
        'this is title',
        createElement('h1', 'this is header'),
        createElement(MyComponent, {
            props: {
                propA: 'hello world'
            }
        })
    ]
)
```
#### 配置对象
```js
createElement(
    MyComponent,
    {
        class: {//与 v-bind:class 类似，支持 字符串，对象和数组语法
            foo: true,
            baz: this.error
        },
        style: {
            color: this.activeColor,
            fontSize: this.fontSize + 'px'
        },
        attrs: {//普通的 HTML attribute
            id: 'my-component'
        },
        props: {
            propA: this.message
        },
        domProps: {
            innerHTML: 'baz'
        },
        on: {//监听组件 MyComponent emit 的自定义 add 事件
            'add': this.handleClick
        },
        slot: 'name-of-slot',
        scopedSlots: {
            default: props => createElement('span', props.text)
        }
    })
```
#### 使用 JS 代替模板指令
对于 *v-for*, *v-if*, 就可以使用 JS array map 和 if 语句来直接实现其功能。  
对于 v-model, 就需要自己实现相应的逻辑。
```js
props: ['value'],
render: function (createElement) {
  var self = this
  return createElement('input', {
    domProps: {
      value: self.value
    },
    on: {
      input: function (event) {
        self.$emit('input', event.target.value)
      }
    }
  })
}
``` 
这就是深入底层的代价，但是可以更好的控制交互细节。
#### 事件&按键修饰符
```js
on: {
  keyup: function (event) {
    // 如果触发事件的元素不是事件绑定的元素则返回
    if (event.target !== event.currentTarget) return
    // 如果按下去的不是 enter 键或者
    // 没有同时按下 shift 键
    // 则返回
    if (!event.shiftKey || event.keyCode !== 13) return
    // 阻止 事件冒泡
    event.stopPropagation()
    // 阻止该元素默认的 keyup 事件
    event.preventDefault()
    // ...
  }
}
```
#### 插槽
*this.$slots* 对象 记录了从父组件传递过来的 每个slot 的内容，key 是 slot 名字，值是 vnode 数组。  
*this.$scopedSlots* 对象记录了生成父组件传递插槽内容的函数，key 是 slot 名字，值是 function, 该function 参数是作用域插槽绑定的数据，返回值是 vnode 数组.  
如果 *this.$slots.slotname* 或者 *this.$scopedSlots.slotname* 为空，表示父组件没有提供对应的插槽内容或者作用域插槽函数，则可以在渲染函数里自己通过 createElement 函数创建 slot default content.   
*scopeSlots* 是作用于渲染函数配置对象里的，其作用是配置对应的根据子组件插槽提供的参数生成 vnode 的函数。
```html
<!-- html -->
<div id="root">
    <time-counter></time-counter>
</div>
```
```js
//js
const counter = {
  data() {
  	return {
    	id: new Date()
    }
  },
	props: ['count'],
  model: {
  	prop: 'count',
    event: 'change'
  },
  methods: {
  	add() {
    	this.$emit('change', this.count + 1)
    }
  },
/*   template: `<div>
              <slot :time="id"><span>Counter:</span></slot>
              <button @click="add">{{count}}</button>
             </div>`, */
  render(h) {
  	let f = this.$scopedSlots.default
    let label = (f && f({time: this.id})) || h('span', 'Counter:')
    return h(
    	'div',
      [
      	label,
        h(
        	'button',
          {
          	on: {
            	'click': this.add
            }
          }, this.count
        )
      ]
    )
  }
}

const TimeCounter = {
	data() {
  	return {
    	init: 2
    }
  },
  components: {
  	counter
  },
/*   template: `<counter v-model="init">
      <template #default="{time}">
        {{time}}
      </template>
  </counter>`, */
  render(h) {
  	return h(
    	'counter',
      {
      	on: {
        	'change': value => this.init = value
        },
        props: {
        	count: this.init
        },
        scopedSlots: {
        	default({time}) {
          	return time.toString()
          }
        }
      }
    )
  }
}

new Vue({
	el: "#root",
  components: {
  	TimeCounter
  }
})
```
## 自定义指令
除了 Vue 内置的指令外，Vue 也支持自定义指令。虽然代码复用的主要形式是组件，但是有的情况下，你仍然需要对普通 DOM 元素进行底层操作，这个时候就需要用到自定义指令。  
指令分为全局指令和局部指令，全局指令通过 *Vue.directive('name', {...})* 来定义，局部指令可以在组件构建选项 *directives: {name: {...}}* 定义。注意，指令名字为 *name*, 那么使用的时候，是 *v-name*.  
指令的使用形式为 <code>v-name:prop.modifier="expression"</code>
### 钩子函数
一个指定定义对象可以提供如下几个钩子函数(均为 optional)
* bind, 只调用一次，指令第一次绑定到元素时调用。在这里可以进行一次性的初始化操作
* inserted, 在绑定元素插入到父节点时调用(仅保证父节点存在，但是不一定已经被插入到文档中)
* update, 所在的组件 VNode 更新时调用，但是可能发生在其子 VNode 更新之前。指令的值可能发生了改变，也可能没有。
* componentUpdated, 指令所在组件的 VNode 以及子 VNode 全部更新后调用
* unbind, 只调用一次，指令与元素解绑时调用

### 钩子函数参数
指令钩子函数会传入以下参数：
* el, 指令绑定的 DOM 元素，可以用来直接操作 DOM
* binding, 一个对象，包含了指令的所有属性
   - name: 指令的名字，不包括 *v-* 前缀
   - value: 指令 expression 的值，注意不是表达式，而是表达式的值，比如 v-my-directive="1 + 1", value 为 2
   - oldValue, 表达式之前的值，仅在 update, componentUpdated 钩子中可用
   - expression, 指令表达式
   - arg: 传给指令的参数，比如 v-my-directive:foo="1+1", arg 则为 *foo*
   - modifiers: 一个包含修饰符的对象，比如 v-my-directive.foo.bar, 则 modifiers 为 *{foo: true, bar: true}*
* vnode, 编译生成的 vnode 节点
* oldVnode, 上次编译生成的 vnode

除了 el 之外，其它参数都是只读的，切勿进行修改.

### 动态指令参数
指令所绑定的参数也可以是动态的，其形式为 <code>v-name:[prop].modifier="expression"</code>.  
比如，我们有一个指令 *v-pin="200"* 可用把绑定元素固定在页面上，但是这个 200 是作用域 top, left, 还是 right 等，就可以通过动态参数来决定。*v-pin:[direction]="200"*

