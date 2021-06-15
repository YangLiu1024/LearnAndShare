# Questions for Interview

## Basic knowledge about Vue
1. Vue 完整版和运行时版本有什么区别？ 开发环境版本和生产环境版本有什么区别？
   
   完整版包含了编译器和运行时，编译器用于编译 <i>template</i> 模板为 render 函数，运行时用于创建 vue 实例，渲染 DOM树等。

   生产环境相对于开发版本，就是删掉了只用于开发环境的 code, 并进行一定的压缩。
2. Vue 实例的常用选项有什么？用处是什么？有什么需要注意的地方？

    常用的选项有 <i>el</i>, 用于指定挂载的DOM 元素，一般为 CSS 选择器， 渲染出来的结果， vm.$el, 将替换指定元素的内容

    <i>data</i>, 实例的数据对象， 当用于组件时，必须为函数

    <i>props</i>, 组件可以接收的参数，由子组件定义，父组件传入。 props 的定义可以由 type/required/default/validator

    <i>template</i>, 定义组件模板，只能有一个根元素。(vue3 可以有多个根元素，但必须user自己指定父组件传入的 attrs 该由哪个根元素获取)

    <i>computed</i>, 定义计算属性，可以定义 getter 和 setter

    <i>methods</i>, 定义组件方法

    <i>render</i>, 定义 render 函数，如果该选项存在，则会忽略 <i>template</i> 选项

    <i>watch</i>, 是一个对象，key 是需要监听的 js 表达式，value 是回调function， 一般用于监听父组件传递的 props 的变化， 在实例初始化时，对该对象键值对，依次调用 vm.$watch.  并且回调函数不能使用箭头函数。

    <i>components</i>, 定义该组件引用的其它组件

    <i>beforeCreate</i>, <i>created</i>, <i>beforeMount</i>, <i>mounted</i>, <i>beforeUpdate</i>, <i>updated</i>, <i>beforeDestory</i>, <i>destoryed</i>, 生命周期 hook. 
    
    beforeCreate 在完成实例初始化之后，执行数据观测，事件配置之前调用。完成数据观测，事件配置后，执行 created. 这时候还没开始挂载。如果发现有 el选项，则进入 compile 阶段，开始编译 template. 如果没有， 则等待 invoke vm.$mount(), 触发 $mount() 后，进入编译阶段。 编译完成后，调用 beforeMount, 这个时候 vm.$el 还没有被更新，仍不可用。在 vm.$el 创建完成之后，并替换了 el 指定的元素内容之后，调用 mounted. 之后则进入等待用户输入的阶段，当用户更改了视图，数据改变，则调用 beforeUpdate。 当视图更新完成后，调用 updated, 并循环该流程。当组件要被销毁前，调用 beforeDestory, 销毁阶段，清除所有的数据观测，事件配置和所有的子组件。销毁完成，调用 destroyed.

    注意当 mounted 和 updated 调用时，并不能保证该组件的所有子组件都已经被mounted 或者 updated.

3. Vue 实例的常用属性有什么？有什么需要注意的地方？

    <i>vm.\$data</i>, vue 实例的数据对象, vm.$data.key 等价于 vm.key. 当直接添加/删除 键值对时，并不是响应式的，需要使用 Vue.set 和 Vue.delete

    <i>vm.$props</i>, vue 实例实际接收到的参数，可以通过 v-bind="\$props" 传递给子组件

    <i>vm.\$attrs</i>, 包含了父作用域里不作为 prop 被识别且获取的 attribute 绑定(class 和 style 除外)，可以在内部 通过 v-bind="$attrs" 传递给子组件

    <i>vm.$el</i>, vue 实例编译出来的 DOM 根元素

    <i>vm.$parent</i>, 该 vue 实例的 父实例

    <i>vm.$root</i>, 该 vue 实例组件树上的根 vue 实例

    <i>vm.$children</i>, 该 vue 实例的直接子组件数组，该属性并不保证顺序，也不是响应式的

    <i>vm.$slots</i>, 该 vue 实例被父组件分发的 slot 节点， 该属性是一个对象，key 是 slot name, value 是父组件分发的 vnode array. 

    对于具名 slot, 如果父组件对该 slot 分发了多次内容，则只使用最后一次分发内容

    对于 default slot, 如果父组件通过 v-slot 指定了default， 则使用最后一个指定了 default 的分发内容，如果没有通过 v-slot 指定 default, 则所有未指定其它具名slot 的内容都会被分发到 default slot 里

    <i>vm.$refs</i>, 存储所有在组件里 声明了 ref 的元素，包括原生 element 和自定义组件

4. Vue 实例常用的方法有什么？ 有什么需要注意的地方？
   
   <i>vm.$on</i>, 监听当前实例上的自定义事件，该事件通过 该实例的 vm.\$emit 触发，第一个参数是事件名，或者事件名数组，第二个参数是回调函数 

   <i>vm.$emit</i>, 触发当前实例上的事件，该事件可以被父组件监听， 第一个参数是事件名，后面的附加参数都会传递给回调函数。

   <i>vm.$off</i>, 解除当前实例上绑定的监听器。如果没有提供参数，则解除所有的事件监听器。如果提供了事件名，则移除该事件的所有监听器。如果提供了事件名和回调，则只移除该回调

   <i>vm.\$on</i>, 类似于 vm.$on, 但只监听一次，触发后则自动移除

   <i>vm.$watch</i>,  第一个参数是一个 js 表达式，当表达式的值改变，触发回调。该方法返回一个 unwatch, 调用该 unwatch, 解除监听

5. Vue 里常用的指令有什么？ 有什么需要注意的地方？
   
   <i>v-bind</i>, 用于绑定组件或者原生元素属性，参数为元素属性或者子组件定义的prop, 值为 js 表达式。当不指定参数时，值为一个对象。对象的键值对为参数名和对应的绑定的 js 表达式。当v-bind 作用于 style 和 class 时，还可以接收对象和数组。 缩写为 <code>:</code>

   <i>v-on</i>, 用于注册事件监听器，参数为事件名，值为回调函数。用在原生元素上时，只能监听原生 DOM 事件， 且回调函数以事件为唯一参数。用在自定义组件上时，可以监听自定义事件。
   当不使用参数时，可以指定一个对象，该对象键值对为事件名和对应的回调函数。缩写为 <code>@</code>。 还可以指定事件修饰符， 比如 stop/prevent/native 等

   <i>v-slot</i>, 用于指定想分发内容的插槽，必须搭配 template 一起使用。参数为 slot name, 默认值是 default, 当指定 value， 该 value 表示由 slot 本身提供的数据(作用域插槽)， 比如 <code>\<template v-slot:header="slotProps"\></code>

   <i>v-model</i>, 用于在表单控件或者自定义组件上创建双向绑定。<code>\<comp v-model="foo"\/></code>. v-model 默认绑定组件或者元素的 <i>value</i> prop, 在子组件想更新 <i>value</i> 的值时，需要显示调用 <code>this.$emit('input', newValue)</code>. 父组件会监听该事件，并且将传递过来newValue 用来更新 foo, 从而更改了 子组件里的 value.

   需要注意的是，可以更改 v-model 总是绑定 value prop 和 input event 的行为，在子组件里使用 model 选项，指定 prop 和 event, prop 是 v-model 想绑定的属性名， event 是v-model 想监听的事件名。

   相同的功能也可以通过 v-bind 来实现。 <code>v-bind:<i>prop</i>.sync="foo"</code>, 这里的 prop 可以是任意的 prop, 在子组件，通过触发 'update:<i>prop</i>' 事件来更新属性

   <i>v-for</i>, 用于迭代对象，数组，或者整数。<code>\<div v-for="(item, index) in items"\></code>. 需要为每个迭代元素绑定 key 属性

   <i>v-if</i>, <i>v-else</i>, <i>v-else-if</i>, 根据条件结果决定是否渲染元素。 v-if 与 v-for 同时使用与同一元素时，v-for 优先级更高

   <i>v-show</i>, 根据条件结果切换元素的 display 属性

## Furthur concept of Vue

1. Class 绑定
   
   Vue 对class 和 style 绑定做了增强， 可以传给 class 和 style 对象以及数组。
   
   比如 <code>\<div :class="{active: isActive}"\></code>, 当 <i>isActive</i> 为真，该元素将具有 'active' class. 同时 v-bind:class 可以与普通的 class 赋值共存。 通常可以将 class 绑定在一个计算属性对象上。

   当绑定数组时，<code>\<div :class="[activeClass, errorClass]"\></code>, 'activeClass' 的值将作为 class 被添加。 同样的，在数组里，也可以使用对象语法。

   总结，当使用对象语法，对象 key 就是 class name, value 为 真值表达式，当表达式为真，则该key 会作为 class 被添加。

   当使用数组语法，数组元素的值，将作为 class 被添加。

2. Style 绑定
   
   内联语法， <code>\<div v-bind:style="{color: activeColor, fontSize: fontSize + 'px'}"\></code>， CSS 属性名可以使用驼峰， 或者括号括起来的 kebab-case

   也可以直接绑定在一个样式对象， <code>\<div v-bind:style="styleObject"\></code>, 通常与计算属性一起使用

   当使用数组时，可以将多个样式对象应用到同一个元素上。 <code>\<div v-bind:style="[styleObject, otherStyleObject]"\></code>

3. 组件注册
   组件名为驼峰时，在非 template 字符串里使用该组件时，必须使用 kebab-case
   
   局部注册，引用时，必须在 components 里指明。

   组件里定义的 props,如果在非模板字符串里使用，必须使用 kebab-case

   当给 prop 赋值的时候，prop="value" 和 v-bind:prop="value" 的区别
4. 组件通信
5. 作用域插槽
6. 递归组件
7. 混入
8. 自定义指令
9.  渲染函数
10. 插件
11. 过滤器