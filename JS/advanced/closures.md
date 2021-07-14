A closure give you access to an outer function's scope from an inner function.

And in JS, the closure is created every time a function is created, at function creation time.

```js
function makeFunc() {
  var name = 'Mozilla';
  function displayName() {
    alert(name);//inner function displayName could access outer function scope
  }
  return displayName;//return the inner function
}

var myFunc = makeFunc();
myFunc();//the inner function can still work as expected
```
简单的说，函数在创建的时候，会形成一个闭包，该闭包由该函数和声明该函数的上下文环境组成，函数可以访问该上下文环境里的变量。

对于普通函数，上下文即全局对象 window，对inner function， 上下文即其 outer function scope

# private method with closure
在 JS 里，可以通过 闭包实现类似于 Java private method的效果，这样不仅可以限制 access to code, 同时也提供了一个管理全局命名空间的方法。
```js
var counter = (function() {
  //privateCounter 和 changeBy 是该匿名函数的私有成员(不能被外界访问)
  var privateCounter = 0;
  function changeBy(val) {
    privateCounter += val;
  }
  //外界能访问的，只有返回对象的三个方法。且该三个方法共享同一份 上下文环境
  return {
    increment: function() {
      changeBy(1);
    },

    decrement: function() {
      changeBy(-1);
    },

    value: function() {
      return privateCounter;
    }
  };
})();

console.log(counter.value());  // 0.

counter.increment();
counter.increment();
console.log(counter.value());  // 2.

counter.decrement();
console.log(counter.value());  // 1.
```

# Closure Scope
Every closure has three scopes:
1. Local scope(own scope)
2. outer scope
3. global scope

```js
// global scope
var e = 10;
function sum(a){
  return function(b){
    return function(c){
      // outer functions scope
      return function(d){
        // local scope
        return a + b + c + d + e;//can access all outer scopes
      }
    }
  }
}

console.log(sum(1)(2)(3)(4)); // log 20
```
# Closure in loop
```html
<p id="help">Helpful notes will appear here</p>
<p>E-mail: <input type="text" id="email" name="email"></p>
<p>Name: <input type="text" id="name" name="name"></p>
<p>Age: <input type="text" id="age" name="age"></p>
```
```js
function showHelp(help) {
  document.getElementById('help').textContent = help;
}

function setupHelp() {
  var helpText = [
      {'id': 'email', 'help': 'Your e-mail address'},
      {'id': 'name', 'help': 'Your full name'},
      {'id': 'age', 'help': 'Your age (you must be over 16)'}
    ];

  for (var i = 0; i < helpText.length; i++) {
    var item = helpText[i];
    document.getElementById(item.id).onfocus = function() {
      showHelp(item.help);
    }
  }
}

setupHelp();
```
上面代码的问题在于，首先，用 `var` 声明了变量 item, 由于*hoisting*, 该变量变成 function scope variable. 然后每个元素绑定的 callback  function都是闭包，捕获了outer function 的上下文环境，即三个元素的 callback function 捕获的上下文环境是一样的，都含有变量 `var item`。当setupHelp 执行完成，item 变量指向了最后一个 helpText, 当回调执行时，就都会指向相同的 item.help。

为了解决上述问题，第一，可以简单使用 `let` 变量而不是 `var`, 这样就限制了变量 item 为块作用域，这样每个闭包捕获的就是块作用域而不是函数作用域的 item。

还可以通过 `helpText.forEach(function(text){})` 来绑定 callback，这样每个闭包捕获的，也会是局部变量 text