# Uniform
uniform 用于存储 GLSL 中使用的全局变量  

每一个 Uniform 必须包括一个 value 属性。 value 的类型必须和 GLSL 的基本类型相对应。GLSL 基本类型队列必须要么被声明为一个 THREE 对象的队列，要么被声明为一个包含所有对象数据的队列。  
这就是说，队列中的 GLSL 基础类型不能再是一个队列。举例来说，一个有 5 个 vec2 元素的队列，必须是一个包含 5 个 vector2 的队列数组，或者一个 包含 10 个 number 的队列。
```js
Uniform(value: Object)

new Uniform(new Vector2())
```
## 属性 
* value, 该 uniform 的数据
  

## 方法
* clone() 