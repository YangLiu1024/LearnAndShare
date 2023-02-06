# 函数形参与实参
* 形参是函数声明时，定义的参数名字，用来接收实参
* 实参是实际传递给函数的参数，实参可以和形参不一样
* 基础类型的实参是拷贝值到形参，引用类型是拷贝内存地址
* 函数可以使用 *arguments* 来访问传入的所有参数，箭头函数不具有 arguments
* 未匹配到的形参，会解析为 undefined

## arguments
只在函数级作用域里可访问，由实参决定初始化，是一个类数组对象，包含 length 属性，但是不包含其它数组方法。  
arguments 有一个特殊的属性 *callee*, 指向当前正在执行的函数本身，这在匿名函数里非常有用。  

## 变量提升与函数提升
* 通过 var 声明的变量，将会把声明(不包含定义)提升到当前作用域的最上面。  
* 函数声明及其定义会被提升到当前作用域的最上面，所以可以先调用函数，再定义函数。函数表达式并不会提升。
* 变量提升优先级更高，即变量提升会在函数提升之上
* 不要使用变量提升或者函数提升，变量声明只使用 let, const, 函数必须先声明，后使用
```js
foo()
function foo() {}

bar()
const bar = () => {}
```

## 闭包
在代码执行时，会构建一个上下文环境。当进入新的函数时，会创建一个新的上下文环境，当该函数执行结束，会销毁该上下文环境，然后在之前的上下文环境中继续执行。  
闭包就是下级上下文环境捕获了上级上下文中的变量引用，从而形成了闭包。  
闭包很多时候也会引入一些意想不到的错误
```html
<ul>
    <li></li>
    <li></li>
    <li></li>
    <li></li>
</ul>
```
```js
const lis = document.getElementsByTagName('ul')[0].children;
for (var i = 0; i < lis.length; i ++) {
    lis[i].onclick = function() {
        console.log(i)
    }
}
```
上述代码的问题在哪儿呢？不管点击哪个 li, 输出的都是 4.   
这是因为，首先，i 使用 var 声明，不同迭代都会使用相同的 i 变量。其次，每一次迭代，都会创建一个新的函数，该函数会捕获外部变量 i 的引用。当 for 循环结束，i 的值是 4，当用户点击 li 的时候，根据 i 的引用来 output 时，就都会输出 4 了。  
那么怎么解决这个问题呢？  
第一种方法，使用 *let* 来声明变量。根据 ES6, 由 *let* 声明的变量，在不同迭代里，都会得到不同的实例，由此，闭包捕获的，其实是不同的实例，自然就不会有问题。当然，有的浏览器版本不支持该定义。  
```js
const lis = document.getElementsByTagName('ul')[0].children;
for (let i = 0; i < lis.length; i ++) {
    lis[i].onclick = function() {
        console.log(i)
    }
}
```
第二种，就是通过值和 index 的方式来遍历 HTMLCollection  
第三种，就是通过函数调用时，对于基本类型，形参是实参的值的拷贝的原理来实现
```js
const lis = document.getElementsByTagName('ul')[0].children;
for (var i = 0; i < lis.length; i ++) {
    (function(index) {
        // 函数捕获了外界变量 lis 的引用，形成了闭包
        // 但因为函数是自执行函数，并没有变量持有函数引用，函数执行一次后，就会被回收
        lis[index].onclick= function() {
            console.log(index)
        }
    })(i)
}
```
还有一种常见的问题是 for 循环和 setTimeout 同时调用
```js
const arr = ['1', '2', '3']
for (var i = 0; i < arr.length; i++) {
    setTimeout(() => console.log(arr[i]), i * 1000)
}
```
同样的，当 setTimeout 包裹的函数体执行时，for 循环已经结束，i 变量的值是 3，这个时候 arr[3] 都会返回 undefined.  
为例解决这个问题，最简单的就是使用 let, 而不是 var.

## Call vs Apply vs Bind
```js
func.call(obj, arg1, arg2, ...) // 将函数作用于 obj
func.apply(obj, [args]) // 功能与 call 类似，只是调用方式不一样，参数是以数组的形式
func.bind(obj, arg1, arg2, ...) // 返回一个新的函数对象，该函数对象绑定了 obj 作为 this, 且参数也可以被部分绑定或者全部绑定
```

