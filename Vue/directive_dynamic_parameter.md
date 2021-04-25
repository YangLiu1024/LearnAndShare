# 指令动态参数
有的指令能够传入参数，比如 v-bind, v-on, v-slot 等。 
* v-bind 的参数是子组件或者元素的属性，值为 JS 表达式
* v-on 的参数是事件名，值为 JS 函数
* v-slot 的参数是 slot name, 当赋予值的时候，该值表示插槽提供的参数， 比如 v-slot:default="slotProps". slotPros 是一个对象，包含了所有 default 插槽绑定的参数

since 2.6.0, 可以用方括号括起来的 JS 表达式作为一个指令的参数， 比如 <code><a v-bind:[attrName]="url"></code>, 这里的 <b>attrName</b> 就是一个 JS 表达式，它的值将作为 v-bind 的参数。 同样的， v-on, v-slot 也支持这样的语法。

动态参数预期会求出一个字符串，异常情况下为 null, 这个特殊的 null 值可以被显示的用于解除绑定。其它任意类型的值，将触发一个警告